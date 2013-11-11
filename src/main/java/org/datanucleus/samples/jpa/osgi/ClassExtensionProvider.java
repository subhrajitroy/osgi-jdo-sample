package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

import java.io.IOException;

public class ClassExtensionProvider {

    public static byte[] defineClassExtension(JdoClassLoader classLoader, String qualifiedExtendedClassName, Class baseClass) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(baseClass));
        CtClass cc = pool.makeClass(qualifiedExtendedClassName);
        cc.setSuperclass(pool.get(baseClass.getName()));

        cc.addField(CtField.make("private java.lang.String format;", cc));


        ClassName className = new ClassName(qualifiedExtendedClassName);

        cc.addConstructor(CtNewConstructor.make(String.format("public %s(java.lang.String name, java.lang.String format) { super(name); this.format=format; }", className.getSimpleName()), cc));

        byte[] classBytes = cc.toBytecode();
        classLoader.defineClass(qualifiedExtendedClassName, classBytes);
        return classBytes;
    }
}