package com.github.timurstrekalov.coverage

class CoverageData {

    String title
    Integer statements
    Integer executed
    Integer coverage

    CoverageData(String title, statements, executed) {
        this.title = title
        this.statements = statements as int
        this.executed = executed as int
        this.coverage = calculateCoverage()
    }

    public String getCoverageString() {
        return "${coverage}%"
    }

    private Integer calculateCoverage() {
        return (executed == 0 ? 0 : executed / statements * 100) as int
    }

}
