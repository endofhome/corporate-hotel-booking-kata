package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId

interface BookingPolicyRepository {
    fun add(bookingPolicy: BookingPolicy)
    fun findPolicyFor(employeeId: EmployeeId): BookingPolicy.EmployeePolicy?
    fun findPolicyFor(companyId: CompanyId): BookingPolicy.CompanyPolicy?
    fun deletePoliciesFor(employeeId: EmployeeId)
}

class InMemoryBookingPolicyRepository : BookingPolicyRepository {
    private var bookingPolicies: List<BookingPolicy> = listOf()

    fun allBookingPolicies() = bookingPolicies

    override fun add(bookingPolicy: BookingPolicy) {
        bookingPolicies = bookingPolicies + bookingPolicy
    }

    override fun findPolicyFor(employeeId: EmployeeId): BookingPolicy.EmployeePolicy? =
        bookingPolicies.filterIsInstance<BookingPolicy.EmployeePolicy>().find { it.employeeId == employeeId }

    override fun findPolicyFor(companyId: CompanyId): BookingPolicy.CompanyPolicy? =
        bookingPolicies.filterIsInstance<BookingPolicy.CompanyPolicy>().find { it.companyId == companyId }

    override fun deletePoliciesFor(employeeId: EmployeeId) {
        bookingPolicies = bookingPolicies.filterNot { it is BookingPolicy.EmployeePolicy && it.employeeId == employeeId}
    }
}