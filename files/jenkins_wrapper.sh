#!/bin/bash

JENKINS_VER=2.89.3
RELEASE=2

# This enables you to directly tie versions of plugins to a specific version of Jenkins
# The base dockerfile installs plugins, but it won't override plugins that are pinned,
# so we will copy the plugin directory on each new version of Jenkins
# More information - https://wiki.jenkins.io/display/JENKINS/Pinned+Plugins
if [ "$(cat /var/jenkins_home/jenkins.docker.image.version)" != "${JENKINS_VER}-${RELEASE}" ] ; then
  echo "Updating plugins..."
  cp -var /usr/share/jenkins/ref/plugins/* /var/jenkins_home/plugins
  echo "${JENKINS_VER}-${RELEASE}" > /var/jenkins_home/jenkins.docker.image.version
fi

# https://github.com/jenkinsci/docker/blob/master/jenkins.sh
exec /usr/local/bin/jenkins.sh "$@"
