package uk.co.endofhome.corporatehotelbookingkata.result

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success

fun <T> T.asFailure(): Failure<T> = Failure(this)
fun <T> T.asSuccess(): Success<T> = Success(this)
