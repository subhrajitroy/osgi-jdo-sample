package org.datanucleus.samples.jpa.osgi.web.controller;


import org.datanucleus.samples.jpa.osgi.domain.Book;
import org.datanucleus.samples.jpa.osgi.factory.PersistenceManagerFactoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.io.IOException;

@Controller
public class BooksController {


    @Autowired
    PersistenceManagerFactoryFactory persistenceManagerFactoryFactory;

    @RequestMapping("/book/create/{name}/{author}")
    @ResponseBody
    public String createBook(@PathVariable String name, @PathVariable String author) throws IOException {
        PersistenceManagerFactory persistenceManagerFactory = persistenceManagerFactoryFactory.getPersistenceManagerFactory(BooksController.class.getClassLoader());
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        Transaction transaction = persistenceManager.currentTransaction();
        transaction.begin();
        Book book = new Book(name, author);
        persistenceManager.makePersistent(book);
        transaction.commit();
        return "Book name " + book.getName() + " by " + book.getAuthor();
    }

    @RequestMapping(value = "/book/status", method = RequestMethod.GET)
    @ResponseBody
    public String status() {
        return "book - ok";
    }


}
