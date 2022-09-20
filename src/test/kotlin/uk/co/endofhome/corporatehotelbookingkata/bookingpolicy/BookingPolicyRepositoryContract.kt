package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class BookingPolicyRepositoryContract {
    private val bookingPolicyRepository = InMemoryBookingPolicyRepository()

    @Test
    fun `get all booking policies for employee`() {
        val bookingPolicy = EmployeePolicy(exampleEmployeeId, setOf(RoomType.Single))
        bookingPolicyRepository.add(bookingPolicy)

        bookingPolicyRepository.getPoliciesFor(exampleEmployeeId) shouldBe setOf(bookingPolicy)
    }
}