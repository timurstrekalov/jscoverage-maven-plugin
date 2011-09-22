package com.github.timurstrekalov;

import java.util.List

import org.apache.maven.plugin.MojoExecutionException
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
     * @parameter
     */
    List<String> noInstrument

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

        Integer exitCode = jscoverage.waitFor()

		if (exitCode != 0) {
			throw new MojoExecutionException(
				"JSCoverage exited with code $exitCode, aborting");
		}
    }

    private String createCommand() {
        def command = []

        command << jsCoveragePath

        if (verbose) {
            command << "--verbose"
        }

        def root = new File(srcDir).toURI()

		appendCommand(command, noInstrument) { File file ->
			return "--no-instrument=${root.relativize(file.toURI())}"
		}

		appendCommand(command, excludes) { File file ->
			return "--exclude=${root.relativize(file.toURI())}"
		}

        command << srcDir
        command << destDir

        if (verbose) {
            log.info "Running jscoverage with:"
            log.info command.join("\n\t")
        }

        return command.join(" ")
    }

	private void appendCommand(List<String> command, List<String> patterns,
		Closure collector) {

		if (!patterns || patterns.size() == 0) {
			return
		}

		def antPatterns = patterns.findAll { it.contains('*') }

		patterns.findAll { !it.contains('*') }.each { String path ->
			command << collector(new File(srcDir, path))
		}

		if (antPatterns.size() > 0) {
			def scanner = ant.fileScanner {
				fileset(dir: srcDir, defaultexcludes: false) {
					patterns.each {
						include name: it
					}
				}
			}

			command.addAll(scanner.collect(collector))
		}
	}

}
