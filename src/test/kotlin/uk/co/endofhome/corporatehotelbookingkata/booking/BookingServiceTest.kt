package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Failure
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.HotelDoesNotExist
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.RoomTypeUnavailable
import java.time.LocalDate

internal class BookingServiceTest {
    private val hotelService = HotelService(listOf(
        Hotel(id = exampleHotelId, availableRooms = mapOf(
            RoomType.Single to 1)
        )
    ))
    private val bookingService = BookingService(hotelService)

    @Test
    fun `Check out date must be at least one day after the check in date`() {
        val checkInDate = LocalDate.of(2022, 9, 18)
        val checkOutDate = LocalDate.of(2022, 9, 18)

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        result shouldBe Failure(BookingError.CheckInMustPreceedCheckOut(checkInDate, checkOutDate))
    }

    @Test
    fun `Bookings cannot be made for hotels that do not exist`() {
        val nonExistentHotelId = HotelId("Non-existent")

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = nonExistentHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Failure(HotelDoesNotExist(nonExistentHotelId))
    }

    @Test
    fun `Bookings cannot be made for room types unavailable at the chosen hotel`() {
        val roomType = RoomType.Double

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Double,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Failure(RoomTypeUnavailable(exampleHotelId, roomType))
    }
}