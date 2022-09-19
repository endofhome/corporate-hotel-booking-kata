package uk.co.endofhome.corporatehotelbookingkata.company

import uk.co.endofhome.corporatehotelbookingkata.booking.BookingRepository
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

class CompanyService(
    private val companyRepository: CompanyRepository,
    private val bookingRepository: BookingRepository,
    private val bookingPolicyRepository: BookingPolicyRepository
) {
    fun addEmployee(companyId: CompanyId, employeeId: EmployeeId) {
        companyRepository.add(companyId, employeeId)
    }

    fun deleteEmployee(employeeId: EmployeeId) {
        companyRepository.delete(employeeId)
        bookingRepository.deleteBookingsFor(employeeId)
        bookingPolicyRepository.deletePoliciesFor(employeeId)
    }
}

interface CompanyRepository {
    fun add(companyId: CompanyId, employeeId: EmployeeId)
    fun delete(employeeId: EmployeeId)
}

class InMemoryCompanyRepository : CompanyRepository {
    private var companies: Map<CompanyId, List<EmployeeId>> = emptyMap()

    override fun add(companyId: CompanyId, employeeId: EmployeeId) {
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

    fun allCompanies() = companies
}
