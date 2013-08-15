#!/bin/sh
mvn -DpackageName=[PACKAGE_NAME] -DlicenseFile=[FULL_PATH_TO_THE_LICENSE_FILE] -DinterfaceName=[INTERFACE_NAME] -Dfunction=[FUNCTION_PREFIX] -Dexceptions=[EXCEPTION_NAME] -DminParam=[MIN_PARAM] -DmaxParam=[MAX_PARAM] -Dfilter=[FILTER] install
