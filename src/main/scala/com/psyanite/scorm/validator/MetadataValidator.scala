package com.psyanite.scorm.validator

import com.psyanite.scorm.node.Metadata

class MetadataValidator {

    def validate(metadata: Metadata): Seq[String] = {
        if ((metadata.schema, metadata.schemaVersion, metadata.scheme) == (None, None, None)) {
            Seq("Metadata schema and scheme not found")
        }
        else {
            if (metadata.scheme.isEmpty) {
                validate("metadata schema", MetadataValidator.ValidSchemaValue, metadata.schema) ++
                    validate("metadata schema version", MetadataValidator.ValidSchemaVersionValue, metadata.schemaVersion)
            }
            else {
                validate("metadata scheme", MetadataValidator.ValidSchemeValue, metadata.scheme)
            }
        }
    }

    private def validate(field: String, expected: String, target: Option[String]): Seq[String] = {
        target match {
            case None => Seq(buildNotFoundFailure(field))
            case Some(value) =>
                if (value != expected) {
                    Seq(buildUnexpectedFailure(field, expected, value))
                } else {
                    Seq()
                }
        }
    }

    private def buildNotFoundFailure(field: String): String = {
        "%s value not found".format(field.capitalize)
    }

    private def buildUnexpectedFailure(field: String, expected: String, found: String): String = {
        "Invalid %s value found; expected '%s', but found '%s'".format(field, expected, found)
    }

}

object MetadataValidator {
    private val ValidSchemaValue         = "ADL SCORM"
    private val ValidSchemaVersionValue  = "1.2"
    private val ValidSchemeValue         = "ADL SCORM 1.2"

    def apply() = new MetadataValidator
}
