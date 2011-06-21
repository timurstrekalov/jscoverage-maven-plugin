var data = window._$jscoverage;

if (!data) {
    throw new Error("No coverage data found");
}

var report = {
    files: [],
    totals: {
        files: 0,
        statements: 0,
        executed: 0
    }
};

var files = report.files;
var totals = report.totals;

for (name in data) {
    if (data.hasOwnProperty(name)) {
        files.push({
            name: name,
            statements: 0,
            executed: 0,
            missing: []
        });
    }
}

files.sort();

var rowCounter = 0;

for (var i = 0; i < files.length; i++) {
    var file = files[i];
    var fileCovData = data[file.name];

    var lineNumber;

    var missing = file.missing;
    var currentConditionalEnd = 0;
    var conditionals;

    if (fileCovData.conditionals) {
        conditionals = fileCovData.conditionals;
    }

    for (lineNumber = 0; lineNumber < fileCovData.length; lineNumber++) {
        var n = fileCovData[lineNumber];

        if (lineNumber === currentConditionalEnd) {
            currentConditionalEnd = 0;
        } else if (currentConditionalEnd === 0 && conditionals
                && conditionals[lineNumber]) {
            currentConditionalEnd = conditionals[lineNumber];
        }

        if (currentConditionalEnd !== 0) {
            continue;
        }

        if (n === undefined || n === null) {
            continue;
        }

        if (n === 0) {
            missing.push(lineNumber);
        } else {
            file.executed++;
        }

        file.statements++;
    }

    totals["files"]++;
    totals["statements"] += file.statements;
    totals["executed"] += file.executed;
}

report

