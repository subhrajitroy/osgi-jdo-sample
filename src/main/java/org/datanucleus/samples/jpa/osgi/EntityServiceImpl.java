package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import org.datanucleus.samples.jpa.osgi.builder.ClassBuilder;
import org.datanucleus.samples.jpa.osgi.builder.Field;
import org.datanucleus.samples.jpa.osgi.domain.Book;
import org.datanucleus.samples.jpa.osgi.domain.Patient;
import org.datanucleus.samples.jpa.osgi.enhancer.MotechJDOEnhancer;
import org.datanucleus.samples.jpa.osgi.factory.ClassMetadataFactory;
import org.datanucleus.samples.jpa.osgi.factory.MetadataFactory;
import org.datanucleus.samples.jpa.osgi.factory.PatientMetadaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jdo.JDOEnhancer;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class EntityServiceImpl implements EntityService {


    @Autowired
    @Qualifier("persistenceManagerFactory")
    private PersistenceManagerFactory persistenceManagerFactory;


    @Override
    public void extendExistingEntity() throws Exception {
        //        define extension class for Publisher dynamically

        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(EntityServiceImpl.class.getClassLoader());

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(getClassLoader());
        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(getClassLoader());

        byte[] patientBytes = ClassExtensionProvider.definePatientExtension(classLoaderForEnhancer, "org.motechproject.MotechPatient");
//        define jdo metadata for Publisher Extension programatically
        PatientMetadaFactory patientMetadataFactory = new PatientMetadaFactory();
//        enchance PublisherExt at runtime (Publisher is enhanced at compile time)
        byte[] enhancedPublisherBytes = enhance("org.motechproject.MotechPatient", "format", patientBytes, patientMetadataFactory, classLoaderForEnhancer);
//        define enhanced PublisherExt in new classloader
        classLoaderForPersistence.defineClass("org.motechproject.MotechPatient", enhancedPublisherBytes);
//        register Book metadata with Book

        persistenceManagerFactory.registerMetadata(patientMetadataFactory.populate(persistenceManagerFactory.newMetadata(), "org.motechproject.MotechPatient", "format"));

//        sample Publisher insert with extension
        Class motechPatientClass = classLoaderForPersistence.loadClass("org.motechproject.MotechPatient");
        Object publisher = motechPatientClass.getDeclaredConstructor(String.class, String.class).newInstance("scholastic", "print");


        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();

        persistenceManager.makePersistent(publisher);


//        sample Publisher insert

        persistenceManager.makePersistent(new Patient("John"));

        Thread.currentThread().setContextClassLoader(oldContextClassLoader);

        // get all Publishers including extensions
        printAll(persistenceManagerFactory, Patient.class);
    }


    @Override
    public void createEntity(String fullyQualifiedClassName, String fieldName) throws IOException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // used while enhancing

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        // adding this class as context class loader so that the newly defined class is available
        //As I have to use a transactional proxy,cannot set primary class loader
        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(EntityServiceImpl.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoaderForPersistence);

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(classLoaderForPersistence);
        // has enhanced classes, used while persisting

        // define Book class dynamically
        byte[] customClassBytes = defineClass(classLoaderForEnhancer, fullyQualifiedClassName, fieldName);
        // define jdo metadata for Book programatically
        ClassMetadataFactory classMetaDataFactory = new ClassMetadataFactory();
        // enchance Book at runtime
        byte[] enhancedBytes = enhance(fullyQualifiedClassName, fieldName, customClassBytes, classMetaDataFactory, classLoaderForEnhancer);
        // define enhanced Book in new classloader
        classLoaderForPersistence.defineClass(fullyQualifiedClassName, enhancedBytes);
        // register Book metadata with Book
        persistenceManagerFactory.registerMetadata(classMetaDataFactory.populate(persistenceManagerFactory.newMetadata(), fullyQualifiedClassName, fieldName));

        // sample Book insert
        Class customClazz = classLoaderForPersistence.loadClass(fullyQualifiedClassName);
        Object customClazzInstance = customClazz.getDeclaredConstructor(String.class).newInstance(UUID.randomUUID().toString());

        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        persistenceManager.makePersistent(customClazzInstance);
        persistenceManager.makePersistent(new Book("some book", "some author"));

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }


    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(EntityServiceImpl.class.getClassLoader().getResourceAsStream("datanucleus.properties"));
        return properties;
    }

    private static byte[] defineClass(JdoClassLoader classLoader, String fullyQualifiedClassName, String fieldName) throws CannotCompileException, IOException {

        int lastIndexOfDot = fullyQualifiedClassName.lastIndexOf(".");
        String packageName = fullyQualifiedClassName.substring(0, lastIndexOfDot);
        String className = fullyQualifiedClassName.substring(lastIndexOfDot + 1, fullyQualifiedClassName.length());

        System.out.println("Package name " + packageName);
        System.out.println("Class name " + className);

        ClassBuilder classBuilder = new ClassBuilder();
        classBuilder.withClassLoader(classLoader)
                .withName(className).inPackage(packageName).withField(new Field(fieldName, String.class));

        return classBuilder.build();
    }

    public static byte[] enhance(String fullyQualifiedClassName, String fieldName, byte[] classBytes, MetadataFactory mdf, ClassLoader classLoader) throws IOException {
        JDOEnhancer enhancer = new MotechJDOEnhancer(getProperties());
        enhancer.setClassLoader(classLoader);
        enhancer.setVerbose(true);
        enhancer.registerMetadata(mdf.populate(enhancer.newMetadata(), fullyQualifiedClassName, fieldName));
        enhancer.addClass(fullyQualifiedClassName, classBytes);
        enhancer.enhance();
        return enhancer.getEnhancedBytes(fullyQualifiedClassName);
    }

    private static void printAll(PersistenceManagerFactory pmf, Class clazz) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery(clazz);
        for (Object o : (List) q.execute()) {
            System.out.println(o);
        }
    }
}

