package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.DefaultBookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.InMemoryBookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService
import uk.co.endofhome.corporatehotelbookingkata.hotel.InMemoryHotelRepository
import uk.co.endofhome.corporatehotelbookingkata.result.expectSuccess
import java.time.LocalDate

internal class BookingServiceTest {
    private val hotelService = HotelService(InMemoryHotelRepository()).also {
        it.setRoomType(exampleHotelId, RoomType.Single, 1)
    }
    private val companyRepository = InMemoryCompanyRepository()
    private val bookingPolicyService = DefaultBookingPolicyService(InMemoryBookingPolicyRepository(),companyRepository)
    private val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

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

        result shouldBe Failure(CheckInMustPrecedeCheckOut(checkInDate, checkOutDate))
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
        val bookingNotAllowedBookingPolicyService = object : BookingPolicyService by DefaultBookingPolicyService(InMemoryBookingPolicyRepository(), companyRepository) {
            override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean = false
        }
        val bookingService = BookingService(hotelService, bookingNotAllowedBookingPolicyService, InMemoryBookingRepository())

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
        val firstDay = exampleCheckInDate
        val secondDay = exampleCheckOutDate
        val hotelService = HotelService(InMemoryHotelRepository()).also {
            it.setRoomType(exampleHotelId, RoomType.Single, 1)
        }
        val bookingRepository = InMemoryBookingRepository()
        val bookingService = BookingService(hotelService, bookingPolicyService, bookingRepository)

        bookingRepository.add(
            Booking(
                employeeId = exampleEmployeeId,
                hotelId = exampleHotelId,
                roomType = RoomType.Single,
                from = secondDay,
                to = secondDay.plusDays(1)
            )
        )

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = firstDay,
            checkOutDate = firstDay.plusDays(2)
        )

        result shouldBe Failure(
            RoomTypeUnavailable(
                hotelId = exampleHotelId,
                roomType = RoomType.Single,
                onDates = listOf(firstDay.plusDays(1))
            ))
    }

    @Test
    fun `Room cannot be booked twice`() {
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

        val firstResult = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        val secondResult = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        firstResult shouldBe Success(Booking(exampleEmployeeId, exampleHotelId, RoomType.Single, exampleCheckInDate, exampleCheckOutDate))

        secondResult shouldBe Failure(RoomTypeUnavailable(
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            onDates = listOf(exampleCheckInDate)
        ))
    }

    @Test
    fun `Valid booking can be made`() {
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Success(Booking(exampleEmployeeId, exampleHotelId, RoomType.Single, exampleCheckInDate, exampleCheckOutDate))
    }

    @Test
    fun `A change in quantity of rooms should not affect existing bookings`() {
        val bookingRepository = InMemoryBookingRepository()
        val bookingService = BookingService(hotelService, bookingPolicyService, bookingRepository)
        hotelService.setRoomType(exampleHotelId, RoomType.Single, 1)

        val booking = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = exampleHotelId,
            roomType = RoomType.Single,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        ).expectSuccess()

        hotelService.setRoomType(exampleHotelId, RoomType.Single, 0)

        val existingBookings = bookingRepository.getBookingsFor(exampleHotelId, RoomType.Single, exampleCheckInDate)

        existingBookings.single() shouldBe booking
    }

}