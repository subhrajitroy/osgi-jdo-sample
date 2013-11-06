package org.datanucleus.samples.jpa.osgi.factory;

import org.datanucleus.samples.jpa.osgi.EntityService;
import org.springframework.stereotype.Component;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

@Component
public class PersistenceManagerFactoryFactory {

    public PersistenceManagerFactory getPersistenceManagerFactory(ClassLoader classLoaderForPersistence) throws IOException {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PersistenceManagerFactoryFactory.class.getClassLoader());
        Properties properties = getProperties();
        // use separate classloader for persistence objects
        properties.put("datanucleus.primaryClassLoader", classLoaderForPersistence);
        PersistenceManagerFactory persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(new HashMap(properties));
        Thread.currentThread().setContextClassLoader(oldContextClassLoader);
        return persistenceManagerFactory;
    }

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(EntityService.class.getClassLoader().getResourceAsStream("datanucleus.properties"));
        return properties;
    }

}