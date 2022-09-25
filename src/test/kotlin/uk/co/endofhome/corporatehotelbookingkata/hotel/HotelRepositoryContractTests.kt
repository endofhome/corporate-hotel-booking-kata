package uk.co.endofhome.corporatehotelbookingkata.hotel

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.domain.HotelId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleHotelId

internal class HotelRepositoryContractTests {
    private val hotelRepository: HotelRepository = InMemoryHotelRepository()

    @Test
    fun `unknown hotel`() {
        hotelRepository.findHotel { it.id == HotelId("unknown") } shouldBe null
    }

    @Test
    fun `can set room type and find hotel`() {
        val hotelId = exampleHotelId
        val roomType = RoomType.Double
        val quantity = 35

        hotelRepository.setRoomType(hotelId, roomType, quantity )

        hotelRepository.findHotel { it.id == hotelId } shouldBe Hotel(hotelId, mapOf(roomType to quantity))
    }
}