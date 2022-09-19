package uk.co.endofhome.corporatehotelbookingkata.hotel

import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

// TODO move state to a repository object
class HotelService(private val hotelRepository: List<Hotel>) {
    fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int) {
        TODO("Not yet implemented")
    }

    fun findHotelBy(hotelId: HotelId): Hotel? {
        return hotelRepository.find { it.id == hotelId }
    }

}

data class Hotel(val id: HotelId, val rooms: Map<RoomType, Int>)