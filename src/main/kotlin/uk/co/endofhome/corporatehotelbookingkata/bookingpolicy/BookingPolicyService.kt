package uk.co.endofhome.corporatehotelbookingkata.bookingpolicy

import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.CompanyPolicy
import uk.co.endofhome.corporatehotelbookingkata.bookingpolicy.BookingPolicy.EmployeePolicy
import uk.co.endofhome.corporatehotelbookingkata.company.CompanyRepository
import uk.co.endofhome.corporatehotelbookingkata.domain.CompanyId
import uk.co.endofhome.corporatehotelbookingkata.domain.EmployeeId
import uk.co.endofhome.corporatehotelbookingkata.domain.RoomType

interface IBookingPolicyService {
    fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>)
    fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>)
    fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean
}

class BookingPolicyService(private val bookingPolicyRepository: BookingPolicyRepository, private val companyRepository: CompanyRepository) : IBookingPolicyService {
    override fun setCompanyPolicy(companyId: CompanyId, roomTypes: Set<RoomType>) {
        bookingPolicyRepository.add(CompanyPolicy(companyId, roomTypes))
    }

    override fun setEmployeePolicy(employeeId: EmployeeId, roomTypes: Set<RoomType>) {
        bookingPolicyRepository.add(EmployeePolicy(employeeId, roomTypes))
    }

    override fun isBookingAllowed(employeeId: EmployeeId, roomType: RoomType): Boolean {
        val employee = companyRepository.find(employeeId)
        val employeePolicy = bookingPolicyRepository.findPolicyFor(employeeId)
        val companyPolicy = employee?.let { bookingPolicyRepository.findPolicyFor(employee.companyId) }
        val policyWithPrecedence = employeePolicy ?: companyPolicy

        return policyWithPrecedence?.roomTypesAllowed?.contains(roomType) ?: true
    }
}

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

sealed interface BookingPolicy {
    val roomTypesAllowed: Set<RoomType>

    data class CompanyPolicy(val companyId: CompanyId, override val roomTypesAllowed: Set<RoomType>) : BookingPolicy
    data class EmployeePolicy(val employeeId: EmployeeId, override val roomTypesAllowed: Set<RoomType>) : BookingPolicy
}
