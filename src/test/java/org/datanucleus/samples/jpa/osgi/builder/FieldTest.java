package org.datanucleus.samples.jpa.osgi.builder;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class FieldTest {

    @Test
    public void shouldReturnExpectedFieldRepresentation() {
        Field field = new Field("name", String.class);
        assertThat(field.toString(), Is.is("java.lang.String name"));
    }

    @Test
    public void shouldReturnExpectedFieldRepresentationWithAccessModifier() {
        Field field = new Field("name", String.class);
        assertThat(field.toStringWithAccessModifier(), Is.is("private java.lang.String name"));
    }

}
