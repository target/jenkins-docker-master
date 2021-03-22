ARG JENKINS_VER=2.277.1
ARG RELEASE=1

FROM target/jenkins-docker-master:${JENKINS_VER}-${RELEASE}
USER root

RUN curl -sL https://github.com/logzio/jmx2graphite/releases/download/v1.1.0/jmx2graphite-1.1.0-javaagent.jar > /usr/share/jenkins/jmx2graphite.jar && \
    curl -sL https://raw.githubusercontent.com/target/jenkins-docker-master/master/examples/files/base-plugins.txt > /usr/share/jenkins/base-plugins.txt && \
    curl -sL https://raw.githubusercontent.com/target/jenkins-docker-master/master/examples/files/setup_security.groovy > /usr/share/jenkins/ref/init.groovy.d/setup.groovy.override && \
    curl -sL https://raw.githubusercontent.com/target/jenkins-docker-master/master/examples/files/setup_reporting.groovy > /usr/share/jenkins/ref/init.groovy.d/setup_reporting.groovy.override

USER jenkins

RUN cat /usr/share/jenkins/base-plugins.txt | xargs /usr/local/bin/install-plugins.sh
