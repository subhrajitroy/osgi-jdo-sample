osgi-jdo-sample
===============

Code is result of a spike to investigate how to use JDO in OSGI environment provided by felix under following
circumstances.

You will need to download

https://code.google.com/p/motech/

1. Save instance of an existing type (Book).
   http://<motech_platform_context>/module/jdo/spike/book/create/<book_name_here>/<author_here>

2. Save instance of a type created on runtime.
   http://motech_platform_context/module/jdo/spike/entity/create/<class_name_here>/<field_name_here>

   eg. If you hit the URL http://motech_platform_context/module/jdo/spike/entity/create/Address/street
       Then this bundle will create a class called org.motechproject.Adress on the fly with a String field named street
       An instance of this type will be created and saved in the table using JDO.
       JDO will create a table called address to save the instance.
3. Add field to an existing class and save an instance of the new type.
       http://localhost:8080/motech-platform/module/jdo/spike/entity/extend/<class-to-extend>

       This will add a field named "format" to the existing class.

       As of now the class-to-extend can be either  book or patient.
       Datanucleus creates additional tables for the extended classes.In this case motechbook and motechpatient.


References
=================

I have taken help and code from multiple sources.

1. https://github.com/datanucleus/sample-jpa-osgi
