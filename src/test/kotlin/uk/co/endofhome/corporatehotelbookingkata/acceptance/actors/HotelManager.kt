package uk.co.endofhome.corporatehotelbookingkata.acceptance.actors

import io.kotest.matchers.shouldBe
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService

class HotelManager(private val hotelService: HotelService) {
    fun canSetRoomType(hotelId: HotelId, roomType: RoomType, quantity: Int){
        hotelService.setRoomType(hotelId, roomType, quantity)

        hotelService.findHotelBy(hotelId)?.rooms?.get(roomType) shouldBe quantity
    }
}