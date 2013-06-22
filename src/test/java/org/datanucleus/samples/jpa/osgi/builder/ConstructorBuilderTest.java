package org.datanucleus.samples.jpa.osgi.builder;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class ConstructorBuilderTest {

    @Test
    public void shouldBuildConstructorStringRepresentation() {
        String bookConstructor = new ConstructorBuilder().forClass("Book").withParameter(new Field("name", String.class)).build();
        assertThat(bookConstructor, Is.is("public Book(java.lang.String name){this.name=name;}"));
        String patientConstructor = new ConstructorBuilder().forClass("Patient")
                .withParameter(new Field("name", String.class))
                .withParameter(new Field("age", Integer.class))
                .build();
        assertThat(patientConstructor, Is.is("public Patient(java.lang.String name,java.lang.Integer age){this.name=name;this.age=age;}"));
    }

}
