ARG JENKINS_VER=2.60.3
ARG JENKINS_REL=1

FROM target/jenkins-docker-master:${JENKINS_VER}-${JENKINS_REL}
USER root

RUN curl -L https://github.com/logzio/jmx2graphite/releases/download/v1.1.0/jmx2graphite-1.1.0-javaagent.jar > /usr/share/jenkins/jmx2graphite.jar

USER jenkins

COPY base-plugins.txt /usr/share/jenkins/base-plugins.txt
RUN cat /usr/share/jenkins/base-plugins.txt | xargs /usr/local/bin/install-plugins.sh

COPY setup_security.groovy /usr/share/jenkins/ref/init.groovy.d/setup.groovy.override
COPY setup_reporting.groovy /usr/share/jenkins/ref/init.groovy.d/setup_reporting.groovy.override
