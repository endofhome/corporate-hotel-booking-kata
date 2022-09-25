package uk.co.endofhome.corporatehotelbookingkata.acceptance.actors

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import java.time.LocalDate

class Employee(
    val employeeId: EmployeeId,
    private val bookingService: BookingService
) {
    fun canBook(hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate) {
        val result = bookingService.book(employeeId, hotelId, roomType, checkInDate, checkOutDate)

        result shouldBe Success(Booking(employeeId, hotelId, roomType, checkInDate, checkOutDate))
    }

    fun cannotBook(hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate, because: BookingError) {
        val result = bookingService.book(employeeId, hotelId, roomType, checkInDate, checkOutDate)

        result shouldBe Failure(because)
    }
}