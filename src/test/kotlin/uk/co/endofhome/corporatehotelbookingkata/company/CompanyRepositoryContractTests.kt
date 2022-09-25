package uk.co.endofhome.corporatehotelbookingkata.company

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId
import uk.co.endofhome.corporatehotelbookingkata.exampleEmployeeId

class CompanyRepositoryContractTests {
    private val companyRepository: CompanyRepository = InMemoryCompanyRepository()
    private val employeeId = exampleEmployeeId
    private val companyId = exampleCompanyId

    @Test
    fun `can add employee to company`() {
        companyRepository.add(employeeId, companyId)

        assertSoftly {
            companyRepository.findEmployee(employeeId) shouldBe Employee(employeeId, companyId)
            companyRepository.findCompany(companyId) shouldBe Company(companyId, listOf(employeeId))
        }
    }

    @Test
    fun `does not add same employee twice`() {
        companyRepository.add(employeeId, companyId)
        companyRepository.add(employeeId, companyId)

        companyRepository.findCompany(companyId) shouldBe Company(companyId, listOf(employeeId))
    }

    @Test
    fun `delete employee from one company`() {
        companyRepository.add(employeeId, companyId)
        companyRepository.delete(employeeId)

        companyRepository.findCompany(companyId) shouldBe null
    }

    @Test
    fun `delete employee from many companies`() {
        companyRepository.add(employeeId, companyId)
        companyRepository.add(employeeId, CompanyId("another-company-id"))
        companyRepository.delete(employeeId)

        companyRepository.findCompany(companyId) shouldBe null
    }
}