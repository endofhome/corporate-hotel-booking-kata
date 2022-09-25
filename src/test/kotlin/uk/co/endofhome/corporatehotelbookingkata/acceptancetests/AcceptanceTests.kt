package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.*
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.CompanyAdmin
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.Employee
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.HotelManager
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.booking.InMemoryBookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.DefaultBookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.InMemoryBookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType.Single
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.BookingIsAgainstPolicy
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService
import uk.co.endofhome.corporatehotelbookingkata.hotel.InMemoryHotelRepository

class AcceptanceTests {
    private val companyRepository = InMemoryCompanyRepository()
    private val bookingPolicyRepository = InMemoryBookingPolicyRepository()
    private val hotelService = HotelService(InMemoryHotelRepository())
    private val bookingPolicyService = DefaultBookingPolicyService(bookingPolicyRepository, companyRepository)
    private val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

    @Test
    fun `Hotel manager can set number of rooms for each room type and employee can book a room`() {
        val helena = HotelManager(hotelService)
        val edwin = Employee(exampleEmployeeId, bookingService)

        helena.canSetRoomType(exampleHotelId, RoomType.Single, 1)
        edwin.canBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
    }

    @Test
    fun `Two employees can book the same room on consecutive nights`() {
        val edwin = Employee(exampleEmployeeId, bookingService)
        val eileen = Employee(EmployeeId("eileen-id"), bookingService)
        val helena = HotelManager(hotelService)
        val edwinChecksInDate = exampleCheckInDate
        val eileenChecksInDate = edwinChecksInDate.plusDays(1)
        val eileenChecksOutDate = eileenChecksInDate.plusDays(1)
        helena.canSetRoomType(exampleHotelId, RoomType.Single, 1)

        edwin.canBook(exampleHotelId, Single, edwinChecksInDate, eileenChecksInDate)
        eileen.canBook(exampleHotelId, Single, eileenChecksInDate, eileenChecksOutDate)
    }

    @Test
    fun `Company admin can add employees`() {
        val christina = CompanyAdmin()

        christina.canAddEmployee(exampleEmployeeId)
    }

    @Test
    fun `Company admin can delete employees`() {
        val christina = CompanyAdmin(InMemoryBookingRepository(), bookingPolicyRepository)
        val edwin = Employee(exampleEmployeeId, bookingService)
        val helena = HotelManager(hotelService)

        helena.canSetRoomType(exampleHotelId, RoomType.Single, 1)
        christina.canAddEmployee(edwin.employeeId)
        edwin.canBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
        christina.canSetEmployeePolicy(edwin.employeeId, setOf(RoomType.Double))

        christina.canDeleteEmployee(exampleEmployeeId)
    }

    @Test
    fun `Company admin can set booking policy - employee policy`() {
        val christina = CompanyAdmin(bookingPolicyRepository = bookingPolicyRepository)
        val edwin = Employee(exampleEmployeeId, bookingService)

        christina.canSetEmployeePolicy(edwin.employeeId, setOf(RoomType.Double))
        edwin.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
    }

    @Test
    fun `Company admin can set booking policy - company policy`() {
        val christina = CompanyAdmin(bookingPolicyRepository = bookingPolicyRepository, companyRepository = companyRepository, companyId = exampleCompanyId)
        val edwin = Employee(exampleEmployeeId, bookingService)
        val emilio = Employee(EmployeeId("emilio"), bookingService)

        christina.canAddEmployee(edwin.employeeId)
        christina.canAddEmployee(emilio.employeeId)

        christina.canSetCompanyPolicy(setOf(RoomType.Double))
        edwin.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
        emilio.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
    }
}