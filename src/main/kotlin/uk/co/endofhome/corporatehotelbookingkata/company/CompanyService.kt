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
        companyRepository.add(employeeId, companyId)
    }

    fun deleteEmployee(employeeId: EmployeeId) {
        companyRepository.delete(employeeId)
        bookingRepository.deleteBookingsFor(employeeId)
        bookingPolicyRepository.deletePoliciesFor(employeeId)
    }
}