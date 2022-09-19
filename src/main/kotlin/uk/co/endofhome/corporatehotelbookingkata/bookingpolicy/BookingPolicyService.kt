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

sealed class BookingPolicyType {
    data class CompanyPolicy(val bookingPolicy: BookingPolicy) : BookingPolicyType()
}

sealed class BookingPolicy {
    data class RoomTypeNotAllowed(val roomTypeAttempted: RoomType, val roomTypesAllowed: Set<RoomType>) : BookingPolicy()
}