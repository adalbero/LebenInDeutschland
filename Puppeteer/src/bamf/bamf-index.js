const puppeteer = require('puppeteer');
const fs = require('node:fs');
const crypto = require('node:crypto');

const RUN = 'run-2026-02-15';
const OFFSET = 0;

const DELAY = 1_000;

const URL = 'https://oet.bamf.de/ords/oetut';

const BUNDESLANDS = {
    '': '300',

    'Brandenburg': 'BB',
    'Berlin': 'BE',
    'Baden-Württemberg': 'BW',
    'Bayern': 'BY',

    'Bremen': 'HB',
    'Hessen': 'HE',
    'Hamburg': 'HH',
    'Mecklenburg-Vorpommern': 'MV',

    'Niedersachsen': 'NI',
    'Nordrhein-Westfalen': 'NW',
    'Rheinland-Pfalz': 'RP',
    'Schleswig-Holstein': 'SH',

    'Saarland': 'SL',
    'Sachsen': 'SN',
    'Sachsen-Anhalt': 'ST',
    'Thüringen': 'TH'
};

const data = {
    name: 'BAMF',
    url: URL,
    timestamp: new Date().toISOString().substring(0, 19).replace(/[:\/,]+/g, '-'),
    questions: [],
}

const filename = `./data/${RUN}/data-${data.name}-${data.timestamp}.json`;

async function main() {
    const browser = await puppeteer.launch({ headless: false, timeout: 30_000 });
    const page = await browser.newPage();
    await page.setViewport({ width: 1080, height: 1024 });
    page.setDefaultTimeout(5_000);

    console.log('==> SAVE to file: ' + filename);
    await saveData(filename, data);

    try {
        const list = [...Object.keys(BUNDESLANDS)];
        for (let bundesland of list) {
            await fetchQuestions(page, bundesland);
        }

        console.log('==> COLLECT success');
    } catch (error) {
        console.error(error.message ?? 'Aborted');
        console.error(error);
        console.log('==> COLLECT failure');
    }

    await delay(1_000);
    console.log('==> CLOSE browser');
    await browser.close();

    console.log('==> END');
}

async function fetchQuestions(page, bundesland) {
    const start = bundesland ? 0 : OFFSET;
    const end = bundesland ? 10 : 300;

    console.log(`==> FETCH questions for bundesland: ${bundesland ? bundesland : 'all'}. Start: ${start}, End: ${end}`);
    await selectBundesland(page, bundesland, start);

    for (let i = start; i < end; i++) {
        await clickNext(page);

        const q = await parseQuestion(page);
        q.bundesland = bundesland;
        if (bundesland) {
            const code = BUNDESLANDS[bundesland];
            const num = (parseInt(q.number) - 300);
            q.number = `${code}-${num}`;
        }
        await appendQuestion(filename, q);
    }
}

async function delay(time = 500) {
    console.log('==> delay: ' + time);
    await new Promise(resolve => setTimeout(resolve, time));
}

async function parseQuestion(page) {
    const q = {
        number: await getQuestionNumber(page),
        title: await getImageHash(page),
        options: await getOptions(page),
        correct: await getResponse(page)
    };
    console.log('--> Processed:', q.number);
    return q;
}

async function selectBundesland(page, bundesland, start = 0) {
    await page.goto(URL + '/f?p=514:1::::::');
    console.log('==> select bundesland: ' + (bundesland ? bundesland : 'all'));
    if (bundesland) {
        await page.type('#P1_BUL_ID', bundesland, { delay: 120 });
        await clickNext(page);
        await openQuestion(page, '300');
    } else if (start > 0) {
        await clickNext(page);
        await openQuestion(page, start.toString());
    }
}

async function clickNext(page) {
    await delay(DELAY);
    const START = 'input[value="Zum Fragenkatalog"]';
    const NEXT = 'input[value="nächste Aufgabe >"]';
    if ((await page.$(NEXT)) != null) {
        console.log('==> click next');
        await page.locator(NEXT).click();
    } else if ((await page.$(START)) != null) {
        console.log('==> click start');
        await page.locator(START).click();
    }
}

async function getQuestionNumber(page) {
    const title = await getText(page, 'table[aria-live="polite"] > tbody > tr:nth-child(1) > td');
    return /Aufgabe (\d+) von/.exec(title || '0')?.[1] ?? '0';
}

async function getOptions(page) {
    return await page.$$eval('TD[headers="ANTWORT"]', (handle) => handle.map((el) => el.textContent));
}

async function getResponse(page) {
    await page.locator('TD[headers="CHECKBOX"]').click();
    return await page.$$eval('TD[headers="RICHTIGE_ANTWORT"]', (handle) => handle.map((el, index) => el.textContent.startsWith('richtige')).indexOf(true));
}

async function getText(page, locator) {
    const textSelector = await page.locator(locator).waitHandle();
    const text = await textSelector?.evaluate(el => el.textContent);
    return text?.trim() ?? '';
}

async function openQuestion(page, text) {
    await delay(DELAY);
    console.log('==> select question: ' + text);
    await page.type('#P30_ROWNUM', text, { delay: 120 });
}

async function getImageHash(page) {
    if (await page.$('.formlayout img') != null) {
        const img = await page.$eval('.formlayout img', (img) => img.src);
        const hash = crypto.createHash('md5');

        const buff = await fetch(img).then(res => res.arrayBuffer());
        const data = new Uint8Array(buff);

        try {
            hash.update(data);
        } catch (e) {
            console.error(e);
        }

        return 'MD5:' + hash.digest('hex');
    } else {
        return getText(page, '.formlayout SPAN.display_only');
    }
}

async function appendQuestion(filename, question) {
    try {
        const num = question.number;
        console.log(`==> APPEND question: ${num}`);
        const str = JSON.stringify(question, null, 2) + ',\n';
        fs.appendFileSync(filename, str);
        console.log('==> APPEND success');
    } catch (error) {
        console.error('==> APPEND failure');
        console.error(error);
    }
}

async function saveData(filename, data) {
    try {
        const str = JSON.stringify(data, null, 2) + ',\n';
        fs.writeFileSync(filename, str);
        console.log('==> SAVE success');
    } catch (error) {
        console.error('==> SAVE failure');
        console.error(error);
    }
}

(async () => {
    await main();
})();
