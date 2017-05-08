package com.psyanite.scorm.exception

object ParseException {
    def defaultMessage(message: String, cause: Throwable): String = {
        if (message != null) {
            message
        }
        else if (cause != null) {
            cause.toString
        }
        else {
            null
        }
    }
}

class ParseException(message: String) extends RuntimeException {
    new RuntimeException(message, null)
}
