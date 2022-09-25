package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class BookingPolicyRepositoryContractTests {
    private val bookingPolicyRepository: BookingPolicyRepository = InMemoryBookingPolicyRepository()

    @Test
    fun `get booking policy for employee`() {
        val bookingPolicy = EmployeePolicy(exampleEmployeeId, setOf(RoomType.Single))

        bookingPolicyRepository.add(bookingPolicy)

        bookingPolicyRepository.findPolicyFor(bookingPolicy.employeeId) shouldBe bookingPolicy
    }

    @Test
    fun `get booking policy for company`() {
        val bookingPolicy = CompanyPolicy(exampleCompanyId, setOf(RoomType.Single))

        bookingPolicyRepository.add(bookingPolicy)

        bookingPolicyRepository.findPolicyFor(bookingPolicy.companyId) shouldBe bookingPolicy
    }

    @Test
    fun `delete booking policies for employee`() {
        val employeePolicy = EmployeePolicy(exampleEmployeeId, setOf(RoomType.Single))
        bookingPolicyRepository.add(employeePolicy)

        bookingPolicyRepository.deletePoliciesFor(employeePolicy.employeeId)

        bookingPolicyRepository.findPolicyFor(employeePolicy.employeeId) shouldBe null
    }
}