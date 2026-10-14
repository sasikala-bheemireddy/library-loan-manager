package com.example.library;

import java.time.LocalDate;

/** Small demo that walks through borrowing, returning late and overdue reminders. */
public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook(new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin"));
        library.addMember(new Member("M001", "Alex Taylor"));
        library.addMember(new Member("M002", "Jo Patel"));

        LocalDate start = LocalDate.of(2026, 1, 5);

        Loan first = library.borrowBook("978-0-13-468599-1", "M001", start);
        Loan second = library.borrowBook("978-0-13-235088-4", "M002", start.plusDays(3));
        System.out.println("Borrowed: " + first);
        System.out.println("Borrowed: " + second);

        LocalDate checkDate = start.plusDays(20);
        System.out.println("Overdue on " + checkDate + ":");
        for (Loan loan : library.overdueReminders(checkDate)) {
            System.out.println("  " + loan);
        }

        int finePence = library.returnBook("978-0-13-468599-1", checkDate);
        System.out.printf("Returned late, fine: %d.%02d GBP%n", finePence / 100, finePence % 100);
    }
}
