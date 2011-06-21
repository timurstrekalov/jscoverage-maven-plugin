package com.github.timurstrekalov.reporter

import com.github.timurstrekalov.coverage.Coverage
import com.github.timurstrekalov.coverage.CoverageData

class ConsoleReporter extends AbstractReporter {

    private def buf = ["COVERAGE REPORT"]

    ConsoleReporter(Coverage coverage) {
        super(new PrintWriter(System.out, true), coverage)
    }

    @Override
    void generate() {
        forEachFile { CoverageData c ->
            outputCoverage(c)
        }

        outputCoverage(coverage.total)

        writer.println buf.join("\n  ")
    }

    private void outputCoverage(CoverageData c) {
        buf << c.title
        buf << "  Statements: $c.statements"
        buf << "  Executed: $c.executed"
        buf << "  Coverage: $c.coverageString"
    }

}
