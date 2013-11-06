package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.datanucleus.samples.jpa.osgi.builder.ClassBuilder;
import org.datanucleus.samples.jpa.osgi.builder.Field;
import org.datanucleus.samples.jpa.osgi.domain.Patient;
import org.datanucleus.samples.jpa.osgi.enhancer.MotechJDOEnhancer;
import org.datanucleus.samples.jpa.osgi.factory.PersistenceManagerFactoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.JDOEnhancer;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class EntityService {


    private ClassLoader classLoader;

    @Autowired
    private PersistenceManagerFactoryFactory persistenceManagerFactoryFactory;


    public void extendExistingEntity() throws Exception {
        //        define extension class for Publisher dynamically

        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(EntityService.class.getClassLoader());

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(getClassLoader());
        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(getClassLoader());

        byte[] publisherBytes = definePatientExtension(classLoaderForEnhancer);
//        define jdo metadata for Publisher Extension programatically
        PatientMetadaFactory patientMetadataFactory = new PatientMetadaFactory();
//        enchance PublisherExt at runtime (Publisher is enhanced at compile time)
        byte[] enhancedPublisherBytes = enhance("org.motechproject.MotechPatient", "format", publisherBytes, patientMetadataFactory, classLoaderForEnhancer);
//        define enhanced PublisherExt in new classloader
        classLoaderForPersistence.defineClass("org.motechproject.MotechPatient", enhancedPublisherBytes);
//        register Book metadata with Book

        PersistenceManagerFactory persistenceManagerFactory = persistenceManagerFactoryFactory.getPersistenceManagerFactory(classLoaderForPersistence);

        persistenceManagerFactory.registerMetadata(patientMetadataFactory.populate(persistenceManagerFactory.newMetadata(), "org.motechproject.MotechPatient", "format"));

//        sample Publisher insert with extension
        Class motechPatientClass = classLoaderForPersistence.loadClass("org.motechproject.MotechPatient");
        Object publisher = motechPatientClass.getDeclaredConstructor(String.class, String.class).newInstance("scholastic", "print");
        insertIntoDb(persistenceManagerFactory, publisher);

//        sample Publisher insert
        insertIntoDb(persistenceManagerFactory, new Patient("John"));

        Thread.currentThread().setContextClassLoader(oldContextClassLoader);

        // get all Publishers including extensions
        printAll(persistenceManagerFactory, Patient.class);
    }


    public void createEntity(String fullyQualifiedClassName, String fieldName) throws IOException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // used while enhancing

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(EntityService.class.getClassLoader());

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(getClassLoader());
        // has enhanced classes, used while persisting
        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(getClassLoader());

        PersistenceManagerFactory persistenceManagerFactory = persistenceManagerFactoryFactory.getPersistenceManagerFactory(classLoaderForPersistence);

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
        Class bookClass = classLoaderForPersistence.loadClass(fullyQualifiedClassName);
        Object book = bookClass.getDeclaredConstructor(String.class).newInstance(UUID.randomUUID().toString());
        insertIntoDb(persistenceManagerFactory, book);

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }


    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(EntityService.class.getClassLoader().getResourceAsStream("datanucleus.properties"));
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

    private static byte[] definePatientExtension(JdoClassLoader classLoader) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Patient.class));
        CtClass cc = pool.makeClass("org.motechproject.MotechPatient");
        cc.setSuperclass(pool.get(Patient.class.getName()));

        cc.addField(CtField.make("private java.lang.String format;", cc));

        cc.addConstructor(CtNewConstructor.make("public MotechPatient(java.lang.String name, java.lang.String format) { super(name); this.format=format; }", cc));

        byte[] classBytes = cc.toBytecode();
        classLoader.defineClass("org.motechproject.MotechPatient", classBytes);
        return classBytes;
    }

    private static byte[] enhance(String fullyQualifiedClassName, String fieldName, byte[] classBytes, MetadataFactory mdf, ClassLoader classLoader) throws IOException {
        JDOEnhancer enhancer = new MotechJDOEnhancer(getProperties());
        enhancer.setClassLoader(classLoader);
        enhancer.setVerbose(true);
        enhancer.registerMetadata(mdf.populate(enhancer.newMetadata(), fullyQualifiedClassName, fieldName));
        enhancer.addClass(fullyQualifiedClassName, classBytes);
        enhancer.enhance();
        return enhancer.getEnhancedBytes(fullyQualifiedClassName);
    }

    private static void insertIntoDb(PersistenceManagerFactory persistenceManagerFactory, Object objectToSave) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.begin();
        pm.makePersistent(objectToSave);
        tx.commit();
    }

    private static void printAll(PersistenceManagerFactory pmf, Class clazz) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery(clazz);
        for (Object o : (List) q.execute()) {
            System.out.println(o);
        }
    }
}

