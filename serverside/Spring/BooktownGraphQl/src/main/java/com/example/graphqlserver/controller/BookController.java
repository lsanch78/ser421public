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

    @QueryMapping
    public List<Book> books() {
        return bookRepository.getBooks();
    }

    @QueryMapping
    public  Book bookByISBN(@Argument("isbn") String isbn) {
        return bookRepository.getBookByISBN(isbn);
    }

    @QueryMapping
    public List<Book> booksByAuthorId(@Argument("authorId") int authorId) { return BookRepository.getBooksByAuthorId(authorId); }

    @QueryMapping
    public List<String> bookTitlesByAuthorFirstname(@Argument("firstName") String firstName) {
        List<Author> authors = authorRepository.getAuthorByFirstname(firstName);
        // get authors by Id
        List<String> bookTitles = new ArrayList<>(List.of());
        for (Author author : authors) {
            List<Book> books = bookRepository.getBooksByAuthorId(author.getId());
            for (Book book : books) {
                bookTitles.add(book.getTitle());
            }
        }
        return bookTitles;
    }



    @MutationMapping
    public AddBookPayload addBook(@Argument AddBookInput input) {
        Author author = authorRepository.getAuthorById(input.authorId());
        if (author == null) {
            throw  new IllegalArgumentException("Author with ID " + input.authorId() + "does not exist");
        }
        var book = bookRepository.save(input.isbn(), input.title(), input.authorId());
        author.getBooks().add(book);
        var out = new AddBookPayload(book);
        return out;
    }


    @MutationMapping
    public String removeBookByIsbn(@Argument String isbn) {
        Book bookToDelete = bookRepository.getBookByISBN(isbn);
        if (bookToDelete != null) {
            bookRepository.removeByIsbn(isbn);
            return isbn;
        }
        return null;
    }
}
