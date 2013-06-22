package org.datanucleus.samples.jpa.osgi.builder;

public class Field {
    private String name;
    private String accessModifier;
    private Class type;

    public Field(String name, Class type) {
        this(name, "private", type);
    }

    public Field(String name, String accessModifier, Class type) {
        this.name = name;
        this.accessModifier = accessModifier;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type.getName(), name);
    }

    public String toStringWithAccessModifier() {
        return String.format("%s %s %s", accessModifier, type.getName(), name);
    }
}
