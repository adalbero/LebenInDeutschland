const fs = require('node:fs');

const LID_FILE = 'data/question_list';
const BOT_FILE = 'data/data-2024-07-07-BAMF.json';

interface Rec {
    number: string;
    // title: string;
    a: string;
    b: string;
    c: string;
    d: string;
    solution: string;
}

function main() {
    const lidQuestions = getLidQuestions();
    fs.writeFileSync(`data/dataLid.json`, JSON.stringify(lidQuestions, null, 2));

    const botQuestions = getBotQuestions();
    fs.writeFileSync(`data/dataBot.json`, JSON.stringify(botQuestions, null, 2));

    compare(lidQuestions, botQuestions);

    console.log('==> END');
}

function compare(lidQuestions: Rec[], botQuestions: Rec[]) {
    let count = 0;
    const n = lidQuestions.length;
    for (let i = 0; i < n; i++) {
        if (JSON.stringify(lidQuestions[i]) !== JSON.stringify(botQuestions[i])) {
            console.log('==> ' + lidQuestions[i].number);
            count++;
        }
    }
    console.log(`==> total ${lidQuestions.length} / ${botQuestions.length}`)
    console.log(`==> ${count} of ${n}`)

}
function getLidQuestions(): Rec[] {
    const lidData = fs.readFileSync(LID_FILE, 'utf-8');
    const result: Rec[] = lidData.split('\r\n').slice(1).filter((line: string) => line.trim()).map((line: string) => {
        const vet = line.split(';');
        return {
            number: vet[0],
            // title: vet[1],
            a: vet[2]?.trim(),
            b: vet[3]?.trim(),
            c: vet[4]?.trim(),
            d: vet[5]?.trim(),
            solution: vet[6]
        }
    });

    return result;
}

function getBotQuestions() {
    const botData = fs.readFileSync(BOT_FILE, 'utf-8');
    const botJson = JSON.parse(botData);
    return botJson.questions.map((q: any) => {
        return {
            number: q.number,
            // title: q.title,
            a: q.options[0].trim(),
            b: q.options[1].trim(),
            c: q.options[2].trim(),
            d: q.options[3].trim(),
            solution: 'abcd'.charAt(q.correct)
        }
    });
}

main();
