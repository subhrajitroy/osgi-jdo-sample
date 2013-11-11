package org.datanucleus.samples.jpa.osgi;

import javassist.CannotCompileException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface EntityService {
    @Transactional
    void extendEntity(String className) throws Exception;

    @Transactional
    void createEntity(String fullyQualifiedClassName, String fieldName) throws IOException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
