package com.psyanite.scorm.metadata

import com.psyanite.scorm.exception.InvalidManifestException

class SchemaValidator {
    def validate(schema: Schema): Unit =  {
        if ((schema.schema, schema.schemaVersion, schema.metadataScheme) == (None, None, None)) {
            throw new InvalidManifestException("Metadata schema and scheme not found")
        }
        else {
            if (schema.metadataScheme.isEmpty) {
                schema.schema match {
                    case None => throw new InvalidManifestException("Metadata schema not found")
                    case Some(name) => if (name != SchemaValidator.ValidSchemaValue) {
                        throw new InvalidManifestException("Invalid metadata schema value found; expected '%s', but found '%s'".format(SchemaValidator.ValidSchemaValue, name))
                    }
                }
                schema.schemaVersion match {
                    case None => throw new InvalidManifestException("Metadata schema version not found")
                    case Some(version) => if (version != SchemaValidator.ValidSchemaVersionValue) {
                        throw new InvalidManifestException("Invalid metadata schema version value found; expected '%s', but found '%s'".format(SchemaValidator.ValidSchemaVersionValue, version))
                    }
                }
            }
            else {
                schema.metadataScheme match {
                    case None => throw new InvalidManifestException("Metadata scheme not found")
                    case Some(version) => if (version != SchemaValidator.ValidMetadataSchemeValue) {
                        throw new InvalidManifestException("Invalid metadata scheme value found; expected '%s', but found '%s'".format(SchemaValidator.ValidMetadataSchemeValue, version))
                    }
                }
            }
        }
    }
}

object SchemaValidator {
    val ValidSchemaValue         = "ADL SCORM"
    val ValidSchemaVersionValue  = "1.2"
    val ValidMetadataSchemeValue = "ADL SCORM 1.2"
}
