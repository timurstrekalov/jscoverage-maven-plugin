package com.github.timurstrekalov.reporter

import groovy.xml.MarkupBuilder

import com.github.timurstrekalov.coverage.Coverage
import com.github.timurstrekalov.coverage.CoverageData

class XmlReporter extends AbstractReporter {

    XmlReporter(String path, Coverage coverage) {
        super(new File(path).newPrintWriter(), coverage)
    }

    @Override
    public void generate() {
        def xml = new MarkupBuilder(writer)

        xml.coverage {
            files {
                forEachFile { CoverageData c ->
                    file(
                        name: c.title,
                        statements: c.statements,
                        executed: c.executed,
                        coverage: c.coverage
                    )
                }
            }

            total(
                statements: coverage.total.statements,
                executed: coverage.total.executed,
                coverage: coverage.total.coverage
            )
        }

        writer.flush()
    }

}
