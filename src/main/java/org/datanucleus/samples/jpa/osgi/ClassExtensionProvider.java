package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.datanucleus.samples.jpa.osgi.domain.Patient;

import java.io.IOException;

public class ClassExtensionProvider<T> {

    public static byte[] definePatientExtension(JdoClassLoader classLoader, String qualifiedExtendedClassName) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Patient.class));
        CtClass cc = pool.makeClass(qualifiedExtendedClassName);
        cc.setSuperclass(pool.get(Patient.class.getName()));

        cc.addField(CtField.make("private java.lang.String format;", cc));

        cc.addConstructor(CtNewConstructor.make("public MotechPatient(java.lang.String name, java.lang.String format) { super(name); this.format=format; }", cc));

        byte[] classBytes = cc.toBytecode();
        classLoader.defineClass("org.motechproject.MotechPatient", classBytes);
        return classBytes;
    }
}