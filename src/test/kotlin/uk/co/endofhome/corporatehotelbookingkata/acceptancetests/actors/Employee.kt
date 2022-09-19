package uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors

import dev.forkhandles.result4k.Success
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.domain.BookingConfirmation
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import java.time.LocalDate

class Employee(private val employeeId: EmployeeId, private val bookingService: BookingService) {

    fun book(hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate){
        val result = bookingService.book(employeeId, hotelId, roomType, checkInDate, checkOutDate)

        result.shouldBeInstanceOf<Success<BookingConfirmation>>()
    }
}