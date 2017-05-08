package com.psyanite.scorm.validator

import com.psyanite.scorm.node.Item

import scalaz.Scalaz._
import scalaz.{NonEmptyList, Validation}

class ItemValidator {
    type ValidationNel[T] = Validation[NonEmptyList[String], T]

    def validate(item: Item): ValidationNel[Item] = {
        (validateIdentifier(item.identifier) |@|
          validateTitle(item.title) |@|
          validateMasteryScore(item.masteryScore))
        {Item(_, _, _)}
    }

    def validateIdentifier(identifier: String): ValidationNel[String] = {
        identifier.successNel
    }

    def validateTitle(title: String): ValidationNel[String] = {
        title.successNel
    }

    def validateMasteryScore(masteryScore: Option[Int]): ValidationNel[Option[Int]] = {

        masteryScore match {
            case None => masteryScore.successNel
            case Some(score) => if (score < ItemValidator.MinValue || score > ItemValidator.MaxValue) {
                    "Invalid mastery score '%d' found; expected value between %d and %d".format(score, ItemValidator.MinValue, ItemValidator.MaxValue).failureNel
                }
                else {
                    masteryScore.successNel
                }
        }
    }
}

object ItemValidator {
    private val MinValue = 0
    private val MaxValue = 100

    def apply() = new ItemValidator
}
