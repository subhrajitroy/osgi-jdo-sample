package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import org.datanucleus.samples.jpa.osgi.builder.ClassBuilder;
import org.datanucleus.samples.jpa.osgi.builder.Field;
import org.datanucleus.samples.jpa.osgi.domain.Book;
import org.datanucleus.samples.jpa.osgi.domain.Patient;
import org.datanucleus.samples.jpa.osgi.enhancer.MotechJDOEnhancer;
import org.datanucleus.samples.jpa.osgi.factory.ClassMetadataFactory;
import org.datanucleus.samples.jpa.osgi.factory.MetadataFactory;
import org.datanucleus.samples.jpa.osgi.factory.PatientMetadataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jdo.JDOEnhancer;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Service
public class EntityServiceImpl implements EntityService {


    @Autowired
    @Qualifier("persistenceManagerFactory")
    private PersistenceManagerFactory persistenceManagerFactory;

    //defines jdo metadata for Publisher Extension programatically
    @Autowired
    private PatientMetadataFactory patientMetadataFactory;


    private Map<String, Class> classesForExtensionMap = new HashMap<String, Class>();

    public EntityServiceImpl() {
        classesForExtensionMap.put("patient", Patient.class);
        classesForExtensionMap.put("book", Book.class);
    }


    @Override
    public void extendEntity(String className) throws Exception {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();

        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(EntityServiceImpl.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoaderForPersistence);

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(classLoaderForPersistence);

        Class baseClass = classesForExtensionMap.get(className);
        String extendedClassName = generatedExtendedClassName(baseClass.getSimpleName());

        byte[] clazzBytes = ClassExtensionProvider.defineClassExtension(classLoaderForEnhancer, extendedClassName, baseClass);

        byte[] enhancedPatientBytes = enhance(extendedClassName, "format", clazzBytes, patientMetadataFactory, classLoaderForEnhancer);
        classLoaderForPersistence.defineClass(extendedClassName, enhancedPatientBytes);
        persistenceManagerFactory.registerMetadata(patientMetadataFactory.populate(persistenceManagerFactory.newMetadata(), extendedClassName, "format"));
        Class extendedClass = classLoaderForPersistence.loadClass(extendedClassName);
        Object extendedClassInstance = extendedClass.getDeclaredConstructor(String.class, String.class).newInstance("scholastic", "print");
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        persistenceManager.makePersistent(extendedClassInstance);
        Thread.currentThread().setContextClassLoader(oldContextClassLoader);
        printAll(persistenceManagerFactory, extendedClass);
    }

    private String generatedExtendedClassName(String className) {
        return String.format("org.motechproject.Motech%s", className);
    }


    @Override
    public void createEntity(String fullyQualifiedClassName, String fieldName) throws IOException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        JdoClassLoader classLoaderForPersistence = new JdoClassLoader(EntityServiceImpl.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoaderForPersistence);

        JdoClassLoader classLoaderForEnhancer = new JdoClassLoader(classLoaderForPersistence);

        byte[] customClassBytes = defineClass(classLoaderForEnhancer, fullyQualifiedClassName, fieldName);
        ClassMetadataFactory classMetaDataFactory = new ClassMetadataFactory();
        byte[] enhancedBytes = enhance(fullyQualifiedClassName, fieldName, customClassBytes, classMetaDataFactory, classLoaderForEnhancer);
        classLoaderForPersistence.defineClass(fullyQualifiedClassName, enhancedBytes);
        persistenceManagerFactory.registerMetadata(classMetaDataFactory.populate(persistenceManagerFactory.newMetadata(), fullyQualifiedClassName, fieldName));
        Class customClazz = classLoaderForPersistence.loadClass(fullyQualifiedClassName);
        Object customClazzInstance = customClazz.getDeclaredConstructor(String.class).newInstance(UUID.randomUUID().toString());
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        persistenceManager.makePersistent(customClazzInstance);
        persistenceManager.makePersistent(new Book("some book", "some author"));
        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }


    private static byte[] enhance(String fullyQualifiedClassName, String fieldName, byte[] classBytes, MetadataFactory mdf, ClassLoader classLoader) throws IOException {
        JDOEnhancer enhancer = new MotechJDOEnhancer(getProperties());
        enhancer.setClassLoader(classLoader);
        enhancer.registerMetadata(mdf.populate(enhancer.newMetadata(), fullyQualifiedClassName, fieldName));
        enhancer.addClass(fullyQualifiedClassName, classBytes);
        enhancer.enhance();
        return enhancer.getEnhancedBytes(fullyQualifiedClassName);
    }


    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(EntityServiceImpl.class.getClassLoader().getResourceAsStream("datanucleus.properties"));
        return properties;
    }

    private static byte[] defineClass(JdoClassLoader classLoader, String fullyQualifiedClassName, String fieldName) throws CannotCompileException, IOException {
        ClassName className = new ClassName(fullyQualifiedClassName);
        System.out.println("Package name " + className.getPackageName());
        System.out.println("Class name " + className.getName());
        ClassBuilder classBuilder = new ClassBuilder();
        classBuilder.withClassLoader(classLoader)
                .withName(className.getName()).inPackage(className.getPackageName()).withField(new Field(fieldName, String.class));
        return classBuilder.build();
    }

    private static void printAll(PersistenceManagerFactory pmf, Class clazz) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery(clazz);
        for (Object o : (List) q.execute()) {
            System.out.println(o);
        }
    }
}

