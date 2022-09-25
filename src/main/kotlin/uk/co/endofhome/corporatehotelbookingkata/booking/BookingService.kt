package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.flatMap
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import uk.co.endofhome.corporatehotelbookingkata.hotel.Hotel
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService
import uk.co.endofhome.corporatehotelbookingkata.result.asFailure
import uk.co.endofhome.corporatehotelbookingkata.result.asSuccess
import java.time.LocalDate

class BookingService(
    private val hotelService: HotelService,
    private val bookingPolicyService: BookingPolicyService,
    private val bookingRepository: InMemoryBookingRepository
) {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result<Booking, BookingError> =
        validateDates(checkInDate, checkOutDate)
            .flatMap { findHotel(hotelId) }
            .flatMap { hotel ->
                val roomAvailability = getRoomAvailability(hotel, roomType, checkInDate, checkOutDate, bookingRepository)
                validateRoomTypeExists(hotelId, roomType, roomAvailability)
                    .flatMap { validateRoomIsAvailable(hotelId, roomType, roomAvailability) }
            }.flatMap { add(Booking(employeeId, hotelId, roomType, checkInDate, checkOutDate)) }

    private fun add(booking: Booking) =
        if (bookingPolicyService.isBookingAllowed(booking.employeeId, booking.roomType)) {
            bookingRepository.add(booking)
            booking.asSuccess()
        } else {
            BookingIsAgainstPolicy.asFailure()
        }

    private fun validateRoomIsAvailable(hotelId: HotelId, roomType: RoomType, roomAvailability: List<RoomAvailability>): Result4k<Unit, BookingError> {
        val unavailableDates = roomAvailability
            .filter { (_, availableRooms) -> availableRooms == null || availableRooms <= 0 }
            .toList()

        return if (unavailableDates.isNotEmpty()) {
            RoomTypeUnavailable(hotelId, roomType, unavailableDates.map { it.date }).asFailure()
        } else {
            Unit.asSuccess()
        }
    }

    private fun validateRoomTypeExists(
        hotelId: HotelId,
        roomType: RoomType,
        roomAvailability: List<RoomAvailability>,
    ): Result4k<Unit, BookingError> {
        return if (roomAvailability.all { it.availability == null }) {
            RoomTypeDoesNotExist(hotelId, roomType).asFailure()
        } else {
            Unit.asSuccess()
        }
    }

    private fun getRoomAvailability(
        hotel: Hotel,
        roomType: RoomType,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        bookingRepository: InMemoryBookingRepository
    ): List<RoomAvailability> {
        val allBookingDates: Sequence<LocalDate> =
            generateSequence(checkInDate) { it.plusDays(1) }.takeWhile { it < checkOutDate }

        return allBookingDates.map { date ->
            val numberOfRoomTypeInHotel = hotel.rooms[roomType]
            val bookingsForThisRoom = bookingRepository.allBookings()
                .filter { it.hotelId == hotel.id }
                .filter { it.roomType == roomType }
                .filter { (it.from..it.to.minusDays(1)).contains(date) }
                .fold(0) { acc, _ -> acc + 1 }

            RoomAvailability(date, numberOfRoomTypeInHotel?.minus(bookingsForThisRoom))
        }.toList()
    }

    private fun findHotel(hotelId: HotelId): Result4k<Hotel, HotelDoesNotExist> =
        hotelService.findHotelBy(hotelId).let { maybeHotel ->
            maybeHotel?.asSuccess() ?: HotelDoesNotExist(hotelId).asFailure()
        }

    private fun validateDates(checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Unit, CheckInMustPrecedeCheckOut> =
        if (checkInDate >= checkOutDate) {
            CheckInMustPrecedeCheckOut(checkInDate, checkOutDate).asFailure()
        } else {
            Unit.asSuccess()
        }
}

data class RoomAvailability(val date: LocalDate, val availability: Int?)