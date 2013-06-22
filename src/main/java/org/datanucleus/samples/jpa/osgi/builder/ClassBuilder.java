package org.datanucleus.samples.jpa.osgi.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import org.datanucleus.samples.jpa.osgi.JdoClassLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassBuilder {

    private String className;
    private String packageName;
    private List<Field> fields = new ArrayList<Field>();
    private JdoClassLoader classLoader;

    public ClassBuilder withName(String className) {
        this.className = className;
        return this;
    }

    public ClassBuilder inPackage(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public ClassBuilder withField(Field field) {
        fields.add(field);
        return this;
    }

    public ClassBuilder withClassLoader(JdoClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }


    public byte[] build() {
        try {
            CtClass cc = ClassPool.getDefault().makeClass(fullyQualifiedClassName());
            addFields(cc);
            buildConstructor(cc);
            byte[] classBytes = cc.toBytecode();
            classLoader.defineClass(fullyQualifiedClassName(), classBytes);
            return classBytes;
        } catch (CannotCompileException ex) {
            throw new ClassBuilderException(ex.getReason(), ex);
        } catch (IOException ex) {
            throw new ClassBuilderException(ex.getMessage(), ex);
        }
    }

    private void addFields(CtClass cc) throws CannotCompileException {
        for (Field field : fields) {
            cc.addField(CtField.make(String.format("%s;", field.toStringWithAccessModifier()), cc));
        }
    }

    private void buildConstructor(CtClass cc) throws CannotCompileException {
        ConstructorBuilder constructorBuilder = new ConstructorBuilder().forClass(className);
        for (Field field : fields) {
            constructorBuilder.withParameter(field);
        }
        String constructor = constructorBuilder.build();
        System.out.println(constructor);
        cc.addConstructor(CtNewConstructor.make(constructor, cc));
    }

    private String fullyQualifiedClassName() {
        return String.format("%s.%s", packageName, className);
    }

}
