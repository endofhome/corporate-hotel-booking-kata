package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.CompanyAdmin
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.Employee
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.booking.InMemoryBookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.InMemoryBookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType.Single
import uk.co.endofhome.corporatehotelbookingkata.domain.errors.BookingError.BookingIsAgainstPolicy
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.hotel.Hotel
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService

class AcceptanceTests {
    private val hotelService = HotelService(listOf(
        Hotel(
            id = exampleHotelId,
            rooms = mapOf(Single to 1)
        )
    ))
    private val companyRepository = InMemoryCompanyRepository()
    private val bookingPolicyRepository = InMemoryBookingPolicyRepository()
    private val bookingPolicyService = BookingPolicyService(bookingPolicyRepository, companyRepository)
    private val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())

    @Test
    fun `Employee can book a room`() {
        val edwin = Employee(exampleEmployeeId, bookingService)

        edwin.canBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
    }

    @Test
    fun `Two employees can book the same room on consecutive nights`() {
        val edwinChecksInDate = exampleCheckInDate
        val eileenChecksInDate = edwinChecksInDate.plusDays(1)
        val eileenChecksOutDate = eileenChecksInDate.plusDays(1)
        val hotelService = HotelService(listOf(
            Hotel(
                id = exampleHotelId,
                rooms = mapOf(Single to 1)
            )
        ))
        val bookingPolicyService = BookingPolicyService(InMemoryBookingPolicyRepository(), companyRepository)
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())
        val edwin = Employee(exampleEmployeeId, bookingService)
        val eileen = Employee(EmployeeId("eileen-id"), bookingService)

        edwin.canBook(exampleHotelId, Single, edwinChecksInDate, eileenChecksInDate)
        eileen.canBook(exampleHotelId, Single, eileenChecksInDate, eileenChecksOutDate)
    }

    @Test
    fun `Company admin can add employees`() {
        val christina = CompanyAdmin()

        christina.addEmployee(exampleEmployeeId)
    }

    @Test
    fun `Company admin can delete employees`() {
        val bookingRepository = InMemoryBookingRepository()
        val bookingPolicyRepository = InMemoryBookingPolicyRepository()
        val christina = CompanyAdmin(bookingRepository = bookingRepository, bookingPolicyRepository = bookingPolicyRepository)
        val edwin = Employee(exampleEmployeeId, bookingService)

        christina.addEmployee(edwin.employeeId)
        edwin.canBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
        bookingPolicyRepository.add(EmployeePolicy(edwin.employeeId, setOf(RoomType.Double)))

        christina.deleteEmployee(exampleEmployeeId)
    }

    @Test
    fun `Company admin can set booking policy - employee policy`() {
        val christina = CompanyAdmin(bookingPolicyRepository = bookingPolicyRepository)

        val edwin = Employee(exampleEmployeeId, bookingService)
        christina.setEmployeePolicy(edwin.employeeId, setOf(RoomType.Double))

        edwin.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
    }

    @Test
    fun `Company admin can set booking policy - company policy`() {
        val christina = CompanyAdmin(bookingPolicyRepository = bookingPolicyRepository, companyRepository = companyRepository)
        val edwin = Employee(exampleEmployeeId, bookingService)
        val emilio = Employee(EmployeeId("emilio"), bookingService)

        christina.addEmployee(edwin.employeeId)
        christina.addEmployee(emilio.employeeId)
        christina.setCompanyPolicy(setOf(RoomType.Double))

        edwin.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
        emilio.cannotBook(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate, because = BookingIsAgainstPolicy)
    }
}