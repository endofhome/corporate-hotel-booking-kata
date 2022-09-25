package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.company.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

internal class BookingPolicyServiceTests{
    @Test
    fun `Employee booking policy takes precedence over company booking policy`() {
        val companyRepository = InMemoryCompanyRepository()
        val bookingPolicyService = DefaultBookingPolicyService(InMemoryBookingPolicyRepository(), companyRepository)
        companyRepository.add(exampleEmployeeId, exampleCompanyId)

        bookingPolicyService.setEmployeePolicy(exampleEmployeeId,setOf(RoomType.Single))
        bookingPolicyService.setCompanyPolicy(exampleCompanyId,setOf(RoomType.Double))

        assertSoftly {
            bookingPolicyService.isBookingAllowed(exampleEmployeeId, RoomType.Single) shouldBe true
            bookingPolicyService.isBookingAllowed(exampleEmployeeId, RoomType.Double) shouldBe false
        }
    }
}