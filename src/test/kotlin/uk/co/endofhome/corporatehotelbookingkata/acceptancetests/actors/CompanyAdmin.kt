package uk.co.endofhome.corporatehotelbookingkata.acceptancetests.actors

import io.kotest.matchers.shouldBe
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.CompanyService
import uk.co.endofhome.corporatehotelbookingkata.acceptancetests.InMemoryCompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.exampleCompanyId

class CompanyAdmin {
    private val companyId = exampleCompanyId
    private val companyRepository = InMemoryCompanyRepository()
    private val companyService = CompanyService(companyRepository)

    fun addEmployee(employeeId: EmployeeId) {
        companyService.addEmployee(companyId, employeeId)

        companyRepository.allCompanies()[companyId] shouldBe listOf(employeeId)
    }
}