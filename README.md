# Library Loan Manager

A small Java console application that manages books, members and loans for a library.
It was written to practise core Java and object-oriented design, and is covered by JUnit tests.

## What it does

- Stores books (by ISBN) and members (by id)
- Lets a member borrow a book for 14 days, with a limit of 3 active loans per member
- Stops a book being borrowed twice, and rejects unknown books or members
- Charges a late fine of 20p per day when a book is returned after its due date
- Lists overdue loans with the longest-overdue loan first

## Java concepts used

- Classes with encapsulation, `equals` and `hashCode` (`Book`, `Member`, `Loan`)
- Collections: `HashMap` for quick lookups, `ArrayList` for history, `PriorityQueue` for overdue reminders
- A custom exception (`LibraryException`) for rule violations
- `java.time.LocalDate` for due dates and late-day calculations
- JUnit 5 unit tests, including a regression test that a late return still frees the member's loan slot

## Requirements

- Java 17 or later
- Maven 3.8 or later

## Run the tests

```bash
mvn test
```

## Run the demo

```bash
mvn compile exec:java
```

## Project structure

```
src/main/java/com/example/library/
  Book.java, Member.java, Loan.java   domain classes
  Library.java                        loan rules and collections
  LibraryException.java               custom exception
  Main.java                           console demo
src/test/java/com/example/library/
  LibraryTest.java                    JUnit 5 tests
.github/workflows/ci.yml              runs the tests on every push and pull request
```

## Possible next steps

- Read and write books and members from a file or database
- Add a command-line menu
- Add a REST API with Spring Boot
