package com.psyanite.scorm.validator

import com.psyanite.scorm.node.Item

class ItemValidator {

    def validate(item: Item): Seq[String] = {
        validateMasteryScore(item.masteryScore)
    }

    def validateMasteryScore(masteryScore: Option[Int]): Seq[String] = {
        masteryScore match {
            case Some(score) if score < ItemValidator.MinValue || score > ItemValidator.MaxValue =>
                Seq("Invalid mastery score '%d' found; expected value between %d and %d".format(score, ItemValidator.MinValue, ItemValidator.MaxValue))
            case _ => Seq()
        }
    }
}

object ItemValidator {
    private val MinValue = 0
    private val MaxValue = 100

    def apply() = new ItemValidator
}
