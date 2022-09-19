package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import java.time.LocalDate

internal class BookingServiceTest {
    private val hotelService = HotelService(
        listOf(
            Hotel(
                id = exampleHotelId,
                availability = mapOf(
                    exampleCheckInDate to mapOf(RoomType.Single to 1),
                )
            )
        )
    )
    private val bookingPolicyService = BookingPolicyService()
    private val bookingService = BookingService(hotelService, bookingPolicyService)

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

        result shouldBe Failure(RoomTypeDoesNotExist(exampleHotelId, roomType))
    }

    @Test
    fun `Bookings cannot be made if they are against the booking policy`() {
        val bookingNotAllowedBookingPolicyService = object : IBookingPolicyService by BookingPolicyService() {
            override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean = false
        }
        val bookingService = BookingService(hotelService, bookingNotAllowedBookingPolicyService)

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Failure(BookingIsAgainstPolicy)
    }

    @Test
    fun `Bookings cannot be made if a room isn't available for the duration of the booking`() {
        val hotelService = HotelService(listOf(
            Hotel(
                id = exampleHotelId,
                availability = mapOf(
                    exampleCheckInDate to mapOf(RoomType.Single to 1)
                )
            )
        ))
        val bookingService = BookingService(hotelService, bookingPolicyService)

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckInDate.plusDays(2)
        )

        result shouldBe Failure(RoomTypeUnavailable(
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            onDates = listOf(exampleCheckInDate.plusDays(1))
        ))
    }

    @Test
    fun `Valid booking can be made`() {
        val bookingService = BookingService(hotelService, bookingPolicyService)

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Success(Booking)
    }
}