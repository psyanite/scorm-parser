package com.psyanite.scorm.exception

object InvalidManifestException {
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

class InvalidManifestException(message: String = null, cause: Throwable = null) extends
    RuntimeException(InvalidManifestException.defaultMessage(message, cause), cause) {
}
