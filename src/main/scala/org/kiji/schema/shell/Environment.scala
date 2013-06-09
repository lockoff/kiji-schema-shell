/**
 * (c) Copyright 2013 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiji.schema.shell

import java.io.PrintStream

import org.kiji.annotations.ApiAudience
import org.kiji.annotations.ApiStability
import org.kiji.schema.KConstants
import org.kiji.schema.KijiURI
import org.kiji.schema.shell.input.JLineInputSource
import org.kiji.schema.shell.input.InputSource
import org.kiji.schema.shell.spi.ParserPluginFactory

/**
 * Runtime environment in which DDL commands are executed.
 *
 * @param instanceURI of the Kiji instance commands executed under this environment will use.
 * @param printer that commands executed under this environment will write output to.
 * @param kijiSystem that commands executed under this environment will use to interact with Kiji.
 * @param inputSource from which commands executed under this environment will read input.
 * @param modules loaded under the environment.
 * @param libJars that commands executed under this environment will have access to.
 * @param isInteractive is `true` if commands executed under this environment should be in
 *     interactive mode, `false` if in batch mode.
 */
@ApiAudience.Framework
@ApiStability.Evolving
final class Environment(
    val instanceURI: KijiURI =
        KijiURI.newBuilder().withInstanceName(KConstants.DEFAULT_INSTANCE_NAME).build(),
    val printer: PrintStream = Console.out,
    val kijiSystem: AbstractKijiSystem = new KijiSystem,
    val inputSource: InputSource = new JLineInputSource,
    val modules: List[ParserPluginFactory] = List(),
    val libJars: List[JarLocation] = List(),
    val isInteractive: Boolean = false) {

  /**
   * @return a new Environment with the instance name replaced with 'newInstance'.
   */
  def withInstance(newInstance: String): Environment = {
    return new Environment(KijiURI.newBuilder(instanceURI).withInstanceName(newInstance).build(),
        printer, kijiSystem, inputSource, modules, libJars, isInteractive)
  }

  /**
   * @return a new Environment with the printer replaced with 'newPrinter'.
   */
  def withPrinter(newPrinter: PrintStream): Environment = {
    return new Environment(instanceURI, newPrinter, kijiSystem, inputSource, modules,
        libJars, isInteractive)
  }

  /** @return a new Environment with the InputSource replaced with newSource. */
  def withInputSource(newSource: InputSource): Environment = {
    return new Environment(instanceURI, printer, kijiSystem, newSource, modules,
        libJars, isInteractive)
  }

  /**
   * Return a new Environment with 'module' appended to the list of modules.
   * If the specified module is already on the list, the list is not changed.
   *
   * @param module the module to include.
   * @return an Environment with a list of modules that includes the provided module.
   */
  def withModule(module: ParserPluginFactory): Environment = {
    // Don't add a module more than once; benign condition to silently ignore.
    val moduleName = module.getName
    if (modules.map(m => m.getName).contains(moduleName)) {
      return this // Nothing to change.
    } else {
      val newModules = modules :+ module // new list, with module appended to modules.
      return new Environment(instanceURI, printer, kijiSystem, inputSource,
          newModules, libJars, isInteractive)
    }
  }

  /**
   * Creates a new environment with the specified jar location appended to the list of library
   * jars used by commands executed under the environment. If the specified library jar is already
   * in use, this environment is returned.
   *
   * @param newJar to include in the new environment.
   * @return a new environment that uses the specified library jar, or this environment if the jar
   *     was already in use.
   */
  def withLibJar(newJar: JarLocation): Environment = {
    if (libJars.contains(newJar)) {
      this
    } else {
      new Environment(instanceURI, printer, kijiSystem, inputSource, modules, libJars :+ newJar,
          isInteractive)
    }
  }

  /**
   * Return a new Environment with the interactivity flag set to the value of the argument
   * to this method.
   *
   * @param interactiveFlag true if this is run from an interactive terminal,
   *     false if from a script or API usage.
   * @return an Environment with the isInteractive flag set to the argument value.
   */
  def withInteractiveFlag(interactiveFlag: Boolean): Environment = {
    return new Environment(instanceURI, printer, kijiSystem, inputSource, modules,
        libJars, interactiveFlag)
  }

  /**
   * @param tableName the name of the table to test for.
   * @return true if a table named 'tableName' is present in the system.
   */
  def containsTable(tableName: String): Boolean = {
    return kijiSystem.getTableLayout(instanceURI, tableName).isDefined
  }
}
