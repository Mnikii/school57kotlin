package ru.tbank.education.school.lesson8.homework.library

class LibraryService {
    private val books = mutableMapOf<String, Book>()
    private val borrowedBooks = mutableMapOf<String, String>()
    private val borrowerFines = mutableMapOf<String, Int>()

    fun addBook(book: Book) {
        books[book.isbn] = book
    }

    fun borrowBook(isbn: String, borrower: String) {
        if (!books.containsKey(isbn)) {
            throw IllegalArgumentException("Book with $isbn not found")
        }
        if (borrowedBooks.containsKey(isbn)) {
            throw IllegalArgumentException("Book with $isbn is already borrowed")
        }
        if (hasOutstandingFines(borrower)) {
            throw IllegalArgumentException("Borrower $borrower has outstanding fines")
        }

        borrowedBooks[isbn] = borrower
    }

    fun returnBook(isbn: String) {
        if (!borrowedBooks.containsKey(isbn)) {
            throw IllegalArgumentException("Book with $isbn is not borrowed")
        }
        borrowedBooks.remove(isbn)
    }

    fun isAvailable(isbn: String): Boolean {
        return !borrowedBooks.containsKey(isbn)
    }

    fun calculateOverdueFine(isbn: String, daysOverdue: Int): Int {
        if (!borrowedBooks.containsKey(isbn)) {
            return 0
        }
        if (daysOverdue <= 10) return 0

        val fine = (daysOverdue - 10) * 60
        val borrower = borrowedBooks[isbn]!!
        borrowerFines[borrower] = (borrowerFines[borrower] ?: 0) + fine

        return fine
    }

    private fun hasOutstandingFines(borrower: String): Boolean {
        val fine = borrowerFines[borrower] ?: 0
        if (fine > 0) return true
        return borrowedBooks.containsValue(borrower)

    }
}