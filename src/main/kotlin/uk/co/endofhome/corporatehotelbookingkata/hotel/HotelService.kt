package uk.co.endofhome.corporatehotelbookingkata.hotel

import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

class HotelService(private val hotelRepository: InMemoryHotelRepository) {
    fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int) {
        hotelRepository.setRoomType(hotelId, roomType, quantity)
    }

    fun findHotelBy(hotelId: HotelId): Hotel? {
        return hotelRepository.find { it.id == hotelId }
    }
}

data class Hotel(val id: HotelId, val rooms: Map<RoomType, Int>)