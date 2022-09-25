package uk.co.endofhome.corporatehotelbookingkata.hotel

import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

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