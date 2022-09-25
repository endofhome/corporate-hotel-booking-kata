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

internal class BookingServiceTests {
    private val employeeId = exampleEmployeeId
    private val hotelId = exampleHotelId
    private val roomType = RoomType.Single
    private val checkInDate = exampleCheckInDate
    private val checkOutDate = exampleCheckOutDate

    private val hotelService = HotelService(InMemoryHotelRepository()).also {
        it.setRoomType(hotelId, RoomType.Single, 1)
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
        val hotelService = HotelService(InMemoryHotelRepository())
        hotelService.setRoomType(hotelId, RoomType.Single, 1)
        val doubleRoom = RoomType.Double

        val result = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = hotelId,
            roomType = doubleRoom,
            checkInDate = exampleCheckInDate,
            checkOutDate = exampleCheckOutDate
        )

        result shouldBe Failure(RoomTypeDoesNotExist(hotelId, doubleRoom))
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

        val hotelService = HotelService(InMemoryHotelRepository())
        val bookingRepository = InMemoryBookingRepository()
        val bookingService = BookingService(hotelService, bookingPolicyService, bookingRepository)

        hotelService.setRoomType(hotelId, roomType, 1)
        bookingRepository.add(
            Booking(
                employeeId = employeeId,
                hotelId = hotelId,
                roomType = roomType,
                from = secondDay,
                to = secondDay.plusDays(1)
            )
        )

        val result = bookingService.book(
            employeeId = employeeId,
            hotelId = hotelId,
            roomType = roomType,
            checkInDate = firstDay,
            checkOutDate = firstDay.plusDays(2)
        )

        result shouldBe Failure(
            RoomTypeUnavailable(
                hotelId = hotelId,
                roomType = roomType,
                onDates = listOf(firstDay.plusDays(1))
            ))
    }

    @Test
    fun `Room cannot be booked twice`() {
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

        val firstResult = bookingService.book(
            employeeId = employeeId,
            hotelId = hotelId,
            roomType = roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        val secondResult = bookingService.book(
            employeeId = employeeId,
            hotelId = hotelId,
            roomType = roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        firstResult shouldBe Success(
            Booking(employeeId, hotelId, roomType, checkInDate, checkOutDate)
        )

        secondResult shouldBe Failure(
            RoomTypeUnavailable(
                hotelId = hotelId,
                roomType = roomType,
                onDates = listOf(checkInDate)
            )
        )
    }

    @Test
    fun `Valid booking can be made`() {
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

        val result = bookingService.book(
            employeeId = employeeId,
            hotelId = hotelId,
            roomType = roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        result shouldBe Success(Booking(employeeId, hotelId, roomType, checkInDate, checkOutDate))
    }

    @Test
    fun `A change in quantity of rooms should not affect existing bookings`() {
        val bookingRepository = InMemoryBookingRepository()
        val bookingService = BookingService(hotelService, bookingPolicyService, bookingRepository)
        hotelService.setRoomType(hotelId, roomType, 1)

        val booking = bookingService.book(
            employeeId = exampleEmployeeId,
            hotelId = hotelId,
            roomType = roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        ).expectSuccess()

        hotelService.setRoomType(hotelId, roomType, 0)

        val existingBookings = bookingRepository.getBookingsFor(hotelId, roomType, checkInDate)

        existingBookings.single() shouldBe booking
    }
}