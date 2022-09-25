package uk.co.endofhome.corporatehotelbookingkata.company

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class CompanyRepositoryContractTests {
    private val companyRepository: CompanyRepository = InMemoryCompanyRepository()

    @Test
    fun `can add employee to company`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.findCompany(exampleCompanyId) shouldBe Company(exampleCompanyId, listOf(exampleEmployeeId))
    }

    @Test
    fun `does not add same employee twice`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.add(exampleEmployeeId, exampleCompanyId)

        companyRepository.findCompany(exampleCompanyId) shouldBe Company(exampleCompanyId, listOf(exampleEmployeeId))
    }

    @Test
    fun `delete employee from one company`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.delete(exampleEmployeeId)

        companyRepository.findCompany(exampleCompanyId) shouldBe null
    }

    @Test
    fun `delete employee from many companies`() {
        companyRepository.add(exampleEmployeeId, exampleCompanyId)
        companyRepository.add(exampleEmployeeId, CompanyId("another-company-id"))
        companyRepository.delete(exampleEmployeeId)

        companyRepository.findCompany(exampleCompanyId) shouldBe null
    }
}