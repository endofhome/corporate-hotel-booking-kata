package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Result4k
import uk.co.endofhome.corporatehotelbookingkata.domain.*
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import uk.co.endofhome.corporatehotelbookingkata.result.asFailure
import java.time.LocalDate

class BookingService(private val hotelService: HotelService, private val bookingPolicyService: BookingPolicyService) {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Booking, BookingError> {
        if (checkInDate >= checkOutDate) {
            return CheckInMustPreceedCheckOut(checkInDate, checkOutDate).asFailure()
        }

        val hotel = hotelService.findHotelBy(hotelId)

        return (if (hotel == null) {
            HotelDoesNotExist(hotelId)
        } else if (hotel.availableRooms[roomType] == null || hotel.availableRooms[roomType]!! <= 0) {
            RoomTypeUnavailable(hotelId, roomType)
        } else {
            BookingIsAgainstPolicy
        }).asFailure()
    }

}

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
data class Hotel(val id: HotelId, val availableRooms: Map<RoomType, Int>)

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