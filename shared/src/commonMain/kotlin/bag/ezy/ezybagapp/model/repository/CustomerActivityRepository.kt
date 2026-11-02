
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Customer

data class CustomerActivityReport(
    val totalCustomers: Int,
    val totalBookings: Int,
    val activeCustomers: Int,
    val inactiveCustomers: Int,
    val repeatCustomers: Int,
    val averageBookings: Double
)

class CustomerActivityRepository {

    fun generateCustomerActivityReport(
        customers: List<Customer>,
        bookings: List<Booking>
    ): CustomerActivityReport {

        val bookingsPerCustomer = customers.map { customer ->
            bookings.count {
                it.customerId == customer.customerId
            }
        }

        val activeCustomers = bookingsPerCustomer.count {
            it >= 1
        }

        val inactiveCustomers = bookingsPerCustomer.count {
            it == 0
        }

        val repeatCustomers = bookingsPerCustomer.count {
            it >= 2
        }

        val averageBookings = if (customers.isNotEmpty()) {
            bookings.size.toDouble() / customers.size
        } else {
            0.0
        }

        return CustomerActivityReport(
            totalCustomers = customers.size,
            totalBookings = bookings.size,
            activeCustomers = activeCustomers,
            inactiveCustomers = inactiveCustomers,
            repeatCustomers = repeatCustomers,
            averageBookings = averageBookings
        )
    }
}
