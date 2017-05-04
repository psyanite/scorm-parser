package com.psyanite.scorm.metadata

import com.psyanite.scorm.exception.InvalidManifestException
import com.psyanite.scorm.helper.Helper

import scala.xml.NodeSeq

class Resource (
    var identifier: String,
    var resourceType: String,
    var scormType: String,
    var href: Option[String]
)

object Resource {
    def apply(resource: NodeSeq): Resource = {
        new Resource(
            getAttribute(resource, "identifier"),
            getAttribute(resource, "type"),
            getScormType(resource),
            Helper.getAttributeValue(resource, "href")
        )
    }

    private def getAttribute(resource: NodeSeq, attribute: String): String = {
        Helper.getAttributeValue(resource, attribute)
            .getOrElse(throw new InvalidManifestException("A resource node is missing the '%s' attribute".format(attribute)))
    }

    private def getScormType(nodeSeq: NodeSeq): String = {
        nodeSeq.headOption match {
            case None => throw new InvalidManifestException("A resource node is missing the 'adlcp:scormtype' attribute")
            case Some(node) => node.attribute(node.getNamespace("adlcp"), "scormtype") match {
                case None => throw new InvalidManifestException("A resource node is missing the 'adlcp:scormtype' attribute")
                case Some(attribute) => attribute.text
            }
        }
    }
}
