ARG JENKINS_VER=2.277.1

FROM jenkins/jenkins:${JENKINS_VER}

ARG JENKINS_VER
ARG RELEASE=1

USER root

COPY files/jenkins_wrapper.sh /usr/local/bin/jenkins_wrapper.sh
COPY files/jenkins.yaml  /usr/local/bin/jenkins.yaml

ENV CASC_JENKINS_CONFIG=/usr/local/bin/jenkins.yaml

# create version files to ensure Jenkins does not prompt for setup
# allow slave to master control - https://wiki.jenkins.io/display/JENKINS/Slave+To+Master+Access+Control
# create file for plugin versioning
RUN echo -n ${JENKINS_VER} > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state && \
    echo -n ${JENKINS_VER} > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion && \
    mkdir -p /usr/share/jenkins/ref/secrets/ && echo false > /usr/share/jenkins/ref/secrets/slave-to-master-security-kill-switch && \
    echo ${JENKINS_VER}-${RELEASE} > /usr/share/jenkins/ref/jenkins.docker.image.version

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/jenkins_wrapper.sh"]

USER jenkins

# Install plugins that are predefined in the base-plugins.txt file
COPY files/base-plugins.txt /usr/share/jenkins/base-plugins.txt
RUN cat /usr/share/jenkins/base-plugins.txt | xargs /usr/local/bin/install-plugins.sh
