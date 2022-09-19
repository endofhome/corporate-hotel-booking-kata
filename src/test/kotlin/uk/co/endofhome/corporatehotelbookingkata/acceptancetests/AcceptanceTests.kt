package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.booking.Hotel
import uk.co.endofhome.corporatehotelbookingkata.booking.HotelService
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType.Single
import java.time.LocalDate

class AcceptanceTests {

    @Test
    fun `Employee can book a room`() {
        val hotelService = HotelService(listOf(
            Hotel(
                id = exampleHotelId,
                availability = mapOf(
                    exampleCheckInDate to mapOf(Single to 1)
                ))
        ))
        val bookingPolicyService = BookingPolicyService()
        val bookingService = BookingService(hotelService, bookingPolicyService)
        val edwin = Employee(exampleEmployeeId, bookingService)

        edwin.book(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate) shouldBe Booking
    }
}

val exampleEmployeeId = EmployeeId("some-id")
val exampleHotelId = HotelId("some-id")
val exampleCheckInDate: LocalDate = LocalDate.of(2022, 9, 18)
val exampleCheckOutDate: LocalDate = LocalDate.of(2022, 9, 19)

class Employee(private val employeeId: EmployeeId, private val bookingService: BookingService) {

    fun book(hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Booking {
        val result = bookingService.book(employeeId, hotelId, roomType, checkInDate, checkOutDate)

        result.shouldBeInstanceOf<Success<Booking>>()

        return result.value
    }
}