package org.datanucleus.samples.jpa.osgi.builder;

public class ClassBuilderException extends RuntimeException {

    public ClassBuilderException(String reason, Throwable throwable) {
        super(reason, throwable);
    }
}
