package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.BadRequestException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ShoppingCartNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtAuthFilter
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtService
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.WebSecurity
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ShoppingCartService
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(ShoppingCartController::class)
@Import(WebSecurity::class, JwtService::class, JwtAuthFilter::class)
class ShoppingCartControllerUnitTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockkBean
    private lateinit var shoppingCartService: ShoppingCartService

    private lateinit var customer: User
    private lateinit var dto: ShoppingCartDto
    private lateinit var itemDto: ItemRequestDto
    private lateinit var shoppingCart: ShoppingCart
    private lateinit var dtoAsString: String
    private lateinit var itemDtoAsString: String

    @BeforeEach
    fun setup() {
        customer = buildUser(roleId = Role.CUSTOMER.value)
        shoppingCart = buildShoppingCart(customer)
        dto = ShoppingCartDto(customer.id!!.toString())
        dtoAsString = mapper.writeValueAsString(dto)
        itemDto = ItemRequestDto(buildProduct().id!!.toString(), 1)
        itemDtoAsString = mapper.writeValueAsString(itemDto)
    }

    // create
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to create a shopping cart when user is customer and return status code 201`() {
        val shoppingCartResponseDto = ShoppingCartResponseDto(shoppingCart.id!!, customer.id!!)
        every { shoppingCartService.create(any()) } returns shoppingCartResponseDto
        mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to create a shopping cart when user is admin and return status code 403`() {
        mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON).content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to create a shopping cart when user is employee and return status code 403`() {
        mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON).content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    // findById
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to access shopping cart info when the customer is the owner and return status code 200`() {
        every { shoppingCartService.findById(any()) } returns buildShoppingCartResponseDto(shoppingCart)
        mockMvc.perform(get("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to access shopping cart info when the customer is not the owner and return status code 200`() {
        val message = "you cannot access this data"
        every { shoppingCartService.findById(any()) } throws ForbiddenException(message)
        val result = mockMvc.perform(get("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to access shopping cart info when user is admin and return status code 403`() {
        mockMvc.perform(get("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to access shopping cart info when user is employee and return status code 403`() {
        mockMvc.perform(get("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    // checkout
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to checkout a shopping cart when the customer is the owner and return status code 200`() {
        every { shoppingCartService.checkout(any(), any()) } returns buildCheckoutResponseDto(PaymentMethod.DEBIT_CARD)
        val checkoutRequestDto = CheckoutRequestDto(paymentMethod = PaymentMethod.DEBIT_CARD)
        dtoAsString = mapper.writeValueAsString(checkoutRequestDto)
        mockMvc.perform(put("$BASE_URL/${UUID.randomUUID()}/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to checkout a shopping cart when the customer is not the owner and return status code 403`() {
        val message = "you cannot access this data"
        every { shoppingCartService.checkout(any(), any()) } throws ForbiddenException(message)
        val checkoutRequestDto = CheckoutRequestDto(paymentMethod = PaymentMethod.DEBIT_CARD)
        dtoAsString = mapper.writeValueAsString(checkoutRequestDto)
        val result = mockMvc.perform(put("$BASE_URL/${UUID.randomUUID()}/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to checkout a non-existing shopping cart and return status code 404`() {
        val message = "shopping cart not found"
        every { shoppingCartService.checkout(any(), any()) } throws ShoppingCartNotFoundException(message)
        val checkoutRequestDto = CheckoutRequestDto(paymentMethod = PaymentMethod.DEBIT_CARD)
        dtoAsString = mapper.writeValueAsString(checkoutRequestDto)
        mockMvc.perform(put("$BASE_URL/${UUID.randomUUID()}/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to checkout a shopping cart without items and return status code 400`() {
        val message = "shopping cart has no items"
        every { shoppingCartService.checkout(any(), any()) } throws BadRequestException(message)
        val checkoutRequestDto = CheckoutRequestDto(paymentMethod = PaymentMethod.DEBIT_CARD)
        dtoAsString = mapper.writeValueAsString(checkoutRequestDto)
        mockMvc.perform(put("$BASE_URL/${UUID.randomUUID()}/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isBadRequest)
    }

    // delete
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to delete a shopping cart when customer is the owner and return status code 204`() {
        every { shoppingCartService.delete(any()) } returns Unit
        mockMvc.perform(delete("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a non-existing shopping cart and return status code 404`() {
        val message = "shopping cart not found"
        every { shoppingCartService.delete(any()) } throws ShoppingCartNotFoundException(message)
        mockMvc.perform(delete("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a shopping cart when the customer is not the owner and return status code 403`() {
        val message = "you cannot access this data"
        every { shoppingCartService.delete(any()) } throws ForbiddenException(message)
        mockMvc.perform(delete("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }


    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a shopping cart when it has items and return status code 404`() {
        val message = "shopping cart contains items and cannot be deleted"
        every { shoppingCartService.delete(any()) } throws BadRequestException(message)
        mockMvc.perform(delete("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a paid shopping cart and return status code 404`() {
        val message = "you cannot delete a paid cart"
        every { shoppingCartService.delete(any()) } throws BadRequestException(message)
        mockMvc.perform(delete("$BASE_URL/${UUID.randomUUID()}"))
            .andExpect(status().isBadRequest)
    }

    // getAllItems
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to get all items of a shopping cart and return status code 200`() {
        every { shoppingCartService.getAllItems(any()) } returns emptyList()
        mockMvc.perform(get("$BASE_URL/${UUID.randomUUID()}/items"))
            .andExpect(status().isOk)
    }

    // addItem
    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to add an item and return status code 200`() {
        val item = buildItem(shoppingCart)
        val itemResponseDto = ItemResponseDto(item.id!!, item.shoppingCart.id!!, item.product.name, item.quantity)
        every { shoppingCartService.addItem(shoppingCart.id!!, itemDto) } returns itemResponseDto
        mockMvc.perform(post("$BASE_URL/${shoppingCart.id}/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(itemDtoAsString))
            .andExpect(status().isCreated)
    }

    companion object {
        private const val BASE_URL = "/shopping-carts"
    }


}