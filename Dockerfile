ARG JENKINS_VER=2.73.3

FROM jenkins/jenkins:${JENKINS_VER}

USER root

ARG JENKINS_VER=2.73.3
ARG JENKINS_REL=1

COPY files/jenkins_wrapper.sh /usr/local/bin/jenkins_wrapper.sh

RUN echo -n ${JENKINS_VER} > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state && \
echo -n ${JENKINS_VER} > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion && \
# allow slave to master control - https://wiki.jenkins.io/display/JENKINS/Slave+To+Master+Access+Control
mkdir -p /usr/share/jenkins/ref/secrets/ && echo false > /usr/share/jenkins/ref/secrets/slave-to-master-security-kill-switch && \
# Create file for plugin versioning
echo ${JENKINS_VER}-${JENKINS_VER} > /usr/share/jenkins/ref/jenkins.docker.image.version && \
sed -i -e "s/##JENKINS_VER##/${JENKINS_VER}/g; s/##JENKINS_REL##/${JENKINS_REL}/g" /usr/local/bin/jenkins_wrapper.sh && \
chmod 0755 /usr/local/bin/jenkins_wrapper.sh

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins_wrapper.sh"]

USER jenkins

# Install plugins that are predefined in the base-plugins.txt file
COPY files/base-plugins.txt /usr/share/jenkins/base-plugins.txt
RUN cat /usr/share/jenkins/base-plugins.txt | xargs /usr/local/bin/install-plugins.sh
