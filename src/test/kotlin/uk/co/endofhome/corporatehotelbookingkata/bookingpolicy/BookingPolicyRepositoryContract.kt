package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class BookingPolicyRepositoryContract {
    private val bookingPolicyRepository = InMemoryBookingPolicyRepository()

    @Test
    fun `get all booking policies for employee`() {
        val bookingPolicy = EmployeePolicy(exampleEmployeeId, setOf(RoomType.Single))
        bookingPolicyRepository.add(bookingPolicy)

        bookingPolicyRepository.findPolicyFor(exampleEmployeeId) shouldBe bookingPolicy
    }

    @Test
    fun `get all booking policies for company`() {
        val bookingPolicy = CompanyPolicy(exampleCompanyId, setOf(RoomType.Single))
        bookingPolicyRepository.add(bookingPolicy)

        bookingPolicyRepository.getPoliciesFor(exampleCompanyId) shouldBe setOf(bookingPolicy)
    }
}