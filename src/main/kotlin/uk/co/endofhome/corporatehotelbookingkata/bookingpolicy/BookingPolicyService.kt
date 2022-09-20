package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

interface IBookingPolicyService {
    fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>)
    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>)
    fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean
}

class BookingPolicyService(private val bookingPolicyRepository: BookingPolicyRepository) : IBookingPolicyService {
    override fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>) {
        TODO("Not yet implemented")
    }

    override fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        bookingPolicyRepository.add(EmployeePolicy(employeeId, roomTypes))
    }

    override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean {
        val employeePolicies = bookingPolicyRepository.getPoliciesFor(employeeId).filterIsInstance<EmployeePolicy>()

        return employeePolicies.find { roomType in it.roomTypesAllowed } != null || employeePolicies.isEmpty()
    }
}

interface BookingPolicyRepository {
    fun add(bookingPolicy: BookingPolicy)
    fun getPoliciesFor(employeeId: EmployeeId): List<BookingPolicy>
    fun deletePoliciesFor(employeeId: EmployeeId)
}

class InMemoryBookingPolicyRepository : BookingPolicyRepository {
    private var bookingPolicies: List<BookingPolicy> = listOf()

    fun allBookingPolicies() = bookingPolicies

    override fun add(bookingPolicy: BookingPolicy) {
        bookingPolicies = bookingPolicies + bookingPolicy
    }

    override fun getPoliciesFor(employeeId: EmployeeId): List<BookingPolicy> =
        bookingPolicies.filterIsInstance<EmployeePolicy>().filter { it.employeeId == employeeId }

    override fun deletePoliciesFor(employeeId: EmployeeId) {
        bookingPolicies = bookingPolicies.filterNot { it is EmployeePolicy && it.employeeId == employeeId}
    }
}

sealed class BookingPolicy {
    data class CompanyPolicy(val companyId: CompanyId, val roomTypesAllowed: Set<RoomType>) : BookingPolicy()
    data class EmployeePolicy(val employeeId: EmployeeId, val roomTypesAllowed: Set<RoomType>) : BookingPolicy()
}
