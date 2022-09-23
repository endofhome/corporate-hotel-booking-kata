package uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldNotBe
import uk.co.endofhome.corporatehotelbookingkata.booking.InMemoryBookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyService
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.InMemoryBookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.company.CompanyService
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId

class CompanyAdmin(
    private val bookingRepository: InMemoryBookingRepository = InMemoryBookingRepository(),
    private val bookingPolicyRepository: InMemoryBookingPolicyRepository = InMemoryBookingPolicyRepository(),
    private val companyRepository: InMemoryCompanyRepository = InMemoryCompanyRepository()
) {
    private val companyId = exampleCompanyId
    private val companyService = CompanyService(companyRepository, bookingRepository, bookingPolicyRepository)
    private val bookingPolicyService = BookingPolicyService(bookingPolicyRepository, companyRepository)

    fun addEmployee(employeeId: EmployeeId) {
        companyService.addEmployee(companyId, employeeId)

        employeeId shouldBeIn(companyRepository.allCompanies()[companyId]!!)
    }

    fun deleteEmployee(employeeId: EmployeeId) {
        companyService.deleteEmployee(employeeId)

        companyRepository.allCompanies().values.shouldNotContain(employeeId)
        bookingRepository.allBookings().map { it.employeeId }.shouldNotContain(employeeId)
        bookingPolicyRepository.allBookingPolicies().filterIsInstance<EmployeePolicy>().map { it.employeeId }
            .shouldNotContain(employeeId)
    }

    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        bookingPolicyService.setEmployeePolicy(employeeId, roomTypes)

        bookingPolicyRepository.allBookingPolicies().find {
            it == EmployeePolicy(employeeId, roomTypes)
        } shouldNotBe null
    }

    fun setCompanyPolicy(roomTypes: Set<RoomType>) {
        bookingPolicyService.setCompanyPolicy(companyId, roomTypes)

        bookingPolicyRepository.allBookingPolicies().find {
            it == CompanyPolicy(companyId, roomTypes)
        } shouldNotBe null
    }
}