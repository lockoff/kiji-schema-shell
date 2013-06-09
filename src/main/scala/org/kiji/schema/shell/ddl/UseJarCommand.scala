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

package org.kiji.schema.shell.ddl

import java.io.File

import org.kiji.annotations.ApiAudience
import org.kiji.schema.shell.DDLException
import org.kiji.schema.shell.Environment
import org.kiji.schema.shell.JarInLocalFile
import org.kiji.schema.shell.JarLocation


/**
 * Returns a modified environment that uses the specified library jar.
 *
 * @param env the environment in which this command executes.
 * @param newJar the location of the library jar that should be used by the new environment.
 */
@ApiAudience.Private
final class UseJarCommand(val env: Environment, val newJar: JarLocation) extends DDLCommand {

  /**
   * Validates that a specified local jar file exists, is a regular file,
   * and has the .jar or .JAR extension.
   *
   * @param jarFile that should be validated.
   */
  private def validateLocalJarFile(jarFile: File) {
    if (!jarFile.exists()) {
      throw new DDLException("The path you specified for the jar does not exist.")
    }
    if (!jarFile.isFile()) {
      throw new DDLException("The path you specified for the jar points to a directory.")
    }
    if (!jarFile.getName().endsWith(".jar") && !jarFile.getName().endsWith(".JAR")) {
      throw new DDLException("You must specify the path to a file with extension .jar or .JAR")
    }
  }

  /**
   * Performs validation on the jar location specified for this command.
   */
  private[shell] def validateJar() {
    // Perform some validation for each type of jar location.
    newJar match {
      case JarInLocalFile(path) => validateLocalJarFile(new File(path))
    }
  }

  override def exec(): Environment = {
    validateJar()
    // Add on to the environment.
    env.withLibJar(newJar)
  }
}
