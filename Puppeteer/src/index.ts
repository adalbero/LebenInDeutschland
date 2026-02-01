import puppeteer, { Page } from 'puppeteer';
const crypto = require('node:crypto');
const fs = require('node:fs');

const OFFSET = 0;
const SIZE = 300;
const DELAY = 1_000;

interface Question {
    number: string;
    title: string;
    options: string[];
    correct: number;
    bundesland?: string;
}

interface Bundesland {
    [key: string]: string;
}

const bundeslands: Bundesland = {
    // '': '300',

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

const questions: Question[] = [];
const URL = 'https://oet.bamf.de/ords/oetut/';
const NAME = 'BAMF';

(async () => {
    const browser = await puppeteer.launch({ headless: false, timeout: 30_000 });
    const page = await browser.newPage();
    await page.setViewport({ width: 1080, height: 1024 });
    page.setDefaultTimeout(5_000);

    try {
        // const list = ['', ...Object.keys(bundeslands)];
        const list = [...Object.keys(bundeslands)];
        for (let bundesland of list) {
            await fetchQuestions(page, bundesland);

            const data = {
                name: NAME,
                url: URL,
                timestamp: new Date().toISOString(),
                questions
            }

            const filename = `data/data-${data.timestamp.substring(0, 10)}-${data.name}.json`;
            fs.writeFileSync(filename, JSON.stringify(data, null, 2));
            console.log(`==> Saved ${data.questions.length} questions to ${filename}`);
        }
        // console.log(data);
    } catch (e: any) {
        console.error(e.message);
        console.error(e);
    }

    console.log('==> END');

    // await browser.close();
})();

async function fetchQuestions(page: Page, bundesland: string) {
    const n = bundesland ? 10 : 300;
    const start = OFFSET * SIZE;
    const end = Math.min(n, SIZE);

    await selectBundesland(page, bundesland);

    for (let i = start; i < end; i++) {
        await clickNext(page);

        const q = await parseQuestion(page);
        q.bundesland = bundesland;
        if (bundesland) {
            const code = bundeslands[bundesland];
            const num = (parseInt(q.number) - 300);
            q.number = `${code}-${num}`;
        }
        questions.push(q);
    }
}
async function delay(time: number = 500) {
    console.log('==> delay: ' + time);
    return new Promise(function (resolve) {
        setTimeout(resolve, time);
    })
}

async function parseQuestion(page: Page): Promise<Question> {
    const q: Question = {
        number: await getQuestionNumber(page),
        title: await getImageHash(page),
        options: await getOptions(page),
        correct: await getResponse(page)
    };
    console.log('--> Processed:', q.number);
    return q;
}

async function selectBundesland(page: Page, text: string) {
    await page.goto(URL + '/f?p=514:1::::::');
    console.log('==> select bundesland: ' + text);
    await page.type('#P1_BUL_ID', text, { delay: 120 });
    if (text) {
        await clickNext(page);
        await openQuestion(page, '300');
    }
}

async function clickNext(page: Page) {
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

async function getQuestionNumber(page: Page): Promise<string> {
    const title = await getText(page, 'table[aria-live="polite"] > tbody > tr:nth-child(1) > td');
    return /Aufgabe (\d+) von/.exec(title || '0')?.[1] ?? '0';
}

async function getOptions(page: Page) {
    return await page.$$eval('TD[headers="ANTWORT"]', (handle: any) => handle.map((el: any) => el.textContent));
}

async function getResponse(page: Page) {
    await page.locator('TD[headers="CHECKBOX"]').click();
    return await page.$$eval('TD[headers="RICHTIGE_ANTWORT"]', (handle: any) => handle.map((el: any, index: number) => el.textContent.startsWith('richtige')).indexOf(true));
}

async function getText(page: Page, locator: string): Promise<string> {
    const textSelector = await page.locator(locator).waitHandle();
    const text = await textSelector?.evaluate(el => el.textContent);
    return text as string;
}

async function openQuestion(page: Page, text: string) {
    await delay(DELAY);
    console.log('==> select question: ' + text);
    await page.type('#P30_ROWNUM', text, { delay: 120 });
}

async function getImageHash(page: Page) {
    if (await page.$('.formlayout img') != null) {
        const img = await page.$eval('.formlayout img', (img: any) => img.src);
        const hash = crypto.createHash('md5');

        const buff: ArrayBuffer = await fetch(img).then(res => res.arrayBuffer());
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

