package com.github.timurstrekalov.reporter

import com.github.timurstrekalov.coverage.Coverage

abstract class AbstractReporter {

    protected PrintWriter writer
    protected Coverage coverage

    AbstractReporter(PrintWriter writer, Coverage coverage) {
        this.writer = writer
        this.coverage = coverage
    }

    abstract void generate()

    protected void forEachFile(Closure closure) {
        coverage.files.each closure
    }

}
