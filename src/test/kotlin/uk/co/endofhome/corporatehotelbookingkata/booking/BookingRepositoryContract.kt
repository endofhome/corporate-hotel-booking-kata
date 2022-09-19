package uk.co.endofhome.corporatehotelbookingkata.booking

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

internal class BookingRepositoryContract {
    private val bookingRepository = InMemoryBookingRepository()

    @Test
    fun `retrieves no bookings`() {
        bookingRepository.all() shouldBe emptyList()
    }

    @Test
    fun `add a booking`() {
        val booking =
            Booking(exampleEmployeeId, exampleHotelId, RoomType.Single, exampleCheckInDate, exampleCheckOutDate)
        bookingRepository.add(booking)
        bookingRepository.all() shouldBe listOf(booking)
    }
}