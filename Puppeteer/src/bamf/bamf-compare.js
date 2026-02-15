const fs = require('node:fs');
const { get } = require('node:http');

const RUN = 'run-2026-02-15';
const ROOT = `./data/${RUN}`;
const BOT_FILE = `${ROOT}/data-BAMF-2026-02-15.json`;
const LID_FILE = `${ROOT}/question_list_old.csv`;
const BOOK_THEMES_FILE = `./data/i-punkt-2024/2024-book-themes.txt`;
const BOOK_AREAS_FILE = `./data/i-punkt-2024/2024-book-areas.txt`;

function main() {

    compareQuestions();

    console.log('==> END');
}

function getBookThemes() {
    const areas = getBookAreas();

    let lines = readLines(BOOK_THEMES_FILE);
    lines = lines.slice(3);

    const themes = [];
    for (let i = 0; i < lines.length;) {
        const theme = lines[i++].trim();
        const questions = lines[i++].trim().split(',').map((num) => num.trim());
        const size = lines[i++].trim();
        const area = areas.find((a) => a.theme === theme);

        themes.push({
            area_code: area?.area_code,
            area: area?.area,
            theme,
            questions,
            size,
        });
    }

    return themes;
}

function getBookAreas() {
    let lines = readLines(BOOK_AREAS_FILE);

    const areas = [];
    for (let i = 0; i < lines.length; i++) {
        const record = lines[i].split(';');
        areas.push({
            area_code: record[0].trim(),
            area: record[1].trim(),
            theme: record[2].trim(),
        });
    }

    return areas;
}

function readLines(filename) {
    const data = fs.readFileSync(filename, 'utf-8');
    return data.split('\n').filter((line) => line.trim());
}

function compareQuestions() {
    const header = 'num;question;a;b;c;d;solution;area_code;area;theme;image;Tags';
    const keys = ['num', 'question', 'a', 'b', 'c', 'd', 'solution', 'area_code', 'area', 'theme'];
    const lines = [header];
    const dataLid = [];
    const dataBot = [];

    const bookThemes = getBookThemes();
    const botQuestions = getBotQuestions(bookThemes);
    const lidQuestions = getLidQuestions();

    const changelog = [];
    const n = lidQuestions.length;
    for (let i = 0; i < n; i++) {
        let diff = undefined;

        const lid = lidQuestions[i];
        const bot = botQuestions[i];

        const lidD = reduce(lid, keys);
        const botD = reduce(bot, keys);

        if (lid.num !== bot.num) {
            throw new Error('Data mismatch');
        }

        if (bot.question?.startsWith('MD5')) {
            botD.question = lidD.question;
        } else {
            if (lid.question !== bot.question) {
                lid.question = bot.question;
                diff = 'question text';
            }
        }

        if (lid.a !== bot.a) {
            lid.a = bot.a;
            diff = 'option A';
        }

        if (lid.b !== bot.b) {
            lid.b = bot.b;
            diff = 'option B';
        }

        if (lid.c !== bot.c) {
            lid.c = bot.c;
            diff = 'option C';
        }

        if (lid.d !== bot.d) {
            lid.d = bot.d;
            diff = 'option D';
        }

        if (lid.solution !== bot.solution) {
            lid.solution = bot.solution;
            diff = 'solution';
        }

        if (i < 300 && lid.theme !== bot.theme) {
            lid.area_code = bot.area_code;
            lid.area = bot.area;
            lid.theme = bot.theme;
            diff = 'theme';
        }

        if (diff) {
            const change = {
                diff: `diff on ${diff}`,
                num: lid.num,
                lid: lidD,
                bot: botD,
            }
            changelog.push(change);
        }

        const line = Object.values(lid).join(';');
        lines.push(line);

        dataLid.push(lidD);
        dataBot.push(botD);
    }

    console.log(`==> total lid: ${lidQuestions.length} / total bot: ${botQuestions.length}`);
    console.log(`==> diffs: ${changelog.length} of ${n}: ` + JSON.stringify(changelog, null, 2));

    lines.push('');

    fs.writeFileSync(`${ROOT}/question_list_new.csv`, lines.join('\n'));
    fs.writeFileSync(`${ROOT}/dataLid.json`, JSON.stringify(dataLid, null, 2));
    fs.writeFileSync(`${ROOT}/dataBot.json`, JSON.stringify(dataBot, null, 2));
    fs.writeFileSync(`${ROOT}/dataChangelog.json`, JSON.stringify(changelog, null, 2));
}

function getLidQuestions() {
    const lidData = fs.readFileSync(LID_FILE, 'utf-8');
    const result = lidData.split('\r\n').slice(1).filter((line) => line.trim()).map((line) => {
        const vet = line.split(';');
        return {
            num: vet[0],
            question: vet[1],
            a: vet[2]?.trim(),
            b: vet[3]?.trim(),
            c: vet[4]?.trim(),
            d: vet[5]?.trim(),
            solution: vet[6],
            area_code: vet[7],
            area: vet[8],
            theme: vet[9],
            image: vet[10],
            tags: vet[11]
        }
    });

    return result;
}

function getBotQuestions(bookThemes) {
    const botData = fs.readFileSync(BOT_FILE, 'utf-8');
    const botJson = JSON.parse(botData);
    return botJson.questions.map((q) => {
        const theme = getBookTheme(bookThemes, q.number);
        return {
            num: q.number,
            question: q.title,
            a: q.options[0].trim(),
            b: q.options[1].trim(),
            c: q.options[2].trim(),
            d: q.options[3].trim(),
            solution: 'abcd'.charAt(q.correct),
            area_code: theme?.area_code,
            area: theme?.area,
            theme: theme?.theme,
        }
    });
}

function getBookTheme(bookThemes, num) {
    return bookThemes.find((t) => t.questions.includes(num));
}

function reduce(obj, keys) {
    if (keys) {
        return Object.keys(obj).reduce(function (acc, k) {
            if (keys.includes(k)) {
                acc[k] = obj[k];
            }
            return acc;
        }, {});
    } else {
        return obj;
    }
}

main();
