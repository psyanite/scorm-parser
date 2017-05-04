package com.psyanite.scorm.metadata

import com.psyanite.scorm.helper.Helper

import scala.xml.NodeSeq

class Schema (
    var schema: Option[String],
    var schemaVersion: Option[String],
    var metadataScheme: Option[String]
)

object Schema {
    def apply(name: NodeSeq, version: NodeSeq, metadataScheme: NodeSeq): Schema = {
        val schema = new Schema(Helper.getText(name), Helper.getText(version), Helper.getText(metadataScheme))
        val validator = new SchemaValidator
        validator.validate(schema)
        schema
    }
}

