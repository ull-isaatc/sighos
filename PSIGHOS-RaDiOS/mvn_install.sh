#!/bin/bash

mvn install:install-file -Dfile=./psighos_radios.jar -DgroupId=es.ull.iis -DartifactId=psighos_radios -Dversion=1.0 -Dpackaging=jar
rm -f ./psighos_radios.jar