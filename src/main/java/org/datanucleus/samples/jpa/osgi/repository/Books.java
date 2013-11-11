package org.datanucleus.samples.jpa.osgi.repository;

import org.datanucleus.samples.jpa.osgi.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

@Repository
public class Books {

    @Autowired
    @Qualifier("persistenceManagerFactory")
    private PersistenceManagerFactory persistenceManagerFactory;


    @Transactional
    public void save(Book book) {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Books.class.getClassLoader());

        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        persistenceManager.makePersistent(book);

        Thread.currentThread().setContextClassLoader(oldContextClassLoader);
    }


}
