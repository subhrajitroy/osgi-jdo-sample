package org.datanucleus.samples.jpa.osgi;

public class JdoClassLoader extends ClassLoader {
    public JdoClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void defineClass(String className, byte[] classBytes) {
        defineClass(className, classBytes, 0, classBytes.length);
    }
}
