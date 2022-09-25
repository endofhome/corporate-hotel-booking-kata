package uk.co.endofhome.corporatehotelbookingkata.hotel

import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

interface HotelRepository {
    fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int)
    fun find(predicate: (Hotel) -> Boolean): Hotel?
}

class InMemoryHotelRepository : HotelRepository {
    private var hotels: List<Hotel> = emptyList()

    override fun setRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int) {
        val foundHotel = hotels.find { it.id == hotelId }
        val hotelToUpdate = foundHotel ?: Hotel(hotelId, emptyMap())
        val updatedHotel = hotelToUpdate.copy(rooms = hotelToUpdate.rooms + (roomType to quantity))

        hotels = (foundHotel?.let { hotels - it } ?: hotels) + updatedHotel
    }

    override fun find(predicate: (Hotel) -> Boolean) = hotels.find(predicate)
}