PrimitiveFunc
=============

Finally fills the gap of using generic "Function" interfaces in Java, that do NOT require you to turn your input or output of primitive type into primitive wrappers, therefore pointlessly creating garbage.

The class com.blockwithme.util.GenFunc can be used to generate your customized function-set, if you prefer. 

Maven build instructions :

* To compile and build the main bundle, run 'mvn install' this step generates PrimitiveFunc-x.y.z.jar in your maven repo.
* To generate sources and packaging them as jar go to source-generation and run the following maven command :

```
  mvn -DpackageName=[PACKAGE_NAME] -DlicenseFile=[FULL_PATH_TO_THE_LICENSE_FILE] -DinterfaceName=[INTERFACE_NAME] -Dfunction=[FUNCTION_PREFIX] -Dexceptions=[EXCEPTION_NAME] -DminParam=[MIN_PARAM] -DmaxParam=[MAX_PARAM] install
```  

Usage :
  * packageName the java package name where sources will be generated. This is an Optional parameter and defaults to 'com.blockwithme.func'.
  * licenseFile the full path to the license file, this is an Optional parameter and defaults to the APACHE_LICENSE_HEADER.txt packaged with this project.
  * interfaceName the interface name prefix, for example 'Func'
  * function is the method name, for example 'apply' or 'call'.
  * exceptions can contain the optional name of a thrown 'Throwable'. Leave blank for no 'throws'. Example: 'java.io.IOException'.
  * minParam is the minimum number of parameters the functions will have, for example 0.
  * maxParam is the maximum number of parameters the functions will have, for example 3.
  
  Hint: It supports up to 5 as the maximum number of parameters, but that would be near 1 million interfaces; not recommended.
  (Note: this step generates PrimitiveFunc-Generated-x.y.z.jar, modify the 'source-generation/pom.xml' appropriately if the artifact-ids need any modifications.)
