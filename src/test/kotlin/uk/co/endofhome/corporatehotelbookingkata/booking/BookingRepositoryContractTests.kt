package uk.co.endofhome.corporatehotelbookingkata.booking

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleHotelId

internal class BookingRepositoryContractTests {
    private val bookingRepository: BookingRepository = InMemoryBookingRepository()
    private val employeeId = exampleEmployeeId
    private val hotelId = exampleHotelId
    private val checkInDate = exampleCheckInDate
    private val checkOutDate = exampleCheckOutDate

    @Test
    fun `add a booking`() {
        val booking = Booking(employeeId, hotelId, RoomType.Single, checkInDate, checkOutDate)

        bookingRepository.add(booking)

        bookingRepository.getBookingsFor(booking.hotelId, booking.roomType, booking.from) shouldBe listOf(booking)
    }

    @Test
    fun `delete all bookings for employee`() {
        val firstBooking = Booking(employeeId, hotelId, RoomType.Single, checkInDate, checkOutDate)
        val secondBooking = firstBooking.copy(employeeId = EmployeeId("another-employee-id"))
        val thirdBooking = firstBooking.copy(hotelId = HotelId("another-hotel-id"))
        bookingRepository.add(firstBooking)
        bookingRepository.add(secondBooking)
        bookingRepository.add(thirdBooking)

        bookingRepository.deleteBookingsFor(employeeId)

        bookingRepository.getBookingsFor(firstBooking.hotelId, firstBooking.roomType, firstBooking.from) shouldBe listOf(secondBooking)
    }
}