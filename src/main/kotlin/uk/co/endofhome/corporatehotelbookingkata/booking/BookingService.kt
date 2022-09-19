package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.flatMap
import uk.co.endofhome.corporatehotelbookingkata.domain.*
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import uk.co.endofhome.corporatehotelbookingkata.result.asFailure
import uk.co.endofhome.corporatehotelbookingkata.result.asSuccess
import java.time.LocalDate

class BookingService(private val hotelService: HotelService, private val bookingPolicyService: BookingPolicyService) {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Booking, BookingError> =
        validateDates(checkInDate, checkOutDate)
            .flatMap { findHotel(hotelId) }
            .flatMap { hotel ->
                val roomAvailability = getRoomAvailability(hotel, roomType, checkInDate, checkOutDate)
                validateRoomTypeExists(hotelId, roomType, roomAvailability)
                    .flatMap { validateRoomIsAvailable(hotelId, roomType, roomAvailability)
                        .flatMap { BookingIsAgainstPolicy.asFailure() }
                }
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
        checkOutDate: LocalDate
    ): List<RoomAvailability> {
        val allBookingDates: Sequence<LocalDate> =
            generateSequence(checkInDate) { it.plusDays(1) }.takeWhile { it < checkOutDate }

        return allBookingDates.map { date -> RoomAvailability(date, hotel.availability[date]?.get(roomType)) }.toList()
    }

    private fun findHotel(hotelId: HotelId): Result4k<Hotel, HotelDoesNotExist> =
        hotelService.findHotelBy(hotelId).let { maybeHotel ->
            maybeHotel?.asSuccess() ?: HotelDoesNotExist(hotelId).asFailure()
        }

    private fun validateDates(checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Unit, CheckInMustPreceedCheckOut> =
        if (checkInDate >= checkOutDate) {
            CheckInMustPreceedCheckOut(checkInDate, checkOutDate).asFailure()
        } else {
            Unit.asSuccess()
        }
}

data class RoomAvailability(val date: LocalDate, val availability: Int?)

// At the moment, the Hotel Service is only required as a collaborator of BookingService, so it lives here.
// TODO move state to a repository object
class HotelService(private val hotelRepository: List<Hotel>) {
    fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int) {
        TODO("Not yet implemented")
    }

    fun findHotelBy(hotelId: HotelId): Hotel? {
        return hotelRepository.find { it.id == hotelId }
    }

}
data class Hotel(val id: HotelId, val availability: Map<LocalDate,Map<RoomType, Int>>)

// At the moment, the Booking Policy Service is only required as a collaborator of BookingService, so it lives here.
class BookingPolicyService {
    fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>) {
        TODO("Not yet implemented")
    }
    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        TODO("Not yet implemented")
    }
    fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean = false
}

sealed class BookingPolicyType {
    data class CompanyPolicy(val bookingPolicy: BookingPolicy) : BookingPolicyType()
}

sealed class BookingPolicy {
    data class RoomTypeNotAllowed(val roomTypeAttempted: RoomType, val roomTypesAllowed: Set<RoomType>) : BookingPolicy()
}