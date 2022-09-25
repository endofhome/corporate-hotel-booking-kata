package uk.co.endofhome.corporatehotelbookingkata.result

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success

fun <T> T.asFailure(): Failure<T> = Failure(this)
fun <T> T.asSuccess(): Success<T> = Success(this)
fun <T, E>Result4k<T, E>.expectSuccess(): T = if (this is Success<T>) { this.value } else { error("Expected success but was failure: $this") }