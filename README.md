PrimitiveFunc
=============

Finally fills the gap of using generic "Function" interfaces in Java, that do NOT require you to turn your input or output of primitive type into primitive wrappers, therefore pointlessly creating garbage.

The class com.blockwithme.util.GenFunc can be used to generate your customized function-set, if you prefer. 

* To compile and build the main bundle, run 'mvn install' this step generates PrimitiveFunc-x.y.z-SNAPSHOT.jar in you maven repo.
* To generate sources and packaging them as jar run the following maven command :

  mvn -DinterfaceName=Func -Dfunction=func -Dexceptions=java.lang.Exception -DminParam=0 -DmaxParam=1 install -Pcode-generator
  
  Usage :
  * interfaceName the interface name prefix, for example 'Func'
	* function is the method name, for example 'apply' or 'call'.
	* exceptions can contain the optional name of a thrown 'Throwable'. Leave blank for no 'throws'. Example: 'java.io.IOException'.
	* minParam is the minimum number of parameters the functions will have, for example 0.
	* maxParam is the maximum number of parameters the functions will have, for example 3.
	* Hint: It supports up to 5 as the maximum number of parameters, but that would be near 1 million interfaces; not recommended.
	* Generated Package name can also be modified by editing pom.xml.

  (Note: this step generates PrimitiveFunc-Generated-x.y.z-SNAPSHOT.jar)
