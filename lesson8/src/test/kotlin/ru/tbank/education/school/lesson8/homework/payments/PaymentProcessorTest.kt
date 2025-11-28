package ru.tbank.education.school.lesson8.homework.payments

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.api.DisplayName


class PaymentProcessorTest {
    private lateinit var processor: PaymentProcessor
    private val validAmount = 1
    private val validCardNumber = "4321234567890123"
    private val validExpiryMonth = 1
    private val validExpiryYear = 2026
    private val validCurrency = "USD"
    private val validCustomerId = "43"

    @BeforeEach
    fun setUp() {
        processor = PaymentProcessor()
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for invalid amounts")
    @CsvSource("0", "-100")
    fun amountShouldBePositive(amount: Int) {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(
                amount,
                validCardNumber,
                validExpiryMonth,
                validExpiryYear,
                validCurrency,
                validCustomerId
            )
        }
    }

    @ParameterizedTest
    @DisplayName("Expiry date validation test cases")
    @CsvSource(
        "6, 2024, true",
        "10, 2025, true",
        "11, 2025, false",
        "12, 2025, false",
        "0, 2026, true",
        "13, 2026, true",
        "1, 2026, false",
        "6, 2026, false",
        "12, 2026, false",
        "-1, 2025, true",
        "0, 2025, true",
        "1, 2025, true",
        "11, 2025, false",
        "12, 2025, false",
        "13, 2025, true",
    )
    fun expiryDateValidation(month: Int, year: Int, shouldRaise: Boolean) {
        if (shouldRaise) {
            assertThrows(IllegalArgumentException::class.java) {
                processor.processPayment(validAmount, validCardNumber, month, year, validCurrency, validCustomerId)
            }
        } else {
            assertDoesNotThrow {
                processor.processPayment(validAmount, validCardNumber, month, year, validCurrency, validCustomerId)
            }
        }
    }

    @ParameterizedTest
    @DisplayName("Validate incorrect card numbers")
    @CsvSource(
        "123",
        "12345678901234567890",
        "4111-1111-1111",
        "abcd567890123456",
        "4111 1111 1111 1111",
        "4111.1111.1111.1111",
        "''",

    )
    fun invalidCardNumbers(cardNumber: String) {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(
                validAmount,
                cardNumber,
                validExpiryMonth,
                validExpiryYear,
                validCurrency,
                validCustomerId
            )
        }
    }

    @Test
    @DisplayName("Should throw exception when customer ID is empty")
    fun customerIdCannotBeEmpty() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(validAmount, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "")
        }
    }

    @Test
    @DisplayName("Should throw exception when currency is empty")
    fun currencyCannotBeEmpty() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(
                validAmount,
                validCardNumber,
                validExpiryMonth,
                validExpiryYear,
                "",
                validCustomerId
            )
        }
    }

    @ParameterizedTest
    @DisplayName("Currency normalization and conversion cases")
    @CsvSource(
        "USD, 100, 100",
        "EUR, 100, 92",
        "GBP, 100, 78",
        "JPY, 100, 15000",
        "RUB, 100, 9000",
        "eur, 100, 92",
        "gbp, 100, 78",
        "ABC, 100, 100",
        "USD, 1, 1",

        "JPY, 1, 150",
    )
    fun currencyConversionTest(inputCurrency: String, amount: Int, expected: Int) {
        val result = processor.processPayment(
            amount,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            inputCurrency,
            validCustomerId
        )
        assertEquals("SUCCESS", result.status)
    }


    @ParameterizedTest
    @DisplayName("Detect suspicious card numbers")
    @CsvSource(
        "4444123412341234",
        "5555123412341234",
        "1111123412341234",
        "9999123412341234",
        "4242424242424241",
        "5555555555554440",
        "378282246310000",
        "6011111111111110",
        "4242424242421",
        "42424242424242424",
    )
    fun suspiciousCardShouldBeRejected(cardNumber: String) {
        val result = processor.processPayment(
            validAmount,
            cardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("REJECTED", result.status)
        assertEquals("Payment blocked due to suspected fraud", result.message)
    }

    @Test
    @DisplayName("Should fail when transaction limit exceeded")
    fun transactionLimitExceeded() {
        val result = processor.processPayment(
            200_000,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("FAILED", result.status)
        assertEquals("Transaction limit exceeded", result.message)
    }

    @Test
    @DisplayName("Should fail when card is blocked by gateway")
    fun gatewayCardBlocked() {
        val result = processor.processPayment(
            100,
            "4445000000000005",
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("FAILED", result.status)
        assertEquals("Card is blocked", result.message)
    }

    @Test
    @DisplayName("Should fail when insufficient funds returned by gateway")
    fun gatewayInsufficientFunds() {
        val result = processor.processPayment(
            100,
            "5500000000000004",
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("FAILED", result.status)
        assertEquals("Insufficient funds", result.message)
    }

    @Test
    @DisplayName("Gateway timeout when amount divisible by 17")
    fun gatewayTimeout() {
        val result = processor.processPayment(
            17,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("FAILED", result.status)
        assertEquals("Gateway timeout", result.message)
    }

    @Test
    @DisplayName("Successful payment flow")
    fun successfulPayment() {
        val result = processor.processPayment(
            100,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )
        assertEquals("SUCCESS", result.status)
        assertEquals("Payment completed", result.message)
    }

    @ParameterizedTest
    @DisplayName("Loyalty discount calculation cases")
    @CsvSource(
        "0, 1000, 0",
        "300, 1000, 0",
        "500, 1000, 50",
        "2000, 1000, 100",
        "5000, 1000, 150",
        "10000, 1000, 200",
        "50000, 100000, 5000"
    )
    fun loyaltyDiscount(points: Int, base: Int, expected: Int) {
        val discount = processor.calculateLoyaltyDiscount(points, base)
        assertEquals(expected, discount)
    }


    @Test
    @DisplayName("Should throw exception for invalid base amount in loyalty calc")
    fun invalidBaseAmountForDiscount() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.calculateLoyaltyDiscount(1000, 0)
        }
    }

    @Test
    @DisplayName("Bulk process should return empty list for empty input")
    fun bulkProcessEmptyList() {
        val result = processor.bulkProcess(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("Bulk process where all payments succeed")
    fun bulkProcessAllSuccess() {
        val payments = listOf(
            PaymentData(10, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "1"),
            PaymentData(20, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "2"),
            PaymentData(30, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "3")
        )
        val results = processor.bulkProcess(payments)
        assertEquals(3, results.size)
        assertTrue(results.all { it.status == "SUCCESS" })
    }


    @Test
    @DisplayName("Bulk process should handle invalid entries gracefully")
    fun bulkProcessWithInvalidData() {
        val payments = listOf(
            PaymentData(10, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "1"),
            PaymentData(0, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "2"),
            PaymentData(30, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "3")
        )
        val results = processor.bulkProcess(payments)
        assertEquals(3, results.size)
        assertEquals("REJECTED", results[1].status)
        assertEquals("Amount must be positive", results[1].message)
    }

}



