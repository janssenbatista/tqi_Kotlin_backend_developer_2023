package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildProduct
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class ProductServiceUnitTest {

    private val productRepository = mockk<ProductRepository>()
    private val categoryService = mockk<CategoryService>()
    private val productService = ProductService(productRepository, categoryService)
    private lateinit var dto: ProductRequestDto
    private lateinit var product: Product
    private lateinit var category: Category

    @BeforeEach
    fun setup() {
        dto = buildProductRequestDto()
        product = buildProduct()
        category = Category(id = 1, name = "Category Name")
    }

    // save
    @Test
    fun `should be able to create a product`() {
        every { productRepository.findByName(any()) } returns Optional.empty()
        every { categoryService.findById(any()) } returns category
        every { productRepository.save(any()) } returns product
        val createdProduct = productService.save(dto)
        assertThat(createdProduct.id).isEqualTo(product.id)
        assertThat(createdProduct.name).isEqualTo(product.name)
        assertThat(createdProduct.measurementUnit).isEqualTo(product.measurementUnit)
        assertThat(createdProduct.unitPrice).isEqualTo(product.unitPrice)
        assertThat(createdProduct.quantityInStock).isEqualTo(product.quantityInStock)
        assertThat(createdProduct.category).isEqualTo(product.category)
        assertThat(createdProduct.createdAt).isEqualTo(product.createdAt)
        assertThat(createdProduct.updatedAt).isEqualTo(product.updatedAt)
        verify(exactly = 1) { productRepository.findByName(any()) }
        verify(exactly = 1) { categoryService.findById(any()) }
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able to create a product when other product with the same name already exists`() {
        every { productRepository.findByName(any()) } returns Optional.of(product)
        assertThatExceptionOfType(ProductAlreadyExistsException::class.java).isThrownBy {
            productService.save(dto)
        }.withMessage("product already exists")
        verify(exactly = 1) { productRepository.findByName(any()) }
        verify(exactly = 0) { categoryService.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    // findById
    @Test
    fun `should be able to find a product by id`() {
        every { productService.findById(any()) } returns product
        val foundProduct = productService.findById(UUID.randomUUID())
        assertThat(foundProduct.id).isNotNull()
        assertThat(foundProduct.name).isNotNull()
        assertThat(foundProduct.measurementUnit).isNotNull()
        assertThat(foundProduct.unitPrice).isNotNull()
        assertThat(foundProduct.quantityInStock).isNotNull()
        assertThat(foundProduct.category).isNotNull()
        assertThat(foundProduct.createdAt).isNotNull()
        assertThat(foundProduct.updatedAt).isNotNull()
        verify(exactly = 1) { productRepository.findById(any()) }
    }

    @Test
    fun `should not be able to find a product by id`() {
        val message = "product not found"
        every { productRepository.findById(any()) } throws ProductNotFoundException(message)
        assertThatExceptionOfType(ProductNotFoundException::class.java).isThrownBy {
            productService.findById(UUID.randomUUID())
        }.withMessage(message)
    }

    // findAll
    @Test
    fun `should return all products`() {
        val products = listOf<Product>()
        val page = PageImpl(products)
        val pageable = Pageable.ofSize(1)
        every { productService.findAll(any()) } returns page
        productService.findAll(pageable)
        verify(exactly = 1) { productRepository.findAll(pageable) }
    }

    // update
    @Test
    fun `should be able to update a product`() {
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { productRepository.findByName(any()) } returns Optional.empty()
        every { categoryService.findById(any()) } returns category
        every { productRepository.save(any()) } returns product
        val updatedProduct = productService.update(productId = product.id!!, dto)
        assertThat(updatedProduct.id).isEqualTo(product.id)
        assertThat(updatedProduct.name).isEqualTo(product.name)
        assertThat(updatedProduct.measurementUnit).isEqualTo(product.measurementUnit)
        assertThat(updatedProduct.unitPrice).isEqualTo(product.unitPrice)
        assertThat(updatedProduct.quantityInStock).isEqualTo(product.quantityInStock)
        assertThat(updatedProduct.category).isEqualTo(product.category)
        assertThat(updatedProduct.createdAt).isEqualTo(product.createdAt)
        assertThat(updatedProduct.updatedAt).isEqualTo(product.updatedAt)
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findByName(any()) }
        verify(exactly = 1) { categoryService.findById(any()) }
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able to update a product when id is invalid`() {
        every { productRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(ProductNotFoundException::class.java).isThrownBy {
            productService.update(UUID.randomUUID(), dto)
        }.withMessage("product not found")
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findByName(any()) }
        verify(exactly = 0) { categoryService.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able to update a product when name already exists`() {
        every { productRepository.findById(any()) } returns Optional.of(product)
        val otherProduct = buildProduct().apply {
            id = UUID.randomUUID()
            name = "Other Product"
        }
        every { productRepository.findByName(dto.name) } returns Optional.of(otherProduct)
        assertThatExceptionOfType(ProductAlreadyExistsException::class.java).isThrownBy {
            productService.update(UUID.randomUUID(), dto)
        }.withMessage("other product with the same name already exists")
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findByName(any()) }
        verify(exactly = 0) { categoryService.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    // delete
    @Test
    fun `should not be able to delete a product when id is invalid`() {
        every { productRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(ProductNotFoundException::class.java).isThrownBy {
            productService.delete(UUID.randomUUID())
        }.withMessage("product not found")
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.delete(any()) }
    }

    @Test
    fun `should be able to delete a product by id`() {
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { productRepository.deleteById(any()) } returns Unit
        productService.delete(UUID.randomUUID())
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.deleteById(any()) }
    }


}