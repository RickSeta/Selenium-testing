package com.gotriva;

import com.gotriva.nlp.antifa.Antifa;
import com.gotriva.nlp.antifa.reporting.OutputFormat;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/** The Antifa UI test plugin Mojo */
@Mojo(name = "ui-test", defaultPhase = LifecyclePhase.INTEGRATION_TEST, requiresProject = false)
public class AntifaMojo extends AbstractMojo {

  private static final Logger LOGGER = LoggerFactory.getLogger(AntifaMojo.class);

  private static final String DRIVER_PATH =  System.getenv("HOMEDRIVE") 
      + System.getenv("HOMEPATH") 
      + "\\Documents\\Selenium\\chromedriver.exe";
  static {
    /** Set webdriver location */
    System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
  }

  /** Location of the input files. */
  @Parameter(
      property = "inputDirectory",
      defaultValue = "${project.basedir}/src/test/resources/antifa",
      required = true)
  private File inputDirectory;

  /** Location of the output files. */
  @Parameter(
      property = "outputDirectory",
      defaultValue = "${project.build.directory}",
      required = true)
  private File outputDirectory;

  /** Output file format (HTML|XML). */
  @Parameter(property = "outputFormat", defaultValue = "HTML", required = true)
  private OutputFormat outputFormat;

  /*
   * (non-Javadoc)
   *
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  public void execute() throws MojoExecutionException {

    /** Check input and output directories */
    if (!inputDirectory.isDirectory()) {
      throw new MojoExecutionException(
          "Input directory is not valid: " + inputDirectory.getAbsolutePath());
    }
    LOGGER.info("Reading files on directory: {}", inputDirectory.getAbsolutePath());

    if (!outputDirectory.isDirectory()) {
      throw new MojoExecutionException(
          "Output directory is not valid: " + outputDirectory.getAbsolutePath());
    }
    LOGGER.info("Output files on directory: {}", outputDirectory.getAbsolutePath());

    /** Output result */
    LOGGER.info("Output result as: " + outputFormat);

    Antifa antifa = Antifa.instance(outputDirectory, outputFormat);

    /** For each test file */
    Stream.of(inputDirectory.listFiles())
        /** execute tests for files */
        .forEach(file -> executeTest(antifa, file));

    LOGGER.info("All done!");
  }

  private void executeTest(Antifa antifa, File testFile) {
    try {
      antifa.execute(getTestName(testFile.getName()), testFile);
    } catch (IOException e) {
      throw new RuntimeException("Error executing file " + testFile.getName(), e);
    }
  }

  private String getTestName(String fileName) {
    return StringUtils.capitalize(
        String.join(" ", fileName.substring(0, fileName.lastIndexOf(".")).split("_"))
            .toLowerCase());
  }
}
