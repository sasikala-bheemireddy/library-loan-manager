package com.example.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** A single loan of one book to one member. */
public final class Loan {
    private final Book book;
    private final Member member;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(Book book, Member member, LocalDate loanDate, int loanDays) {
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(loanDays);
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    /** A loan is overdue if it is still out and the due date has passed. */
    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

    /** Whole days late when the book is returned on the given date (never negative). */
    public long daysLate(LocalDate returnedOn) {
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, returnedOn));
    }

    void markReturned(LocalDate returnedOn) {
        this.returnDate = returnedOn;
    }

    @Override
    public String toString() {
        return book.getTitle() + " -> " + member.getName() + ", due " + dueDate;
    }
}
