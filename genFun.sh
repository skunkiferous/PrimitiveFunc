#!/bin/sh
cd FuncGenerated
mvn -DpackageName=[PACKAGE_NAME] -DlicenseFile=[FULL_PATH_TO_THE_LICENSE_FILE] -DprocNamePrefix=[PROC_NAME_PREFIX] -DfuncNameInfix=[FUNC_NAME_INFIX] -Dfunction=[FUNCTION_PREFIX] -Dexceptions=[EXCEPTION_NAME] -DminParam=[MIN_PARAM] -DmaxParam=[MAX_PARAM] -DshortName=[SHORT_NAME] -Dfilter=[FILTER] clean install
cd ..
