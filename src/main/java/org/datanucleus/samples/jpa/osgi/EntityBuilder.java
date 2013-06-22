package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;

import java.io.IOException;

public class EntityBuilder {

    public byte[] build() throws CannotCompileException, IOException {
        JdoClassLoader classLoader = new JdoClassLoader(Thread.currentThread().getContextClassLoader());
        CtClass cc = ClassPool.getDefault().makeClass("org.thoughtworks.motechproject.Patient");

        cc.addField(CtField.make("private java.lang.String name;", cc));

        cc.addConstructor(CtNewConstructor.make("public Patient(java.lang.String name) { this.name = name; }", cc));

        byte[] classBytes = cc.toBytecode();
        classLoader.defineClass("fuu.Book", classBytes);
        return classBytes;
    }

    public static void main(String[] args) throws CannotCompileException, IOException {
        new EntityBuilder().build();
    }

}
