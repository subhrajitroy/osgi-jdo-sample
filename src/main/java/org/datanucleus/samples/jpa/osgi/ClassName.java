package org.datanucleus.samples.jpa.osgi;

public class ClassName {
    private String fullyQualifiedClassName;

    public ClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public String getPackageName() {
        int lastIndexOfDot = fullyQualifiedClassName.lastIndexOf(".");
        return lastIndexOfDot > -1 ? fullyQualifiedClassName.substring(0, lastIndexOfDot) : "";
    }

    public String getSimpleName() {
        int lastIndexOfDot = fullyQualifiedClassName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return fullyQualifiedClassName;
        }
        return fullyQualifiedClassName.substring(lastIndexOfDot + 1, fullyQualifiedClassName.length());
    }
}
