const fs = require('node:fs');

const LID_FILE = 'data/question_list_old.csv';
const BOT_FILE = 'data/data-2024-07-07-BAMF.json';

interface Rec {
    num: string;
    question?: string;
    a: string;
    b: string;
    c: string;
    d: string;
    solution: string;
    area_code?: string;
    area?: string;
    theme?: string;
    image?: string;
    tags?: string;
}

function main() {
    const lidQuestions = getLidQuestions();
    const botQuestions = getBotQuestions();

    update(lidQuestions, botQuestions)
    console.log('==> END');
}

function update(lidQuestions: Rec[], botQuestions: Rec[]) {
    const header = 'num;question;a;b;c;d;solution;area_code;area;theme;image;Tags';
    const keys = ['num', 'question', 'a', 'b', 'c', 'd', 'solution'];
    const lines: string[] = [header];
    const dataLid: Rec[] = [];
    const dataBot: Rec[] = [];

    let count = 0;
    const n = lidQuestions.length;
    for (let i = 0; i < n; i++) {
        let diff = false;
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
                diff = true;
            }
        }

        if (lid.a !== bot.a) {
            lid.a = bot.a;
            diff = true;
        }

        if (lid.b !== bot.b) {
            lid.b = bot.b;
            diff = true;
        }

        if (lid.c !== bot.c) {
            lid.c = bot.c;
            diff = true;
        }

        if (lid.d !== bot.d) {
            lid.d = bot.d;
            diff = true;
        }

        if (lid.solution !== bot.solution) {
            lid.solution = bot.solution;
            diff = true;
        }

        if (diff) {
            console.log('==> ' + lid.num);
            count++;
        }

        const line = Object.values(lid).join(';');
        lines.push(line);

        dataLid.push(lidD);
        dataBot.push(botD);
    }

    console.log(`==> total ${lidQuestions.length} / ${botQuestions.length}`)
    console.log(`==> ${count} of ${n}`)

    lines.push('');

    fs.writeFileSync(`data/question_list_new.csv`, lines.join('\n'));
    fs.writeFileSync(`data/dataLid.json`, JSON.stringify(dataLid, null, 2));
    fs.writeFileSync(`data/dataBot.json`, JSON.stringify(dataBot, null, 2));
}

function getLidQuestions(): Rec[] {
    const lidData = fs.readFileSync(LID_FILE, 'utf-8');
    const result: Rec[] = lidData.split('\r\n').slice(1).filter((line: string) => line.trim()).map((line: string) => {
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

function getBotQuestions() {
    const botData = fs.readFileSync(BOT_FILE, 'utf-8');
    const botJson = JSON.parse(botData);
    return botJson.questions.map((q: any) => {
        return {
            num: q.number,
            question: q.title,
            a: q.options[0].trim(),
            b: q.options[1].trim(),
            c: q.options[2].trim(),
            d: q.options[3].trim(),
            solution: 'abcd'.charAt(q.correct)
        }
    });
}

function reduce(obj: any, keys: string[]) {
    if (keys) {
        return Object.keys(obj).reduce(function (acc: any, k: string) {
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
