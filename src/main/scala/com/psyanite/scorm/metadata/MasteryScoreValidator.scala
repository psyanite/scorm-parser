package com.psyanite.scorm.metadata

import com.psyanite.scorm.exception.InvalidManifestException

class MasteryScoreValidator {
    def validate(masteryScore: Int): Unit =  {
        if (masteryScore < MasteryScoreValidator.MasteryScoreMinimumValue || masteryScore > MasteryScoreValidator.MasteryScoreMaximumValue) {
            throw new InvalidManifestException("Invalid mastery score '%d' found; expected value between %d and %d"
                .format(masteryScore, MasteryScoreValidator.MasteryScoreMinimumValue, MasteryScoreValidator.MasteryScoreMaximumValue))
        }
    }
}

object MasteryScoreValidator {
    val MasteryScoreMinimumValue = 0
    val MasteryScoreMaximumValue = 100
}
