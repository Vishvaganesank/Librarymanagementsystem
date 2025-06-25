import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
    
    // Getters and setters...
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    
    public void setAvailable(boolean available) { isAvailable = available; }
    
    @Override
    public String toString() {
        return String.format("ISBN: %-15s Title: %-25s Author: %-20s Available: %s",
                isbn, title, author, isAvailable ? "Yes" : "No");
    }
}

class Member {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    
    public Member(String memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters...
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    
    @Override
    public String toString() {
        return String.format("ID: %-8s Name: %-20s Email: %-25s Phone: %s",
                memberId, name, email, phone);
    }
}

class Loan {
    private Book book;
    private Member member;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    
    public Loan(Book book, Member member, LocalDate checkoutDate, LocalDate dueDate) {
        this.book = book;
        this.member = member;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
    }
    
    // Getters and setters...
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    
    public void setReturnDate(LocalDate returnDate) { 
        this.returnDate = returnDate; 
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }
    
    public double calculateFine() {
        if (!isOverdue() || returnDate != null) return 0.0;
        long daysOverdue = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
        return daysOverdue * 0.50; // $0.50 per day fine
    }
    
    @Override
    public String toString() {
        return String.format("Book: %-25s Member: %-20s Checkout: %s Due: %s Returned: %s Fine: $%.2f",
                book.getTitle(), member.getName(), checkoutDate, dueDate,
                returnDate != null ? returnDate : "Not returned", calculateFine());
    }
}

class Library {
    private List<Book> books;
    private List<Member> members;
    private List<Loan> loans;
    
    public Library() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        loans = new ArrayList<>();
    }
    
    // Book methods
    public void addBook(Book book) {
        books.add(book);
    }
    
    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title.trim())) {
                return book;
            }
        }
        return null;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase()) ||
                book.getIsbn().contains(searchTerm)) {
                results.add(book);
            }
        }
        return results;
    }
    
    // Member methods
    public void addMember(Member member) {
        members.add(member);
    }
    
    public Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId.trim())) {
                return member;
            }
        }
        return null;
    }
    
    // Loan methods
    public boolean checkoutBook(Book book, Member member) {
        if (book == null || member == null) return false;
        if (!book.isAvailable()) return false;
        
        book.setAvailable(false);
        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusWeeks(2);
        loans.add(new Loan(book, member, checkoutDate, dueDate));
        return true;
    }
    
    public boolean returnBook(Book book) {
        if (book == null) return false;
        
        for (Loan loan : loans) {
            if (loan.getBook().equals(book) && loan.getReturnDate() == null) {
                loan.setReturnDate(LocalDate.now());
                book.setAvailable(true);
                return true;
            }
        }
        return false;
    }
    
    public List<Loan> getCurrentLoans() {
        List<Loan> current = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getReturnDate() == null) {
                current.add(loan);
            }
        }
        return current;
    }
    
    public List<Loan> getOverdueLoans() {
        List<Loan> overdue = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue()) {
                overdue.add(loan);
            }
        }
        return overdue;
    }
    
    // Get all methods
    public List<Book> getAllBooks() { return new ArrayList<>(books); }
    public List<Member> getAllMembers() { return new ArrayList<>(members); }
    public List<Loan> getAllLoans() { return new ArrayList<>(loans); }
}

public class LibrarySystem {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        initializeSampleData();
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice (0-10): ", 0, 10);
            
            switch (choice) {
                case 1: addBook(); break;
                case 2: addMember(); break;
                case 3: checkoutBook(); break;
                case 4: returnBook(); break;
                case 5: searchBooks(); break;
                case 6: searchMembers(); break;
                case 7: viewAllBooks(); break;
                case 8: viewAllMembers(); break;
                case 9: viewCurrentLoans(); break;
                case 10: viewOverdueLoans(); break;
                case 0: running = false; break;
            }
            
