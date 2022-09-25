package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
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
    private val bookingRepository: BookingRepository
) {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result<Booking, BookingError> =
        validateDates(checkInDate, checkOutDate)
            .flatMap { validateAllowedByBookingPolicy(employeeId, roomType) }
            .flatMap { findHotel(hotelId) }
            .flatMap { hotel ->
                val roomAvailabilityOnBookingDates = getRoomAvailability(hotel, roomType, checkInDate, checkOutDate)
                validateRoomTypeExists(hotelId, roomType, roomAvailabilityOnBookingDates)
                    .flatMap { validateRoomIsAvailable(hotelId, roomType, roomAvailabilityOnBookingDates) }
            }
            .map {
                Booking(employeeId, hotelId, roomType, checkInDate, checkOutDate).also {
                    bookingRepository.add(it)
                }
            }

    private fun validateDates(checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Unit, CheckInMustPrecedeCheckOut> =
        if (checkInDate < checkOutDate) {
            Unit.asSuccess()
        } else {
            CheckInMustPrecedeCheckOut(checkInDate, checkOutDate).asFailure()
        }

    private fun validateAllowedByBookingPolicy(employeeId: EmployeeId, roomType: RoomType): Result4k<Unit, BookingError> =
        if (bookingPolicyService.isBookingAllowed(employeeId, roomType)) {
            Unit.asSuccess()
        } else {
            BookingIsAgainstPolicy.asFailure()
        }

    private fun findHotel(hotelId: HotelId): Result4k<Hotel, HotelDoesNotExist> =
        hotelService.findHotelBy(hotelId).let { maybeHotel ->
            maybeHotel?.asSuccess() ?: HotelDoesNotExist(hotelId).asFailure()
        }

    private fun getRoomAvailability(
        hotel: Hotel,
        roomType: RoomType,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
    ): List<RoomAvailabilityOnDate> {
        val allBookingDates: Sequence<LocalDate> =
            generateSequence(checkInDate) { it.plusDays(1) }.takeWhile { it < checkOutDate }

        return allBookingDates.map { date ->
            val numberOfRoomTypeInHotel = hotel.rooms[roomType]
            val bookingsForThisRoom = bookingRepository.getBookingsFor(hotel.id, roomType, date)
                .fold(0) { acc, _ -> acc + 1 }
            RoomAvailabilityOnDate(date, numberOfRoomTypeInHotel?.minus(bookingsForThisRoom))
        }.toList()
    }

    private fun validateRoomTypeExists(
        hotelId: HotelId,
        roomType: RoomType,
        roomAvailabilityOnDate: List<RoomAvailabilityOnDate>,
    ): Result4k<Unit, BookingError> {
        return if (roomAvailabilityOnDate.all { it.availability != null }) {
            Unit.asSuccess()
        } else {
            RoomTypeDoesNotExist(hotelId, roomType).asFailure()
        }
    }

    private fun validateRoomIsAvailable(hotelId: HotelId, roomType: RoomType, roomAvailabilityOnDate: List<RoomAvailabilityOnDate>): Result4k<Unit, BookingError> {
        val unavailableDates = roomAvailabilityOnDate
            .filter { (_, availableRooms) -> availableRooms == null || availableRooms <= 0 }
            .toList()

        return if (unavailableDates.isEmpty()) {
            Unit.asSuccess()
        } else {
            RoomTypeUnavailable(hotelId, roomType, unavailableDates.map { it.date }).asFailure()
        }
    }
}

data class RoomAvailabilityOnDate(val date: LocalDate, val availability: Int?)