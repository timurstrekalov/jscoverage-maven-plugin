package com.github.timurstrekalov.reporter

import com.github.timurstrekalov.coverage.Coverage
import com.github.timurstrekalov.coverage.CoverageData

class CsvReporter extends AbstractReporter {

    CsvReporter(String path, Coverage coverage) {
        super(new File(path).newPrintWriter(), coverage)
    }

    @Override
    public void generate() {
        writer.println "Filename;Statements;Executed;Coverage"

        forEachFile { CoverageData c ->
            outputCoverage(c)
        }

        outputCoverage(coverage.total)

        writer.flush()
    }

    private void outputCoverage(CoverageData c) {
        writer.print "$c.title;"
        writer.print "$c.statements;"
        writer.print "$c.executed;"
        writer.println "$c.coverageString"
    }

}
