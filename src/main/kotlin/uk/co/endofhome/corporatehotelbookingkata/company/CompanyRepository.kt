package uk.co.endofhome.corporatehotelbookingkata.company

import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

interface CompanyRepository {
    fun add(employeeId: EmployeeId, companyId: CompanyId)
    fun delete(employeeId: EmployeeId)
    fun find(employeeId: EmployeeId): Employee?
}

class InMemoryCompanyRepository : CompanyRepository {
    private var companies: Map<CompanyId, List<EmployeeId>> = emptyMap()

    override fun add(employeeId: EmployeeId, companyId: CompanyId) {
        val employees = companies[companyId] ?: emptyList()
        if (!employees.contains(employeeId)) {
            companies = companies + (companyId to (employees + employeeId))
        }
    }

    override fun delete(employeeId: EmployeeId) {
        val companiesWithThisEmployee = companies.entries.filter { (_, employeeIds) -> employeeIds.contains(employeeId) }
        val updatedCompanies = companiesWithThisEmployee.map { (companyId, employeeIds) ->
            companyId to employeeIds.filter { it != employeeId }
        }
        companies = (companies + updatedCompanies).filter { it.value.isNotEmpty() }
    }

    override fun find(employeeId: EmployeeId): Employee? =
        companies.entries.find { it.value.contains(employeeId) }?.let { Employee(employeeId, it.key) }

    fun allCompanies() = companies
}

data class Employee(val employeeId: EmployeeId, val companyId: CompanyId)