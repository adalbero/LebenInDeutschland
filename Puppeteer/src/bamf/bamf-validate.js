const fs = require('node:fs');

const RUN = 'run-2026-02-15';
const FILENAME = `./data/${RUN}/data-BAMF-026-02-15.json`;

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

async function main() {
    try {
        const data = await loadData(FILENAME);

        const list = Object.keys(BUNDESLANDS);
        for (let i = 1; i <= 460; i++) {
            const current = data.questions[i - 1].number;
            let expected = i.toString();
            if (i > 300) {
                const idx = Math.ceil((i - 300) / 10);
                const code = BUNDESLANDS[list[idx]];
                expected = `${code}-${i - 300 - (idx - 1) * 10}`;
            }
            assert(`question number`, expected, current);
        }
        console.log('==> VALIDATE success');
    } catch (error) {
        console.error(error.message);
        console.log('==> VALIDATE failed!!!');
    }
}

function assert(message, expected, current) {
    const success = expected === current;
    if (success) {
        console.log(`Assert ${message}: success. Expected: ${expected}, Current: ${current}`);
    } else {
        throw new Error(`Assert ${message}: failure. Expected: ${expected}, Current: ${current}`);
    }

}

async function loadData(filename) {
    const content = await fs.promises.readFile(filename, 'utf-8');
    return JSON.parse(content);
}

(async () => {
    await main();
})();
