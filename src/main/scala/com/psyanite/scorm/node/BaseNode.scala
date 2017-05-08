package com.psyanite.scorm.node

import scala.xml.NodeSeq

trait BaseNode {
    def getText(nodeSeq: NodeSeq): Option[String] = {
        nodeSeq.headOption match {
            case Some(node) => Some(node.text)
            case None => None
        }
    }

    def getAttributeValue(nodeSeq: NodeSeq, attribute: String): Option[String] = {
        nodeSeq.headOption.flatMap(_.attribute(attribute).map(_.text))
    }
}
