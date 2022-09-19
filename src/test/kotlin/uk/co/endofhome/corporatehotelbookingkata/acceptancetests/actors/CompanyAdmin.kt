package uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors

import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import uk.co.endofhome.corporatehotelbookingkata.booking.InMemoryBookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyType.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.InMemoryBookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.company.CompanyService
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId

class CompanyAdmin(
    private val bookingRepository: InMemoryBookingRepository = InMemoryBookingRepository(),
    private val bookingPolicyRepository: InMemoryBookingPolicyRepository = InMemoryBookingPolicyRepository()
) {
    private val companyId = exampleCompanyId
    private val companyRepository = InMemoryCompanyRepository()
    private val companyService = CompanyService(companyRepository, bookingRepository, bookingPolicyRepository)

    fun addEmployee(employeeId: EmployeeId) {
        companyService.addEmployee(companyId, employeeId)

        companyRepository.allCompanies()[companyId] shouldBe listOf(employeeId)
    }

    fun deleteEmployee(employeeId: EmployeeId) {
        companyService.deleteEmployee(employeeId)

        companyRepository.allCompanies().values.shouldNotContain(employeeId)
        bookingRepository.allBookings().map { it.employeeId }.shouldNotContain(employeeId)
        bookingPolicyRepository.allBookingPolicies().filterIsInstance<EmployeePolicy>().map { it.employeeId }
            .shouldNotContain(employeeId)
    }
}