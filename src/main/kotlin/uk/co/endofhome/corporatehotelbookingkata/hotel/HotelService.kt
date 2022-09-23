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

class InMemoryHotelRepository {
    private var hotels: List<Hotel> = emptyList()

    fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int) {
        val foundHotel = hotels.find { it.id == hotelId }
        val hotelToUpdate = foundHotel ?: Hotel(hotelId, emptyMap())
        val updatedHotel = hotelToUpdate.copy(rooms = hotelToUpdate.rooms + (roomType to quantity))

        hotels = (foundHotel?.let { hotels - it } ?: hotels) + updatedHotel
    }

    fun find(predicate: (Hotel) -> Boolean) = hotels.find(predicate)
}