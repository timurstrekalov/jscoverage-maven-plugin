package com.github.timurstrekalov.reporter

import com.github.timurstrekalov.coverage.Coverage
import com.github.timurstrekalov.coverage.CoverageData

class CsvReporter extends AbstractReporter {

    private String delimiter

    CsvReporter(String path, Coverage coverage, String delimiter) {
        super(new File(path).newPrintWriter(), coverage)

        this.delimiter = delimiter
    }

    @Override
    public void generate() {
        def header = ["Filename", "Statements", "Executed", "Coverage"]

        writer.println header.join(delimiter)

        forEachFile { CoverageData c ->
            outputCoverage(c)
        }

        outputCoverage(coverage.total)

        writer.flush()
    }

    private void outputCoverage(CoverageData c) {
        def line = [c.title, c.statements, c.executed, c.coverageString]

        writer.println line.join(delimiter)
    }

}
