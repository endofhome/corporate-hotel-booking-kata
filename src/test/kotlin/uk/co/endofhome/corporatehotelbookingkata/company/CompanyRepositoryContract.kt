package uk.co.endofhome.corporatehotelbookingkata.company

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class CompanyRepositoryContract {
    private val companyRepository = InMemoryCompanyRepository()

    @Test
    fun `no companies`() {
        companyRepository.allCompanies() shouldBe emptyMap()
    }

    @Test
    fun `can add employee to company`() {
        companyRepository.add(exampleCompanyId, exampleEmployeeId)
        companyRepository.allCompanies() shouldBe mapOf(exampleCompanyId to listOf(exampleEmployeeId))
    }

    @Test
    fun `does not add same employee twice`() {
        companyRepository.add(exampleCompanyId, exampleEmployeeId)
        companyRepository.add(exampleCompanyId, exampleEmployeeId)

        companyRepository.allCompanies() shouldBe mapOf(exampleCompanyId to listOf(exampleEmployeeId))
    }
}