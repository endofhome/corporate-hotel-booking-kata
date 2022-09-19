package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

interface IBookingPolicyService {
    fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>)
    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>)
    fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean
}

class BookingPolicyService : IBookingPolicyService {
    override fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>) {
        TODO("Not yet implemented")
    }
    override fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        TODO("Not yet implemented")
    }
    override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean = true
}

interface BookingPolicyRepository {
    fun add(bookingPolicyType: BookingPolicyType)
    fun deletePoliciesFor(employeeId: EmployeeId)
}

class InMemoryBookingPolicyRepository() : BookingPolicyRepository {
    private var bookingPolicies: List<BookingPolicyType> = listOf()

    fun allBookingPolicies() = bookingPolicies

    override fun add(bookingPolicyType: BookingPolicyType) {
        bookingPolicies = bookingPolicies + bookingPolicyType
    }

    override fun deletePoliciesFor(employeeId: EmployeeId) {
        bookingPolicies = bookingPolicies.filterNot { it is BookingPolicyType.EmployeePolicy && it.employeeId == employeeId}
    }
}

sealed class BookingPolicyType {
    data class CompanyPolicy(val companyId: CompanyId, val bookingPolicy: BookingPolicy) : BookingPolicyType()
    data class EmployeePolicy(val employeeId: EmployeeId, val bookingPolicy: BookingPolicy) : BookingPolicyType()
}

sealed class BookingPolicy {
    data class RoomTypeNotAllowed(val roomTypeAttempted: RoomType, val roomTypesAllowed: Set<RoomType>) : BookingPolicy()
}