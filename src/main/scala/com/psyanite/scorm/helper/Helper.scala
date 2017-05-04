package com.psyanite.scorm.helper

import scala.xml.NodeSeq

object Helper {

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
