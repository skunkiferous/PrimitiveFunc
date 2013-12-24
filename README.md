Primitive Function Interfaces
=============================

Finally fills the gap of using generic "Function" interfaces in Java, that do NOT require you to turn your input or output of primitive type into primitive wrappers, therefore *pointlessly creating garbage*.

The class com.blockwithme.fn.gen.GenFunc can be used to generate your customized function-set, if you prefer. 

Maven build instructions :

* To compile and build the main bundle, run 'mvn install' this step generates FuncGenerated?-x.y.z.jar in your maven repo.
* To generate sources and packaging them as jar go to FuncGenerated/[whatever] and run the following maven command :

```
  mvn install
```  

There is a bunch of properties that can be used to customize the generated code. The default values normally used are in the POM itself. Here an example of a fully customized generation:

```
  mvn -DpackageName=[PACKAGE_NAME] -DlicenseFile=[FULL_PATH_TO_THE_LICENSE_FILE] -DprocNamePrefix=[PROC_NAME_PREFIX] -DfuncNameInfix=[FUNC_NAME_INFIX] -Dfunction=[FUNCTION_PREFIX] -Dexceptions=[EXCEPTION_NAME] -DminParam=[MIN_PARAM] -DmaxParam=[MAX_PARAM] -DshortName=[SHORT_NAME] -Dfilter=[FILTER] install
```  

Usage :
  * packageName the java package name where sources will be generated. This is an Optional parameter and defaults to 'com.blockwithme.fn'.
  * licenseFile the full path to the license file, this is an Optional parameter and defaults to the APACHE_LICENSE_HEADER.txt packaged with this project.
  * procNamePrefix the interface name prefix, when the methods returns "void", for example 'Proc'
  * funcNameInfix the interface name infix, when the method does not return "void", for example 'Func'
  * function is the method name, for example 'apply' or 'call'.
  * exceptions can contain the optional name of a thrown 'Throwable'. Leave blank for no 'throws'. Example: 'java.io.IOException'.
  * minParam is the minimum number of parameters the functions will have, for example 0.
  * maxParam is the maximum number of parameters the functions will have, for example 3.
  * shortName specifies if the names should be like "ProcZL" (true) or "ProcBooleanLong" (false)
  * filter is the name of a class that implements com.blockwithme.fn.gen.FuncFilter (optional).
  
  Hint: It supports up to 5 as the maximum number of parameters, but that would be near 1 million interfaces; not recommended.
  (Note: this step generates FuncGenerated-x.y.z.jar, modify the 'FuncGenerated/pom.xml' appropriately if the artifact-ids need any modifications.)

Note: To stop the "exponential explosion" of the number of functor interfaces, the "4-paramters" functor only takes an object as last parameter, and the "5-parameters" functor only take objects as the last two parameters. In other words, only the first 3 parameters are "totally free". This can be worked around in many cases by moving the "object parameters" to the end. (Perhaps, having the Objects as first parameters would be better?)

Primitive Tuple Implementations
===============================

Finally fills the gap of using generic "Tuples" in Java, that do NOT require you to turn your input or output of primitive type into primitive wrappers, therefore pointlessly creating garbage.

The class com.blockwithme.tuples.gen.GenTuple can be used to generate your customized tuple-set, if you prefer. 

Maven build instructions :

* To compile and build the main bundle, run 'mvn install' this step generates TupleGenerated-x.y.z.jar in your maven repo.
* To generate sources and packaging them as jar go to TupleGenerated and run the following maven command :

```
  mvn install
```  

There is a bunch of properties that can be used to customize the generated code. The default values normally used are in the POM itself. Here an example of a fully customized generation:

```
  mvn -DpackageName=[PACKAGE_NAME] -DlicenseFile=[FULL_PATH_TO_THE_LICENSE_FILE] -DminParam=[MIN_PARAM] -DmaxParam=[MAX_PARAM] -Dfilter=[FILTER] install
```  

Usage :
  * packageName the java package name where sources will be generated. This is an Optional parameter and defaults to 'com.blockwithme.tuples'.
  * licenseFile the full path to the license file, this is an Optional parameter and defaults to the APACHE_LICENSE_HEADER.txt packaged with this project.
  * minParam is the minimum number of parameters the tuples will have, for example 0.
  * maxParam is the maximum number of parameters the tuples will have, for example 3.
  * filter is the name of a class that implements com.blockwithme.tuples.gen.TupleFilter (optional).
  
  Hint: It supports up to 5 as the maximum number of parameters, but that would be near 1 million tuples; not recommended.
  (Note: this step generates TupleGenerated-x.y.z.jar, modify the 'TupleGenerated/pom.xml' appropriately if the artifact-ids need any modifications.)
