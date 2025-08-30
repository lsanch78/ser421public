package com.example.graphqlserver.controller;

import com.example.graphqlserver.dto.input.AddBookInput;
import com.example.graphqlserver.dto.output.AddBookPayload;
import com.example.graphqlserver.model.Author;
import com.example.graphqlserver.model.Book;
import com.example.graphqlserver.repository.AuthorRepository;
import com.example.graphqlserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // Get all books
    @QueryMapping
    public List<Book> books() {
        return bookRepository.findAll();
    }

    // Get book by ISBN
    @QueryMapping
    public Book bookByISBN(@Argument("isbn") String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    // Get all books by authorId
    @QueryMapping
    public List<Book> booksByAuthorId(@Argument("authorId") int authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    // Get only titles by author's first name
    @QueryMapping
    public List<String> bookTitlesByAuthorFirstname(@Argument("firstName") String firstName) {
        List<Author> authors = authorRepository.findByFirstName(firstName);
        List<String> bookTitles = new ArrayList<>();
        for (Author author : authors) {
            List<Book> books = bookRepository.findByAuthorId(author.getId());
            for (Book book : books) {
                bookTitles.add(book.getTitle());
            }
        }
        return bookTitles;
    }

    // Add a new book
    @MutationMapping
    public AddBookPayload addBook(@Argument AddBookInput input) {
        Author author = authorRepository.findById(input.authorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Author with ID " + input.authorId() + " does not exist"));

        Book book = new Book();
        book.setIsbn(input.isbn());
        book.setTitle(input.title());
        book.setAuthor(author);

        Book saved = bookRepository.save(book);

        // also update Author's book list (optional, if you model bidirectional relationship)
        author.getBooks().add(saved);
        authorRepository.save(author);

        return new AddBookPayload(saved);
    }

    // Remove a book by ISBN
    @MutationMapping
    public String removeBookByIsbn(@Argument String isbn) {
        Book bookToDelete = bookRepository.findByIsbn(isbn);
        if (bookToDelete != null) {
            bookRepository.deleteByIsbn(isbn);
            return isbn;
        }
        return null;
    }
}
