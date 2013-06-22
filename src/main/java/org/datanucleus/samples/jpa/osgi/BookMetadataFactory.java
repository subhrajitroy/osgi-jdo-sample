package org.datanucleus.samples.jpa.osgi;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

class BookMetadataFactory implements MetadataFactory {

    public JDOMetadata populate(JDOMetadata md, String fullyQualifiedClassName, String fieldName) {
        String packageName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf("."));
        PackageMetadata pmd = md.newPackageMetadata(packageName);
        String className = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
        ClassMetadata cmd = pmd.newClassMetadata(className);

        cmd.setTable(className.toUpperCase()).setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        FieldMetadata fmd = cmd.newFieldMetadata(fieldName);
        fmd.setNullValue(NullValue.DEFAULT).setColumn(fieldName);
        return md;
    }
}
