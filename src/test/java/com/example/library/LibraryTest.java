package com.example.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 5);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book("B1", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("B2", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("B3", "Refactoring", "Martin Fowler"));
        library.addBook(new Book("B4", "Head First Java", "Kathy Sierra"));
        library.addMember(new Member("M1", "Alex Taylor"));
        library.addMember(new Member("M2", "Jo Patel"));
    }

    @Test
    void borrowingBookMarksItOnLoanWithDueDate() {
        Loan loan = library.borrowBook("B1", "M1", START);

        assertTrue(library.isOnLoan("B1"));
        assertEquals(START.plusDays(Library.LOAN_DAYS), loan.getDueDate());
        assertEquals(1, library.activeLoanCount("M1"));
    }

    @Test
    void cannotBorrowBookThatIsAlreadyOnLoan() {
        library.borrowBook("B1", "M1", START);

        assertThrows(LibraryException.class, () -> library.borrowBook("B1", "M2", START));
    }

    @Test
    void cannotBorrowUnknownBookOrUnknownMember() {
        assertThrows(LibraryException.class, () -> library.borrowBook("NOPE", "M1", START));
        assertThrows(LibraryException.class, () -> library.borrowBook("B1", "NOPE", START));
    }

    @Test
    void memberCannotExceedLoanLimit() {
        library.borrowBook("B1", "M1", START);
        library.borrowBook("B2", "M1", START);
        library.borrowBook("B3", "M1", START);

        assertThrows(LibraryException.class, () -> library.borrowBook("B4", "M1", START));
    }

    @Test
    void returnOnTimeHasNoFine() {
        library.borrowBook("B1", "M1", START);

        int fine = library.returnBook("B1", START.plusDays(Library.LOAN_DAYS));

        assertEquals(0, fine);
        assertFalse(library.isOnLoan("B1"));
    }

    @Test
    void lateReturnChargesPerDayAndFreesTheLoanSlot() {
        library.borrowBook("B1", "M1", START);
        library.borrowBook("B2", "M1", START);

        int fine = library.returnBook("B1", START.plusDays(Library.LOAN_DAYS + 3));

        assertEquals(3 * Library.FINE_PENCE_PER_DAY, fine);
        // Regression check: a late return must still reduce the member's active loan count.
        assertEquals(1, library.activeLoanCount("M1"));
    }

    @Test
    void returningBookThatIsNotOnLoanFails() {
        assertThrows(LibraryException.class, () -> library.returnBook("B1", START));
    }

    @Test
    void doubleReturnFails() {
        library.borrowBook("B1", "M1", START);
        library.returnBook("B1", START.plusDays(1));

        assertThrows(LibraryException.class, () -> library.returnBook("B1", START.plusDays(2)));
    }

    @Test
    void overdueRemindersAreOrderedLongestOverdueFirst() {
        library.borrowBook("B2", "M2", START.plusDays(5));
        library.borrowBook("B1", "M1", START);
        library.borrowBook("B3", "M1", START.plusDays(10));

        List<Loan> overdue = library.overdueReminders(START.plusDays(40));

        assertEquals(3, overdue.size());
        assertEquals("B1", overdue.get(0).getBook().getIsbn());
        assertEquals("B2", overdue.get(1).getBook().getIsbn());
        assertEquals("B3", overdue.get(2).getBook().getIsbn());
    }

    @Test
    void loanNotYetDueIsNotInReminders() {
        library.borrowBook("B1", "M1", START);

        assertTrue(library.overdueReminders(START.plusDays(Library.LOAN_DAYS)).isEmpty());
    }

    @Test
    void duplicateBookOrMemberIsRejected() {
        assertThrows(LibraryException.class, () -> library.addBook(new Book("B1", "Other", "Someone")));
        assertThrows(LibraryException.class, () -> library.addMember(new Member("M1", "Someone Else")));
    }

    @Test
    void blankBookFieldsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "Title", "Author"));
        assertThrows(IllegalArgumentException.class, () -> new Book("B9", "", "Author"));
        assertThrows(IllegalArgumentException.class, () -> new Member("M9", null));
    }
}
