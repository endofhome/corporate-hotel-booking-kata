package uk.co.endofhome.corporatehotelbookingkata.company

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class CompanyRepositoryContract {
    private val companyRepository = InMemoryCompanyRepository()

    @Test
    fun `no companies`() {
        companyRepository.allCompanies() shouldBe emptyMap()
    }

    @Test
    fun `can add company`() {
        companyRepository.add(exampleCompanyId, exampleEmployeeId)
        companyRepository.allCompanies() shouldBe mapOf(exampleCompanyId to listOf(exampleEmployeeId))
    }
}