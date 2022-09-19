package uk.co.endofhome.corporatehotelbookingkata.domain.errors

import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import java.time.LocalDate

sealed class BookingError{
    data class CheckInMustPreceedCheckOut(val checkInDate: LocalDate, val checkOutDate: LocalDate) : BookingError()
    data class HotelDoesNotExist(val hotelId: HotelId) : BookingError()
    data class RoomTypeUnavailable(val hotelId: HotelId, val roomType: RoomType) : BookingError()
    object BookingIsAgainstPolicy : BookingError()
}