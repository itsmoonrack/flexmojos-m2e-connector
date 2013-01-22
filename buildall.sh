#!/bin/sh

LOCALREPO=/tmp/m2e-flexmojos.localrepo

mvn -f m2e-flexmojos-runtime/pom.xml clean install -Dmaven.repo.local=$LOCALREPO
mvn clean install -Dmaven.repo.local=$LOCALREPO
