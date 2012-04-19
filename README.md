*This project is no longer supported*. Use [Saga](https://github.com/timurstrekalov/saga) instead.

Generates coverage information about JavaScript files using JSCoverage.

As of this moment, you have to provide the path to jscoverage.

Supported formats are: csv, xml and simple console output.

Example config:

<plugin>
    <groupId>com.github.timurstrekalov</groupId>
    <artifactId>jscoverage-maven-plugin</artifactId>
    <version>0.4.2</version>
    <configuration>
        <jsCoveragePath>jscoverage</jsCoveragePath>
        <srcDir>src/main/javascript</srcDir>
        <destDir>target</destDir>
        <instrumentedSrcDir>target/src</instrumentedSrcDir>
        <verbose>true</verbose>
        <formats>
            <format>xml</format>
            <format>csv</format>
            <format>console</format>
        </formats>
        <excludes>
            <exclude>not-really-javascript.js</exclude>
            <exclude>some-other-crap.js</exclude>
            <exclude>**/*someAntPattern*</exclude>
        </excludes>
        <tests>
            <test>target/TestAll.html</test>
        </tests>
    </configuration>
</plugin>

Instrumentation:
    mvn jscoverage:instrument
Coverage report:
    mvn jscoverage:coverage
