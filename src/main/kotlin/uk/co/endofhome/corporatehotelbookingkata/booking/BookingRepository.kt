package uk.co.endofhome.corporatehotelbookingkata.booking

import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import java.time.LocalDate

interface BookingRepository {
    fun getBookingsFor(hotelId: HotelId, roomType: RoomType, date: LocalDate): List<Booking>
    fun add(booking: Booking)
    fun deleteBookingsFor(employeeId: EmployeeId)
}

class InMemoryBookingRepository : BookingRepository {
    private var bookings: List<Booking> = listOf()

    fun allBookings(): List<Booking> = bookings

    override fun getBookingsFor(hotelId: HotelId, roomType: RoomType, date: LocalDate): List<Booking> =
        bookings.filter { it.hotelId == hotelId && it.roomType == roomType && (it.from..(it.to.minusDays(1))).contains(date) }

    override fun add(booking: Booking) {
        bookings = bookings + booking
    }

    override fun deleteBookingsFor(employeeId: EmployeeId) {
        bookings = bookings.filter { it.employeeId != employeeId }
    }
}