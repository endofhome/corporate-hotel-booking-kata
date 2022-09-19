package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import dev.forkhandles.result4k.Success
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.booking.*
import uk.co.endofhome.corporatehotelbookingkata.domain.BookingConfirmation
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
                rooms = mapOf(Single to 1)
            )
        ))
        val bookingPolicyService = BookingPolicyService()
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())
        val edwin = Employee(exampleEmployeeId, bookingService)

        edwin.book(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
    }

    @Test
    fun `Two employees can book the same room on consecutive nights`() {
        val edwinChecksInDate = exampleCheckInDate
        val eileenChecksInDate = edwinChecksInDate.plusDays(1)
        val eileenChecksOutDate = eileenChecksInDate.plusDays(1)
        val hotelService = HotelService(listOf(
            Hotel(
                id = exampleHotelId,
                rooms = mapOf(Single to 1)
            )
        ))
        val bookingPolicyService = BookingPolicyService()
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())
        val edwin = Employee(exampleEmployeeId, bookingService)
        val eileen = Employee(EmployeeId("eileen-id"), bookingService)

        edwin.book(exampleHotelId, Single, edwinChecksInDate, eileenChecksInDate)
        eileen.book(exampleHotelId, Single, eileenChecksInDate, eileenChecksOutDate)
    }
}

val exampleEmployeeId = EmployeeId("some-id")
val exampleHotelId = HotelId("some-id")
val exampleCheckInDate: LocalDate = LocalDate.of(2022, 9, 18)
val exampleCheckOutDate: LocalDate = LocalDate.of(2022, 9, 19)

class Employee(private val employeeId: EmployeeId, private val bookingService: BookingService) {

    fun book(hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate){
        val result = bookingService.book(employeeId, hotelId, roomType, checkInDate, checkOutDate)

        result.shouldBeInstanceOf<Success<BookingConfirmation>>()
    }
}