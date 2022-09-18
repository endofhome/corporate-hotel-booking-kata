package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.BookingError.CheckInMustPreceedCheckOut
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.RoomType.Single
import java.time.LocalDate

class AcceptanceTests {
    @Test
    fun `Check out date must be at least one day after the check in date`() {
        val bookingService = BookingService()
        val checkInDate = LocalDate.of(2022, 9, 18)
        val checkOutDate = LocalDate.of(2022, 9, 18)
        val result = bookingService.book(exampleEmployeeId, exampleHotelId, Single, checkInDate, checkOutDate)

        result shouldBe Failure(CheckInMustPreceedCheckOut(checkInDate, checkOutDate))
    }
}

class BookingService {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Booking, BookingError> {
        return Failure(CheckInMustPreceedCheckOut(checkInDate, checkOutDate))
    }
}

@JvmInline
value class EmployeeId(val value: String)

@JvmInline
value class HotelId(val value: String)

enum class RoomType {
    Single
}

object Booking
val exampleEmployeeId = EmployeeId("some-id")
val exampleHotelId = HotelId("some-id")

sealed class BookingError{
    data class CheckInMustPreceedCheckOut(val checkInDate: LocalDate, val checkOutDate: LocalDate) : BookingError()
}