package uk.co.endofhome.corporatehotelbookingkata.domain

@JvmInline
value class EmployeeId(val value: String)
@JvmInline
value class HotelId(val value: String)
enum class RoomType {
    Single,
    Double
}
object Booking