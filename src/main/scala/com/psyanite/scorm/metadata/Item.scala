package com.psyanite.scorm.metadata

import com.psyanite.scorm.exception.InvalidManifestException
import com.psyanite.scorm.helper.Helper

import scala.util.Try
import scala.xml.NodeSeq

class Item (
    var identifier: String,
    var title: String,
    var masteryScore: Option[Int]
)

object Item {
    def apply(item: NodeSeq): Item = {
        new Item(
            Helper.getAttributeValue(item, "identifier")
                .getOrElse(throw new InvalidManifestException("An item node is missing the 'identifier' attribute")),
            Helper.getText(item \ "title")
                .getOrElse(throw new InvalidManifestException("An item node is missing a child 'title' node")),
            buildMasteryScore(item)
        )
    }

    def buildMasteryScore(item: NodeSeq): Option[Int] = {
        val masteryScore = (item \ "masteryscore").filter(_.prefix == "adlcp")
        masteryScore.headOption match {
            case None => None
            case Some(node) =>
                val score = Try(node.text.toInt).getOrElse(throw new InvalidManifestException("Invalid mastery score '%s' found".format(node.text)))
                val validator = new MasteryScoreValidator
                validator.validate(score)
                Some(score)
        }
    }
}
