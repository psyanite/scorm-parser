package com.psyanite.scorm.validator

import com.psyanite.scorm.node.{Item, Manifest}

class ManifestValidator {

    def validate(manifest: Manifest): Seq[String] = {
        val metadataErrors = MetadataValidator().validate(manifest.metadata)
        val itemErrors = validateItems(manifest.items)
        metadataErrors ++ itemErrors
    }

    def validateItems(items: Seq[Item]): Seq[String] = {
        items.map { item =>
            ItemValidator().validate(item)
        }.reduceLeft{_ ++ _}
    }
}

object ManifestValidator {
    def apply() = new ManifestValidator
}
