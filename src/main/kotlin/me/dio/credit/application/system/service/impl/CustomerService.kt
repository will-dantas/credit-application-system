package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class CustomerService(
  private val customerRepository: CustomerRepository
) : ICustomerService {
  override fun save(customer: Customer): Customer {
    return this.customerRepository.save(customer);
  }

  override fun findById(id: Long): Customer {
    return customerRepository.findById(id).orElseThrow() {
      throw RuntimeException("ID $id not found.");
    }
  }

  override fun delete(id: Long) {
    val customer = this.findById(id)
    this.customerRepository.delete(customer)
  }
}