package org.datanucleus.samples.jpa.osgi.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;

@Component
public class PersistenceManagerFactoryFactory {


    @Autowired
    @Qualifier("persistenceManagerFactory")
    private PersistenceManagerFactory persistenceManagerFactory;


    public PersistenceManagerFactory getPersistenceManagerFactory() throws IOException {
        //Will use the current thread context class loader
        return persistenceManagerFactory;
    }


}