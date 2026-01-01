const fs = require('fs');

const filePath = 'c:\\Coding\\pecas-por-codigo-backend\\src\\main\\resources\\static\\PD195-28122025-verificar.tsv';
const content = fs.readFileSync(filePath, 'utf-8');
const lines = content.split('\n');

console.log(`Total lines: ${lines.length}`);

const colCounts = new Set();
const descriptions = new Map();

for (let i = 0; i < Math.min(lines.length, 100000); i++) {
    const line = lines[i].trim();
    if (!line) continue;

    const parts = line.split('\t');
    colCounts.add(parts.length);

    const desc = parts[parts.length - 1];
    descriptions.set(desc, (descriptions.get(desc) || 0) + 1);

    if (parts.length !== 4 && parts.length !== 5) {
        // Log weird lines
        if (i < 100) {
            // console.log(`Line ${i+1} has ${parts.length} columns: ${line}`);
        }
    }
}

console.log('Column counts distribution:');
colCounts.forEach(c => console.log(` - ${c} columns`));

console.log('\nTop 20 descriptions:');
const sortedDescs = Array.from(descriptions.entries()).sort((a, b) => b[1] - a[1]);
sortedDescs.slice(0, 20).forEach(([desc, count]) => {
    console.log(` - ${desc}: ${count}`);
});
