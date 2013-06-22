package org.datanucleus.samples.jpa.osgi.builder;

import java.util.ArrayList;
import java.util.List;

public class ConstructorBuilder {

    private static final String SPACE = " ";
    private List<Field> fields = new ArrayList<Field>();
    private String className;

    public ConstructorBuilder() {
    }

    private ConstructorBuilder(String className) {
        this.className = className;
    }

    public ConstructorBuilder withParameter(Field field) {
        fields.add(field);
        return this;
    }

    public ConstructorBuilder forClass(String className) {
        this.className = className;
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder("public")
                .append(SPACE)
                .append(className).append("(");
        addConstructorParameters(builder);
        builder.append(")");
        buildConstructorBody(builder);
        return builder.toString();
    }

    private void addConstructorParameters(StringBuilder builder) {
        for (Field field : fields) {
            builder.append(field);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
    }

    private void buildConstructorBody(StringBuilder builder) {
        builder.append("{");
        for (Field field : fields) {
            builder.append(String.format("this.%s=%s;", field.getName(), field.getName()));
        }
        builder.append("}");
    }
}
