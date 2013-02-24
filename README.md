PrimitiveFunc
=============

Finally fills the gap of using generic "Function" interfaces in Java, that do NOT require you to turn your input or output of primitive type into primitive wrappers, therefore pointlessly creating garbage.

The class com.blockwithme.util.GenFunc can be used to generate your customized function-set, if you prefer. 

Maven Build Procedure :

* To compile and build the main bundle, run 'mvn install' this step generates PrimitiveFunc-x.y.z-SNAPSHOT.jar in you maven repo.
* To generate sources and packaging them as jar run the following maven command :
mvn -DinterfaceName=Func -Dfunction=func -Dexceptions=java.lang.Exception -DminParam=0 -DmaxParam=1 install -Pcode-generator
* mvn clean cleans all generated files including the generated sources.