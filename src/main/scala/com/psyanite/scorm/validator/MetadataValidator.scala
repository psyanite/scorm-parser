package com.psyanite.scorm.validator

import com.psyanite.scorm.node.{Manifest, Metadata}

import scalaz.Scalaz._
import scalaz.{NonEmptyList, Validation}

class MetadataValidator {
    type ValidationNel[T] = Validation[NonEmptyList[String], T]

    def validate(metadata: Metadata): ValidationNel[Metadata] = {
        if ((metadata.schema, metadata.schemaVersion, metadata.scheme) == (None, None, None)) {
            "Metadata schema and scheme not found".failureNel
        }
        else {
            if (metadata.scheme.isEmpty) {
                (validate("metadata schema", MetadataValidator.ValidSchemaValue, metadata.schema) |@|
                  validate("metadata schema version", MetadataValidator.ValidSchemaVersionValue, metadata.schemaVersion) |@|
                  validateSuccess(metadata.scheme))
                {Metadata(_, _, _)}
            }
            else {
                (validateSuccess(metadata.schema) |@|
                  validateSuccess(metadata.schemaVersion) |@|
                  validate("metadata scheme", MetadataValidator.ValidSchemeValue, metadata.scheme))
                {Metadata(_, _, _)}
            }
        }
    }

    def validateSuccess(value: Option[String]): ValidationNel[Option[String]] = {
        value.successNel
    }

    private def validate(field: String, expected: String, target: Option[String]): ValidationNel[Option[String]] = {
        target match {
            case None => buildNotFoundFailure(field)
            case Some(value) =>
                if (value != expected) {
                    buildUnexpectedFailure(field, expected, value)
                } else {
                    target.successNel
                }
        }
    }

    private def buildNotFoundFailure(field: String): ValidationNel[Option[String]] = {
        "%s value not found".format(field.capitalize).failureNel
    }

    private def buildUnexpectedFailure(field: String, expected: String, found: String): ValidationNel[Option[String]] = {
        "Invalid %s value found; expected '%s', but found '%s'".format(field, expected, found).failureNel
    }

}

object MetadataValidator {
    private val ValidSchemaValue         = "ADL SCORM"
    private val ValidSchemaVersionValue  = "1.2"
    private val ValidSchemeValue         = "ADL SCORM 1.2"

    def apply() = new MetadataValidator
}
