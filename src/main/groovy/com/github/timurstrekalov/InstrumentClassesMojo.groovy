package com.github.timurstrekalov;

import java.util.List

import org.codehaus.groovy.maven.mojo.GroovyMojo

/**
 * Instruments source classes.
 *
 * @goal instrument
 * @phase process-resources
 *
 * @author Timur Strekalov
 */
class InstrumentClassesMojo extends GroovyMojo {

    /**
    * @parameter default-value="jscoverage"
    */
    String jsCoveragePath

    /**
     * @parameter default-value="${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript"
     */
    String srcDir

    /**
     * @parameter default-value="${project.build.directory}${file.separator}classes"
     */
    String destDir

    /**
     * @parameter
     */
    List<String> excludes

    /**
     * @parameter default-value=false
     */
    Boolean verbose

    void execute() {
        log.info "Cleaning destination directory"

        ant.delete dir: destDir

        log.info "Preparing to instrument classes..."

        if (!new File(destDir).mkdirs()) {
            log.warn "Could not create destination directory. Already exists?"
        }

        def jscoverage = createCommand().execute()
        jscoverage.consumeProcessOutput System.out, System.err
        jscoverage.waitFor()
    }

    private String createCommand() {
        def command = []

        command << jsCoveragePath

        if (verbose) {
            command << "--verbose"
        }

        def scanner = ant.fileScanner {
            fileset(dir: srcDir, defaultexcludes: false) {
                excludes.each {
                    include name: it
                }
            }
        }

        def root = new File(srcDir).toURI()
        def excludedBuf = scanner.collect { root.relativize(it.toURI()) }

        command << excludedBuf.collect { "--exclude=$it" }.join(" ")
        command << srcDir
        command << destDir

        if (verbose) {
            log.info "Running jscoverage with:"
            log.info command.join("\n\t")
        }

        return command.join(" ")
    }

}
