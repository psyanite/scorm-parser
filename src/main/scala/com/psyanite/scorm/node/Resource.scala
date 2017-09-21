package com.psyanite.scorm.node

import com.psyanite.scorm.exception.ParseException

import scala.xml.NodeSeq

case class Resource (
    var identifier:   String,
    var resourceType: String,
    var scormType:    String,
    var href:         Option[String]
)

object Resource extends BaseNode {
    def apply(resource: NodeSeq): Resource = {
        new Resource(
            getAttribute(resource, "identifier"),
            getAttribute(resource, "type"),
            getScormType(resource),
            getAttributeValue(resource, "href")
        )
    }

    private def getAttribute(resource: NodeSeq, attribute: String): String = {
        getAttributeValue(resource, attribute)
            .getOrElse(throw new ParseException("A resource node is missing the '%s' attribute".format(attribute)))
    }

    private def getScormType(nodeSeq: NodeSeq): String = {
        nodeSeq.headOption match {
            case None => throw new ParseException("A resource node is missing the 'adlcp:scormtype' attribute")
            case Some(node) => node.attribute(node.getNamespace("adlcp"), "scormtype") match {
                case None => throw new ParseException("A resource node is missing the 'adlcp:scormtype' attribute")
                case Some(attribute) => attribute.text
            }
        }
    }
}
