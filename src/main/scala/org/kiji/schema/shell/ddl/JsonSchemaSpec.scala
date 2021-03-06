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

import org.kiji.annotations.ApiAudience

import org.kiji.schema.avro.CellSchema
import org.kiji.schema.avro.SchemaStorage
import org.kiji.schema.avro.SchemaType

/** A schema specified as its Avro json representation. */
@ApiAudience.Private
final class JsonSchemaSpec(val json: String) extends SchemaSpec {
  override def toString(): String = { json }

  override def toColumnSchema(): CellSchema = {
    val schema = CellSchema.newBuilder
    schema.setType(SchemaType.INLINE)
    schema.setStorage(SchemaStorage.UID)
    schema.setValue(json)
    return schema.build()
  }
}
