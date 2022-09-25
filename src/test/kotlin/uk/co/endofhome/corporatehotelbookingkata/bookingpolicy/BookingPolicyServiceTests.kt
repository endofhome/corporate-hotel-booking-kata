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
        val employeeId = exampleEmployeeId
        val companyId = exampleCompanyId
        companyRepository.add(employeeId, companyId)

        bookingPolicyService.setEmployeePolicy(employeeId,setOf(RoomType.Single))
        bookingPolicyService.setCompanyPolicy(companyId,setOf(RoomType.Double))

        assertSoftly {
            bookingPolicyService.isBookingAllowed(employeeId, RoomType.Single) shouldBe true
            bookingPolicyService.isBookingAllowed(employeeId, RoomType.Double) shouldBe false
        }
    }
}