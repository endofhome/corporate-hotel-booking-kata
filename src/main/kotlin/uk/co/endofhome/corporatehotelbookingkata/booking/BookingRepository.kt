package uk.co.endofhome.corporatehotelbookingkata.booking

import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

interface BookingRepository {
    fun add(booking: Booking)
    fun deleteBookingsFor(employeeId: EmployeeId)
}

class InMemoryBookingRepository : BookingRepository {
    private var bookings: List<Booking> = listOf()

    fun allBookings(): List<Booking> = bookings

    override fun add(booking: Booking) {
        bookings = bookings + booking
    }

    override fun deleteBookingsFor(employeeId: EmployeeId) {
        bookings = bookings.filter { it.employeeId != employeeId }
    }
}