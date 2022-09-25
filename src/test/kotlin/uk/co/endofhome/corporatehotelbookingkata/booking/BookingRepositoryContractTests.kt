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

    @Test
    fun `add a booking`() {
        val booking = Booking(exampleEmployeeId, exampleHotelId, RoomType.Single, exampleCheckInDate, exampleCheckOutDate)
        bookingRepository.add(booking)
        bookingRepository.getBookingsFor(booking.hotelId, booking.roomType, booking.from) shouldBe listOf(booking)
    }

    @Test
    fun `delete all bookings for employee`() {
        val firstBooking = Booking(exampleEmployeeId, exampleHotelId, RoomType.Single, exampleCheckInDate, exampleCheckOutDate)
        val secondBooking = firstBooking.copy(employeeId = EmployeeId("another-employee-id"))
        val thirdBooking = firstBooking.copy(hotelId = HotelId("another-hotel-id"))
        bookingRepository.add(firstBooking)
        bookingRepository.add(secondBooking)
        bookingRepository.add(thirdBooking)

        bookingRepository.deleteBookingsFor(exampleEmployeeId)

        bookingRepository.getBookingsFor(firstBooking.hotelId, firstBooking.roomType, firstBooking.from) shouldBe listOf(secondBooking)
    }
}