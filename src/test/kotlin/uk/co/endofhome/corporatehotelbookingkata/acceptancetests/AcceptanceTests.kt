package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import dev.forkhandles.result4k.Success
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType.Single
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.booking.Hotel
import uk.co.endofhome.corporatehotelbookingkata.booking.HotelService
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import java.time.LocalDate

class AcceptanceTests {
    @Disabled("W.I.P.")
    @Test
    fun `Employee can book a room`() {
        val edwin = Employee
        val checkInDate = LocalDate.of(2022, 9, 18)
        val checkOutDate = LocalDate.of(2022, 9, 19)

        edwin.book(checkInDate, checkOutDate)
    }
}

val exampleEmployeeId = EmployeeId("some-id")
val exampleHotelId = HotelId("some-id")
val exampleCheckInDate: LocalDate = LocalDate.of(2022, 9, 18)
val exampleCheckOutDate: LocalDate = LocalDate.of(2022, 9, 19)

object Employee {
    private val hotelService = HotelService(listOf(
        Hotel(
            id = exampleHotelId,
            availability = mapOf(
                exampleCheckInDate to mapOf(Single to 1)
        ))
    ))
    private val bookingPolicyService = BookingPolicyService()
    private val bookingService = BookingService(hotelService, bookingPolicyService)

    fun book(checkInDate: LocalDate, checkOutDate: LocalDate) {
        val result = bookingService.book(exampleEmployeeId, exampleHotelId, Single, checkInDate, checkOutDate)

        result.shouldBeInstanceOf<Success<Booking>>()
    }
}