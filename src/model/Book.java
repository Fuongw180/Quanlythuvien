package model;

import java.io.Serializable;

public class Book implements Serializable {
    
    private static int currentId = 9999;
    private int id;
    private String bookName;
    private String bookAuthor;
    private String bookMajor;
    private String bookYear;
    private int bookAmount;

    public Book() {
    }

    public Book(String bookName, String bookAuthor, String bookMajor, String bookYear, int bookAmount) {
        this.id = ++currentId;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookMajor = bookMajor;
        this.bookYear = bookYear;
        this.bookAmount = bookAmount;
    }

    public static int getCurrentId() {
        return currentId;
    }

    public static void setCurrentId(int currentId) {
        Book.currentId = currentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public Object[] toObject() {
        return new Object[]{id, bookName, bookAuthor, bookMajor, bookYear, bookAmount};
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Book) {
            Book b = (Book) obj;
            if (this.bookName.equals(b.bookName)
                    && this.bookAuthor.equals(b.bookAuthor)) {
                return true;
            }
        }
        return false;
    }
}   
