package uk.co.endofhome.corporatehotelbookingkata.result

import dev.forkhandles.result4k.Failure

fun <T> T.asFailure(): Failure<T> = Failure(this)