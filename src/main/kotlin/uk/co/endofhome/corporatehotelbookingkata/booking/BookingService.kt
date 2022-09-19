package uk.co.endofhome.corporatehotelbookingkata.booking

import dev.forkhandles.result4k.Result4k
import uk.co.endofhome.corporatehotelbookingkata.domain.Booking
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.*
import uk.co.endofhome.corporatehotelbookingkata.result.asFailure
import java.time.LocalDate

class BookingService(private val hotelService: HotelService) {
    fun book(employeeId: EmployeeId, hotelId: HotelId, roomType: RoomType, checkInDate: LocalDate, checkOutDate: LocalDate): Result4k<Booking, BookingError> =
        (if ((checkInDate >= checkOutDate)) {
            CheckInMustPreceedCheckOut(checkInDate, checkOutDate)
        } else if ((hotelService.findHotelBy(hotelId) == null)) {
            HotelDoesNotExist(hotelId)
        } else {
            RoomTypeUnavailable(hotelId, roomType)
        }).asFailure()
}

// At the moment, the hotel service is only required as a collaborator of BookingService, so it lives here.
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