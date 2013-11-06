package org.datanucleus.samples.jpa.osgi.factory;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

public class PatientMetadaFactory implements MetadataFactory {

    public JDOMetadata populate(JDOMetadata md, String fullyQualifiedClassName, String fieldName) {
        PackageMetadata pmd = md.newPackageMetadata(fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf(".")));
        ClassMetadata cmd = pmd.newClassMetadata(fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1));

        cmd.setTable("MotechPatient").setDetachable(true);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.newDatastoreIdentityMetadata().setStrategy(IdGeneratorStrategy.INCREMENT);
        cmd.newInheritanceMetadata().setStrategy(InheritanceStrategy.NEW_TABLE);

        FieldMetadata fmd = cmd.newFieldMetadata(fieldName);
        fmd.setNullValue(NullValue.DEFAULT).setColumn(fieldName);
        return md;
    }
}
