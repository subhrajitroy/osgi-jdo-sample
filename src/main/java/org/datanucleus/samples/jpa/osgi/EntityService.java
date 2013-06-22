package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.datanucleus.samples.jpa.osgi.builder.ClassBuilder;
import org.datanucleus.samples.jpa.osgi.builder.Field;
import org.datanucleus.samples.jpa.osgi.enhancer.MotechJDOEnhancer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jdo.JDOEnhancer;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class EntityService {


    private ClassLoader classLoader;

    public static void main(String[] args) throws Exception {
//        createEntity("foo.Epic", "name");


        // define extension class for Publisher dynamically
        //byte[] publisherBytes = definePublisherExt(enhanceCl);
        // define jdo metadata for Publisher Extension programatically
        //PublisherMetadataFactory publisherMdf = new PublisherMetadataFactory();
        // enchance PublisherExt at runtime (Publisher is enhanced at compile time)
        //byte[] enhancedPublisherBytes = enhance("fuu.PublisherExt", publisherBytes, publisherMdf, enhanceCl);
        // define enhanced PublisherExt in new classloader
        //persistCl.defineClass("fuu.PublisherExt", enhancedPublisherBytes);
        // register Book metadata with Book
        //register(pmf, publisherMdf, "fuu.PublisherExt");

        // sample Publisher insert with extension
        //Class publisherExt = persistCl.loadClass("fuu.PublisherExt");
        //Object publisher = publisherExt.getDeclaredConstructor(String.class, String.class).newInstance("scholastic", "print");
        //insert(pmf, publisher);

        // sample Publisher insert
//        insert(pmf, new Publisher("pearson"));

        // get all Publishers including extensions
//        printAll(pmf, Publisher.class);
    }


    public void createEntity(String className, String fieldName) throws IOException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // used while enhancing

        JdoClassLoader enhanceCl = new JdoClassLoader(getContextClassLoader());
        // has enhanced classes, used while persisting
        JdoClassLoader persistCl = new JdoClassLoader(getContextClassLoader());

        Properties properties = getProperties();
        // use separate classloader for persistence objects
        properties.put("datanucleus.primaryClassLoader", persistCl);
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(new HashMap(properties));

        // define Book class dynamically
        byte[] bookBytes = defineClass(enhanceCl, className, fieldName);
        // define jdo metadata for Book programatically
        BookMetadataFactory bookMdf = new BookMetadataFactory();
        // enchance Book at runtime
        byte[] enhancedBytes = enhance(className, fieldName, bookBytes, bookMdf, enhanceCl);
        // define enhanced Book in new classloader
        persistCl.defineClass(className, enhancedBytes);
        // register Book metadata with Book
        register(pmf, bookMdf, className, fieldName);

        // sample Book insert
        Class bookClass = persistCl.loadClass(className);
        Object book = bookClass.getDeclaredConstructor(String.class).newInstance(UUID.randomUUID().toString());
        insert(pmf, book);
    }

    @PostConstruct
    private void setClassLoader() throws Exception {
        classLoader = Thread.currentThread().getContextClassLoader();
        createEntity("com.motech.Foo", "bar");
        System.out.println("Set class loader " + classLoader.toString());
    }

    private ClassLoader getContextClassLoader() {
        System.out.println(classLoader.toString());
        return classLoader;
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

    private static byte[] definePublisherExt(JdoClassLoader classLoader) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
//        pool.insertClassPath(new ClassClassPath(Publisher.class));
        CtClass cc = pool.makeClass("fuu.PublisherExt");
        cc.setSuperclass(pool.get("foo.Publisher"));

        cc.addField(CtField.make("private java.lang.String format;", cc));

        cc.addConstructor(CtNewConstructor.make("public PublisherExt(java.lang.String name, java.lang.String format) { super(name); this.format=format; }", cc));

        byte[] classBytes = cc.toBytecode();
        classLoader.defineClass("fuu.PublisherExt", classBytes);
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

    private static void register(PersistenceManagerFactory pmf, MetadataFactory mdf, String className, String fieldName) {
        pmf.registerMetadata(mdf.populate(pmf.newMetadata(), className, fieldName));
    }

    private static void insert(PersistenceManagerFactory pmf, Object o) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.begin();

        pm.makePersistent(o);

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

