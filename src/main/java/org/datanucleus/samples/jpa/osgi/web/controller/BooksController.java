package org.datanucleus.samples.jpa.osgi.web.controller;


import org.datanucleus.samples.jpa.osgi.domain.Book;
import org.datanucleus.samples.jpa.osgi.repository.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class BooksController {

    @Autowired
    private Books books;

    @RequestMapping("/book/create/{name}/{author}")
    @ResponseBody
    public String createBook(@PathVariable String name, @PathVariable String author) throws IOException {
        Book book = new Book(name, author);
        books.save(book);
        return "Number of Books " + books.all().size();
    }

    @RequestMapping(value = "/book/status", method = RequestMethod.GET)
    @ResponseBody
    public String status() {
        return "book - ok";
    }


}
