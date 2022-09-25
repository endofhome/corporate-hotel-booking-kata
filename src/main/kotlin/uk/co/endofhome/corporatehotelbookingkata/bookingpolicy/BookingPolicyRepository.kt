package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

interface BookingPolicyRepository {
    fun add(bookingPolicy: BookingPolicy)
    fun findPolicyFor(employeeId: EmployeeId): EmployeePolicy?
    fun findPolicyFor(companyId: CompanyId): CompanyPolicy?
    fun deletePoliciesFor(employeeId: EmployeeId)
}

class InMemoryBookingPolicyRepository : BookingPolicyRepository {
    private var bookingPolicies: List<BookingPolicy> = listOf()

    fun allBookingPolicies() = bookingPolicies

    override fun add(bookingPolicy: BookingPolicy) {
        bookingPolicies = bookingPolicies + bookingPolicy
    }

    override fun findPolicyFor(employeeId: EmployeeId): EmployeePolicy? =
        bookingPolicies.filterIsInstance<EmployeePolicy>().find { it.employeeId == employeeId }

    override fun findPolicyFor(companyId: CompanyId): CompanyPolicy? =
        bookingPolicies.filterIsInstance<CompanyPolicy>().find { it.companyId == companyId }

    override fun deletePoliciesFor(employeeId: EmployeeId) {
        bookingPolicies = bookingPolicies.filterNot { it is EmployeePolicy && it.employeeId == employeeId}
    }
}