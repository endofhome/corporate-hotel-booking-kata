package uk.co.endofhome.corporatehotelbookingkata.domain

import java.time.LocalDate

data class Booking(val employeeId: EmployeeId, val hotelId: HotelId, val roomType: RoomType, val from: LocalDate, val to: LocalDate)

@JvmInline
value class EmployeeId(val value: String)

@JvmInline
value class HotelId(val value: String)

@JvmInline
value class CompanyId(val value: String)

enum class RoomType {
    Single,
    Double
}

object BookingConfirmation