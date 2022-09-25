package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.company.CompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

interface BookingPolicyService {
    fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>)
    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>)
    fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean
}

class DefaultBookingPolicyService(private val bookingPolicyRepository: BookingPolicyRepository, private val companyRepository: CompanyRepository) : BookingPolicyService {
    override fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>) {
        bookingPolicyRepository.add(CompanyPolicy(companyId, roomTypes))
    }

    override fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        bookingPolicyRepository.add(EmployeePolicy(employeeId, roomTypes))
    }

    override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean {
        val employee = companyRepository.findEmployee(employeeId)
        val employeePolicy = bookingPolicyRepository.findPolicyFor(employeeId)
        val companyPolicy = employee?.let { bookingPolicyRepository.findPolicyFor(employee.companyId) }
        val policyWithPrecedence = employeePolicy ?: companyPolicy

        return policyWithPrecedence?.roomTypesAllowed?.contains(roomType) ?: true
    }
}

sealed interface BookingPolicy {
    val roomTypesAllowed: Set<RoomType>

    data class CompanyPolicy(val companyId: CompanyId, override val roomTypesAllowed: Set<RoomType>) : BookingPolicy
    data class EmployeePolicy(val employeeId: EmployeeId, override val roomTypesAllowed: Set<RoomType>) : BookingPolicy
}