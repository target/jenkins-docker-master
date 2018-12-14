#!/bin/bash

JENKINS_VER=2.150.1
RELEASE=1

# This enables you to directly tie versions of plugins to a specific version of Jenkins
# The base dockerfile installs plugins, but it won't override plugins that are pinned,
# so we will copy the plugin directory on each new version of Jenkins
# More information - https://wiki.jenkins.io/display/JENKINS/Pinned+Plugins
if [ "$(cat /var/jenkins_home/jenkins.docker.image.version)" != "${JENKINS_VER}-${RELEASE}" ] ; then
  echo "Updating plugins..."
  cp -var /usr/share/jenkins/ref/plugins/* /var/jenkins_home/plugins
  echo "${JENKINS_VER}-${RELEASE}" > /var/jenkins_home/jenkins.docker.image.version
fi

# setup java options
JAVA_OPTS="${JAVA_OPTS} -server -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=20 -XX:G1HeapRegionSize=8m -XX:MetaspaceSize=500M -Djava.awt.headless=true -Djenkins.model.Jenkins.logStartupPerformance=true -Dhudson.InitReactorRunner.concurrency=64 -Dhudson.slaves.NodeProvisioner.MARGIN=50 -Dhudson.slaves.NodeProvisioner.MARGIN0=0.85"
export JAVA_OPTS

# https://github.com/jenkinsci/docker/blob/master/jenkins.sh
exec /usr/local/bin/jenkins.sh "$@"
