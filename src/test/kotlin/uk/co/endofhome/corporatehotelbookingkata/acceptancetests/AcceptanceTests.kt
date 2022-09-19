package uk.co.endofhome.corporatehotelbookingkata.acceptancetests

import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.CompanyAdmin
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors.Employee
import uk.co.endofhome.corporatehotelbookingkata.booking.BookingService
import uk.co.endofhome.corporatehotelbookingkata.booking.InMemoryBookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType.Single
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckInDate
import uk.co.endofhome.corporatehotelbookingkata.exampleCheckOutDate
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleHotelId
import uk.co.endofhome.corporatehotelbookingkata.hotel.Hotel
import uk.co.endofhome.corporatehotelbookingkata.hotel.HotelService

class AcceptanceTests {

    @Test
    fun `Employee can book a room`() {
        val hotelService = HotelService(listOf(
            Hotel(
                id = exampleHotelId,
                rooms = mapOf(Single to 1)
            )
        ))
        val bookingPolicyService = BookingPolicyService()
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())
        val edwin = Employee(exampleEmployeeId, bookingService)

        edwin.book(exampleHotelId, Single, exampleCheckInDate, exampleCheckOutDate)
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
        val bookingPolicyService = BookingPolicyService()
        val bookingService = BookingService(hotelService, bookingPolicyService, InMemoryBookingRepository())
        val edwin = Employee(exampleEmployeeId, bookingService)
        val eileen = Employee(EmployeeId("eileen-id"), bookingService)

        edwin.book(exampleHotelId, Single, edwinChecksInDate, eileenChecksInDate)
        eileen.book(exampleHotelId, Single, eileenChecksInDate, eileenChecksOutDate)
    }

    @Test
    fun `Company admin can add employees`() {
        val christina = CompanyAdmin()

        christina.addEmployee(exampleEmployeeId)
    }
}

interface CompanyRepository {
    fun add(companyId: CompanyId, employeeId: EmployeeId)
}

class InMemoryCompanyRepository : CompanyRepository {
    private var companies: Map<CompanyId, List<EmployeeId>> = emptyMap()

    override fun add(companyId: CompanyId, employeeId: EmployeeId) {
        val employees = companies[companyId] ?: emptyList()
        companies = companies + (companyId to (employees + employeeId))
    }

    fun allCompanies() = companies
}

class CompanyService(private val companyRepository: CompanyRepository) {
    fun addEmployee(companyId: CompanyId, employeeId: EmployeeId) {
        companyRepository.add(companyId, employeeId)
    }
}