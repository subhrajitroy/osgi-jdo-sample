package org.datanucleus.samples.jpa.osgi.factory;

import javax.jdo.metadata.JDOMetadata;

public interface MetadataFactory {

    JDOMetadata populate(JDOMetadata md, String fullyQualifiedClassName, String fieldName);
}
