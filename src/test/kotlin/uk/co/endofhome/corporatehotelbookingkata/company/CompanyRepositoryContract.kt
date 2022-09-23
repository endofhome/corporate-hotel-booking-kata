package uk.co.endofhome.corporatehotelbookingkata.company

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
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
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.allCompanies() shouldBe mapOf(exampleCompanyId to listOf(exampleEmployeeId))
    }

    @Test
    fun `does not add same employee twice`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.add(exampleEmployeeId, exampleCompanyId)

        companyRepository.allCompanies() shouldBe mapOf(exampleCompanyId to listOf(exampleEmployeeId))
    }

    @Test
    fun `delete employee from one company`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.delete(exampleEmployeeId)

        companyRepository.allCompanies() shouldBe emptyMap()
    }

    @Test
    fun `delete employee from many companies`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.add(exampleEmployeeId, CompanyId("another-company-id"))
        companyRepository.delete(exampleEmployeeId)

        companyRepository.allCompanies() shouldBe emptyMap()
    }
}