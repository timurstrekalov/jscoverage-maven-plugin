package com.github.timurstrekalov.coverage

import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject

class Coverage {

    List<CoverageData> files = []
    CoverageData total

    Coverage(ScriptableObject obj) {
        obj.files.values().each {
            files << new CoverageData(it.name, it.statements, it.executed)
        }

        def t = obj.totals
        total = new CoverageData("Total", t.statements, t.executed)
    }

}
