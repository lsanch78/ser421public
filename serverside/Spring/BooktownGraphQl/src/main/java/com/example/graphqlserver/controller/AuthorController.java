package com.example.graphqlserver.controller;

import com.example.graphqlserver.dto.input.AddAuthorInput;
import com.example.graphqlserver.dto.output.AddAuthorPayload;
import com.example.graphqlserver.model.Author;
import com.example.graphqlserver.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthorController {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Get all authors
    @QueryMapping
    public List<Author> authors() {
        return authorRepository.findAll();
    }

    // Get author by id
    @QueryMapping
    public Author authorById(@Argument("id") int id) {
        return authorRepository.findById(id).orElse(null);
    }

    // Get authors by last name
    @QueryMapping
    public List<Author> authorsByLastname(@Argument("lastName") String lastName) {
        return authorRepository.findByLastName(lastName);
    }

    // Update author's first name by id
    @MutationMapping
    public String updateAuthorFirstNameById(@Argument("authorId") int id, @Argument("firstName") String firstName) {
        return authorRepository.findById(id)
                .map(author -> {
                    String oldFirstName = author.getFirstName();
                    author.setFirstName(firstName);
                    authorRepository.save(author); // persist change
                    return oldFirstName;
                })
                .orElse(null);
    }

    // Add a new author
    @MutationMapping
    public AddAuthorPayload addAuthor(@Argument AddAuthorInput input) {
        Author author = new Author();
        author.setFirstName(input.firstName());
        author.setLastName(input.lastName());

        Author saved = authorRepository.save(author);
        return new AddAuthorPayload(saved);
    }
}
