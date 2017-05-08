package com.psyanite.scorm.validator

import com.psyanite.scorm.metadata.{ItemValidator, MetadataValidator}
import com.psyanite.scorm.node.{Manifest, Resource}

import scalaz.Scalaz._
import scalaz.{NonEmptyList, Validation}

class ManifestValidator {
    type ValidationNel[T] = Validation[NonEmptyList[String], T]

    def validate(manifest: Manifest): ValidationNel[Manifest] = {
        val metadata = MetadataValidator().validate(manifest.metadata)
        val items = manifest.items.toList.traverseU(ItemValidator().validate(_))
        val resources = validateResources(manifest.resources)
        (metadata |@| items |@| resources){Manifest(_, _, _)}
    }

    def validateResources(resources: Seq[Resource]): ValidationNel[Seq[Resource]] = {
        resources.successNel
    }
}

object ManifestValidator {
    def apply() = new ManifestValidator
}
