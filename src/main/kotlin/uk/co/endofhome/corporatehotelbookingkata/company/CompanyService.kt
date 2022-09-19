package uk.co.endofhome.corporatehotelbookingkata.company

import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

class CompanyService(private val companyRepository: CompanyRepository) {
    fun addEmployee(companyId: CompanyId, employeeId: EmployeeId) {
        companyRepository.add(companyId, employeeId)
    }
}

interface CompanyRepository {
    fun add(companyId: CompanyId, employeeId: EmployeeId)
}

class InMemoryCompanyRepository : CompanyRepository {
    private var companies: Map<CompanyId, List<EmployeeId>> = emptyMap()

    override fun add(companyId: CompanyId, employeeId: EmployeeId) {
        val employees = companies[companyId] ?: emptyList()
        if (!employees.contains(employeeId)) {
            companies = companies + (companyId to (employees + employeeId))
        }
    }

    fun allCompanies() = companies
}
