package com.github.timurstrekalov

import groovy.xml.MarkupBuilder

import java.io.File
import java.util.List

import org.codehaus.groovy.maven.mojo.GroovyMojo

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.IncorrectnessListener
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController
import com.gargoylesoftware.htmlunit.ScriptPreProcessor
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

/**
* Generates coverage and outputs it to the log.
*
* @goal coverage
* @phase test
*
* @author Timur Strekalov
*/
class GenerateCoverageMojo extends GroovyMojo {

    private final String returnJsCoverageVarScript = 'window._$jscoverage.toSource()'
    private final String coverageScript = getClass().getResourceAsStream("/jscoverage.js").text

    /**
     * @parameter
     */
    File instrumentedSrcDir

    /**
     * @parameter
     */
    List<String> tests

    /**
     * A list of formats to generate. Supported values are: csv, xml, console
     *
     * @parameter
     */
    List<String> formats

    public void execute() {
        def webClient = new WebClient(BrowserVersion.FIREFOX_3_6)

        webClient.javaScriptEnabled = true
        webClient.ajaxController = new NicelyResynchronizingAjaxController()
        webClient.incorrectnessListener = { message, origin -> } as IncorrectnessListener

        def preProcessor = new PreProcessor(instrumentedSrcDir)

        webClient.scriptPreProcessor = preProcessor

        HtmlPage page

        tests.each {
            log.info "Running $it"

            page = webClient.getPage(new File(it).toURI() as String)

            webClient.waitForBackgroundJavaScript 30000

            preProcessor.update(page.executeJavaScript(
                returnJsCoverageVarScript).javaScriptResult)
        }

        def coverage = page.executeJavaScript(coverageScript).javaScriptResult

        webClient.closeAllWindows()

        generateReports(coverage)
    }

    private void generateReports(coverage) {
        new File("coverage").mkdir()

        if ('csv' in formats) {
            generateCsvReport(coverage)
        }

        if ('xml' in formats) {
            generateXmlReport(coverage)
        }

        if ('console' in formats) {
            generateConsoleReport(coverage)
        }
    }

    private void generateCsvReport(coverage) {
        log.info "Generating CSV report"
        new File("coverage/coverage.csv").withPrintWriter { out ->
            out.println "Filename;Statements;Executed;Coverage"
            coverage.files.values().each {
                out.print "$it.name;"
                out.print "${it.statements as int};"
                out.print "${it.executed as int};"
                out.println "${(it.executed == 0 ?: it.executed / it.statements * 100) as int}%"
            }

            def t = coverage.totals

            out.print "Total;"
            out.print "${t.statements as int};"
            out.print "${t.executed as int};"
            out.println "${(t.executed == 0 ?: t.executed / t.statements * 100) as int}%"
        }
    }

    private void generateXmlReport(coverage) {
        log.info "Generating XML report"

        new File("coverage/coverage.xml").withPrintWriter { writer ->
            def xml = new MarkupBuilder(writer)

            xml.coverage {
                files {
                    coverage.files.values().each {
                        file(
                            name: it.name,
                            statements: it.statements as int,
                            executed: it.executed as int,
                            coverage: (it.executed == 0 ?: it.executed / it.statements * 100) as int
                        )
                    }
                }

                def t = coverage.totals

                total(
                    statements: t.statements as int,
                    executed: t.executed as int,
                    coverage: (t.executed == 0 ?: t.executed / t.statements * 100) as int
                )
            }
        }
    }

    private void generateConsoleReport(coverage) {
        log.info "Generating console report"
        def buf = ["COVERAGE REPORT"]

        coverage.files.values().each {
            buf << it.name
            buf << "  Statements: ${it.statements as int}"
            buf << "  Executed: ${it.executed as int}"
            buf << "  Coverage: ${(it.executed == 0 ?: it.executed / it.statements * 100) as int}%"
        }

        def t = coverage.totals

        buf << "Total"
        buf << "  Statements: ${t.statements as int}"
        buf << "  Executed: ${t.executed as int}"
        buf << "  Coverage: ${(t.executed == 0 ?: t.executed / t.statements * 100) as int}%"

        log.info buf.join("\n  ")
    }

}

class PreProcessor implements ScriptPreProcessor {

    def result = "{}"
    Boolean injected = false
    File instrumentedSrcDir

    PreProcessor(instrumentedSrcDir) {
        this.instrumentedSrcDir = instrumentedSrcDir
    }

    String preProcess(HtmlPage htmlPage, String sourceCode, String sourceName,
        int lineNumber, HtmlElement htmlElement) {

        if (!injected) {
            injected = true
            return "window._\$jscoverage = $result;\n$sourceCode"
        }

        if (sourceCode.indexOf("loadScript(jasmine.plugin.jsSrcDir + fileName);") != -1) {
            return sourceCode.replaceAll(/jasmine\.plugin\.jsSrcDir/, "'${instrumentedSrcDir.toURI() as String}'")
        }

        return sourceCode
    }

    void update(result) {
        this.result = result
        this.injected = false
    }

}
