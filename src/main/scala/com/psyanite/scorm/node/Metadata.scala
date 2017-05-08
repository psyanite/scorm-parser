package com.psyanite.scorm.node

import scala.xml.NodeSeq

case class Metadata (
    var schema: Option[String],
    var schemaVersion: Option[String],
    var scheme: Option[String]
)

object Metadata extends BaseNode {
    def apply(name: NodeSeq, version: NodeSeq, scheme: NodeSeq): Metadata = {
        new Metadata(getText(name), getText(version), getText(scheme))
    }
}

