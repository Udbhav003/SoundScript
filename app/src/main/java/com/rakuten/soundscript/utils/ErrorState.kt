package com.rakuten.soundscript.utils

data class ErrorState(
    var isErrorOccurred: Boolean,
    var errors: MutableSet<Error>
){
    companion object {
        val DEFAULT = ErrorState(
            isErrorOccurred = false,
            errors = mutableSetOf()
        )
    }

    fun setError(error: Error){
        isErrorOccurred = true
        errors.add(error)
    }


}

data class Error(
    val code: Int? = null,
    val message: String? = null,
    val tag: String
)
