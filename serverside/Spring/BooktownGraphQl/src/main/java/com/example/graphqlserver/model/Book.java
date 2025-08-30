package com.example.graphqlserver.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    private String isbn;
    private String title;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Author author;




    public Book(String isbn, String title, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public Book() {

    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(Author author) {this.author = author;}
    }
