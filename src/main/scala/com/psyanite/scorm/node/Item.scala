package com.psyanite.scorm.node

import com.psyanite.scorm.exception.ParseException

import scala.util.Try
import scala.xml.NodeSeq

case class Item (
    var identifier:   String,
    var title:        String,
    var masteryScore: Option[Int]
)

object Item extends BaseNode {
    def apply(item: NodeSeq): Item = {
        new Item(
            getAttributeValue(item, "identifier")
              .getOrElse(throw new ParseException("An item node is missing the 'identifier' attribute")),
            getText(item \ "title")
              .getOrElse(throw new ParseException("An item node is missing a child 'title' node")),
            buildMasteryScore(item)
        )
    }

    private def buildMasteryScore(item: NodeSeq): Option[Int] = {
        val masteryScore = (item \ "masteryscore").filter(_.prefix == "adlcp")
        masteryScore.headOption match {
            case None => None
            case Some(node) =>
                val score = Try(node.text.toInt).getOrElse(throw new ParseException("Invalid mastery score '%s' found".format(node.text)))
                Some(score)
        }
    }
}