            if (running && choice != 0) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        System.out.println("\nThank you for using the Library Management System!");
        scanner.close();
    }
    
    private static void displayMainMenu() {
        System.out.println("\n=== LIBRARY MANAGEMENT SYSTEM ===");
        System.out.println("1. Add New Book");
        System.out.println("2. Add New Member");
        System.out.println("3. Checkout Book");
        System.out.println("4. Return Book");
        System.out.println("5. Search Books");
        System.out.println("6. Search Members");
        System.out.println("7. View All Books");
        System.out.println("8. View All Members");
        System.out.println("9. View Current Loans");
        System.out.println("10. View Overdue Loans");
        System.out.println("0. Exit");
    }
    
    // Input validation methods
    private static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private static String getNonEmptyInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }
    
    private static String getEmailInput() {
        while (true) {
            String email = getNonEmptyInput("Email: ");
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("Please enter a valid email address (must contain @ and .)");
        }
    }
    
    private static String getPhoneInput() {
        while (true) {
            String phone = getNonEmptyInput("Phone: ");
            if (phone.matches("^[0-9\\-\\+\\(\\)\\s]+$")) {
                return phone;
            }
            System.out.println("Please enter a valid phone number (digits, +, -, () allowed)");
        }
    }
    
    // Core functionality methods
    private static void addBook() {
        System.out.println("\n=== ADD NEW BOOK ===");
        String isbn = getNonEmptyInput("ISBN: ");
        String title = getNonEmptyInput("Title: ");
        String author = getNonEmptyInput("Author: ");
        
        Book book = new Book(isbn, title, author);
        library.addBook(book);
        System.out.println("\nBook added successfully!");
        System.out.println(book);
    }
    
    private static void addMember() {
        System.out.println("\n=== ADD NEW MEMBER ===");
        String memberId = getNonEmptyInput("Member ID: ");
        String name = getNonEmptyInput("Name: ");
        String email = getEmailInput();
        String phone = getPhoneInput();
        
        Member member = new Member(memberId, name, email, phone);
        library.addMember(member);
        System.out.println("\nMember added successfully!");
        System.out.println(member);
    }
    
    private static void checkoutBook() {
        System.out.println("\n=== CHECKOUT BOOK ===");
        String memberId = getNonEmptyInput("Enter Member ID: ");
        Member member = library.findMemberById(memberId);
        
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        
        String title = getNonEmptyInput("Enter Book Title: ");
        Book book = library.findBookByTitle(title);
        
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        if (library.checkoutBook(book, member)) {
            System.out.println("\nBook checked out successfully!");
            System.out.println("Due Date: " + LocalDate.now().plusWeeks(2));
        } else {
            System.out.println("\nBook is not available for checkout!");
        }
    }
    
    private static void returnBook() {
        System.out.println("\n=== RETURN BOOK ===");
        String title = getNonEmptyInput("Enter Book Title: ");
        Book book = library.findBookByTitle(title);
        
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        if (library.returnBook(book)) {
            System.out.println("\nBook returned successfully!");
        } else {
            System.out.println("\nThis book wasn't checked out or already returned!");
        }
    }
    
    private static void searchBooks() {
        System.out.println("\n=== SEARCH BOOKS ===");
        String searchTerm = getNonEmptyInput("Enter search term (title, author, or ISBN): ");
        List<Book> results = library.searchBooks(searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("\nSearch Results:");
            System.out.println("----------------------------------------------------------------");
            System.out.printf("%-15s %-25s %-20s %s\n", "ISBN", "Title", "Author", "Available");
            System.out.println("----------------------------------------------------------------");
            for (Book book : results) {
                System.out.printf("%-15s %-25s %-20s %s\n", 
                        book.getIsbn(), book.getTitle(), book.getAuthor(), 
                        book.isAvailable() ? "Yes" : "No");
            }
        }
    }
    
    private static void searchMembers() {
        System.out.println("\n=== SEARCH MEMBERS ===");
        String searchTerm = getNonEmptyInput("Enter Member ID or Name: ");
        Member member = library.findMemberById(searchTerm);
        
        if (member == null) {
            // Search by name if not found by ID
            for (Member m : library.getAllMembers()) {
                if (m.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    member = m;
                    break;
                }
            }
        }
        
        if (member == null) {
            System.out.println("No members found matching your search.");
        } else {
            System.out.println("\nMember found:");
            System.out.println(member);
        }
    }
    
    private static void viewAllBooks() {
        List<Book> books = library.getAllBooks();
        System.out.println("\n=== ALL BOOKS ===");
        
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            System.out.println("----------------------------------------------------------------");
            System.out.printf("%-15s %-25s %-20s %s\n", "ISBN", "Title", "Author", "Available");
            System.out.println("----------------------------------------------------------------");
            for (Book book : books) {
                System.out.printf("%-15s %-25s %-20s %s\n", 
                        book.getIsbn(), book.getTitle(), book.getAuthor(), 
                        book.isAvailable() ? "Yes" : "No");
            }
        }
    }
    
    private static void viewAllMembers() {
        List<Member> members = library.getAllMembers();
        System.out.println("\n=== ALL MEMBERS ===");
        
        if (members.isEmpty()) {
            System.out.println("No members registered.");
        } else {
            System.out.println("----------------------------------------------------------------");
            System.out.printf("%-8s %-20s %-25s %s\n", "ID", "Name", "Email", "Phone");
            System.out.println("----------------------------------------------------------------");
            for (Member member : members) {
                System.out.printf("%-8s %-20s %-25s %s\n", 
                        member.getMemberId(), member.getName(), 
                        member.getEmail(), member.getPhone());
            }
        }
    }
    
    private static void viewCurrentLoans() {
        List<Loan> currentLoans = library.getCurrentLoans();
        System.out.println("\n=== CURRENT LOANS ===");
        
        if (currentLoans.isEmpty()) {
            System.out.println("No books currently checked out.");
        } else {
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.printf("%-25s %-20s %-12s %-12s %s\n", 
                    "Book", "Member", "Checkout", "Due Date", "Status");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Loan loan : currentLoans) {
                System.out.printf("%-25s %-20s %-12s %-12s %s\n", 
                        loan.getBook().getTitle(), 
                        loan.getMember().getName(),
                        loan.getCheckoutDate(),
                        loan.getDueDate(),
                        loan.isOverdue() ? "OVERDUE" : "On time");
            }
        }
    }
    
    private static void viewOverdueLoans() {
        List<Loan> overdueLoans = library.getOverdueLoans();
        System.out.println("\n=== OVERDUE LOANS ===");
        
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.printf("%-25s %-20s %-12s %-12s %s\n", 
                    "Book", "Member", "Due Date", "Days Late", "Fine");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Loan loan : overdueLoans) {
                long daysLate = LocalDate.now().toEpochDay() - loan.getDueDate().toEpochDay();
                System.out.printf("%-25s %-20s %-12s %-12d $%.2f\n", 
                        loan.getBook().getTitle(), 
                        loan.getMember().getName(),
                        loan.getDueDate(),
                        daysLate,
                        loan.calculateFine());
            }
        }
    }
    
    private static void initializeSampleData() {
        // Add sample books
        library.addBook(new Book("978-0061120084", "To Kill a Mockingbird", "Harper Lee"));
        library.addBook(new Book("978-0451524935", "1984", "George Orwell"));
        library.addBook(new Book("978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald"));
        library.addBook(new Book("978-0544003415", "The Hobbit", "J.R.R. Tolkien"));
        library.addBook(new Book("978-0743477109", "Macbeth", "William Shakespeare"));
        
        // Add sample members
        library.addMember(new Member("M001", "John Doe", "john@example.com", "555-0101"));
        library.addMember(new Member("M002", "Jane Smith", "jane@example.com", "555-0102"));
        library.addMember(new Member("M003", "Robert Johnson", "robert@example.com", "555-0103"));
        
        // Create some sample loans
        Book book1 = library.findBookByTitle("1984");
        Book book2 = library.findBookByTitle("The Great Gatsby");
        Member member1 = library.findMemberById("M001");
        Member member2 = library.findMemberById("M002");
        
        if (book1 != null && member1 != null) {
            library.checkoutBook(book1, member1);
        }
        if (book2 != null && member2 != null) {
            library.checkoutBook(book2, member2);
        }
    }
}