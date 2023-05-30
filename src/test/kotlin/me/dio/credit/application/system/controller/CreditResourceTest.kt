package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CustomerDto
import me.dio.credit.application.system.repository.CreditRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
  @Autowired
  private lateinit var creditRepository: CreditRepository

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  companion object {
    const val URL: String = "/api/credits"
  }

  @BeforeEach
  fun setup() = creditRepository.deleteAll()

  @AfterEach
  fun tearDown() = creditRepository.deleteAll()

  @Test
  fun `should create a credit and return 201 status`() {
    val creditDto: CreditDto = builderCreditDto()
    val valueAsStringCredit: String = objectMapper.writeValueAsString(creditDto)

    val customerDto: CustomerDto = builderCustomerDto()
    val valueAsStringCustomer: String = objectMapper.writeValueAsString(customerDto)

    mockMvc.perform(
      MockMvcRequestBuilders.post(CustomerResourceTest.URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsStringCustomer)
    )

    mockMvc.perform(
      MockMvcRequestBuilders.post(CreditResourceTest.URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsStringCredit)
    )
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(1000.0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(1))
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  fun `should not save a new credit with a id does not exist from customer and return 400 status`() {
    val customerDto: CustomerDto = builderCustomerDto()
    val valueAsStringCustomer: String = objectMapper.writeValueAsString(customerDto)

    val creditDto: CreditDto = builderCreditDtoBadRequest()
    val valueAsStringCredit: String = objectMapper.writeValueAsString(creditDto)

    mockMvc.perform(
      MockMvcRequestBuilders.post(CustomerResourceTest.URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsStringCustomer)
    )

    mockMvc.perform(
      MockMvcRequestBuilders.post(CreditResourceTest.URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsStringCredit)
    )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
      .andExpect(
        MockMvcResultMatchers.jsonPath("$.exception")
          .value("class me.dio.credit.application.system.exception.BusinessException")
      )
      .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
      .andDo(MockMvcResultHandlers.print())
  }


  private fun builderCreditDto(
    creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
    dayFirstOfInstallment: LocalDate =  LocalDate.parse("2023-07-01"),
    numberOfInstallments: Int = 1,
    customerId: Long = 1L
  ) = CreditDto(
    creditValue = creditValue,
    dayFirstOfInstallment = dayFirstOfInstallment,
    numberOfInstallments = numberOfInstallments,
    customerId = customerId
  )

  private fun builderCreditDtoBadRequest(
    creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
    dayFirstOfInstallment: LocalDate =  LocalDate.parse("2023-07-01"),
    numberOfInstallments: Int = 2,
    customerId: Long = 2L
  ) = CreditDto(
    creditValue = creditValue,
    dayFirstOfInstallment = dayFirstOfInstallment,
    numberOfInstallments = numberOfInstallments,
    customerId = customerId
  )

  private fun builderCustomerDto(
    firstName: String = "William",
    lastName: String = "Dantas",
    cpf: String = "28475934625",
    email: String = "william@email.com",
    income: BigDecimal = BigDecimal.valueOf(1000.0),
    password: String = "1234",
    zipCode: String = "000000",
    street: String = "Rua da William, 123",
  ) = CustomerDto(
    firstName = firstName,
    lastName = lastName,
    cpf = cpf,
    email = email,
    income = income,
    password = password,
    zipCode = zipCode,
    street = street
  )
}