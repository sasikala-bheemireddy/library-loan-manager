package com.example.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Keeps track of books, members and loans.
 *
 * <p>Books and members are stored in HashMaps for fast lookup by id. Loans that are
 * currently out are stored by ISBN, so a book can only be on loan once. Overdue
 * reminders use a PriorityQueue so the longest-overdue loan comes first.
 */
public class Library {
    public static final int LOAN_DAYS = 14;
    public static final int MAX_LOANS_PER_MEMBER = 3;
    public static final int FINE_PENCE_PER_DAY = 20;

    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Loan> activeLoans = new HashMap<>();
    private final List<Loan> loanHistory = new ArrayList<>();

    public void addBook(Book book) {
        if (books.containsKey(book.getIsbn())) {
            throw new LibraryException("Book already exists: " + book.getIsbn());
        }
        books.put(book.getIsbn(), book);
    }

    public void addMember(Member member) {
        if (members.containsKey(member.getId())) {
            throw new LibraryException("Member already exists: " + member.getId());
        }
        members.put(member.getId(), member);
    }

    public Loan borrowBook(String isbn, String memberId, LocalDate today) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new LibraryException("Unknown book: " + isbn);
        }
        Member member = members.get(memberId);
        if (member == null) {
            throw new LibraryException("Unknown member: " + memberId);
        }
        if (activeLoans.containsKey(isbn)) {
            throw new LibraryException("Book is already on loan: " + isbn);
        }
        if (activeLoanCount(memberId) >= MAX_LOANS_PER_MEMBER) {
            throw new LibraryException(
                    "Member has reached the limit of " + MAX_LOANS_PER_MEMBER + " loans: " + memberId);
        }
        Loan loan = new Loan(book, member, today, LOAN_DAYS);
        activeLoans.put(isbn, loan);
        loanHistory.add(loan);
        return loan;
    }

    /**
     * Returns a book and gives back the fine in pence (0 if on time).
     * The loan is removed from the active loans whether or not it was late.
     */
    public int returnBook(String isbn, LocalDate today) {
        Loan loan = activeLoans.remove(isbn);
        if (loan == null) {
            throw new LibraryException("Book is not on loan: " + isbn);
        }
        loan.markReturned(today);
        return (int) loan.daysLate(today) * FINE_PENCE_PER_DAY;
    }

    public int activeLoanCount(String memberId) {
        int count = 0;
        for (Loan loan : activeLoans.values()) {
            if (loan.getMember().getId().equals(memberId)) {
                count++;
            }
        }
        return count;
    }

    /** Overdue loans, with the earliest due date (longest overdue) first. */
    public List<Loan> overdueReminders(LocalDate today) {
        PriorityQueue<Loan> queue = new PriorityQueue<>(Comparator.comparing(Loan::getDueDate));
        for (Loan loan : activeLoans.values()) {
            if (loan.isOverdue(today)) {
                queue.add(loan);
            }
        }
        List<Loan> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            ordered.add(queue.poll());
        }
        return ordered;
    }

    public boolean isOnLoan(String isbn) {
        return activeLoans.containsKey(isbn);
    }

    public List<Loan> getLoanHistory() {
        return Collections.unmodifiableList(loanHistory);
    }
}
