#!/bin/bash
# This is a simple script to update the version of jenkins being used

JENKINS_VER=2.73.1
JENKINS_REL=1

sed -i '' -E "s/\`(debug-)?latest\`, \`(debug-)?[0-9]{1,2}\.[0-9]{1,3}\.[0-9]{1,2}-[0-9]{1,2}\`/\`\1latest\`, \`\1${JENKINS_VER}-${JENKINS_REL}\`/g" README.md
sed -i '' -E "s/JENKINS_VER=(.*)/JENKINS_VER=${JENKINS_VER}/g; s/JENKINS_REL=(.*)/JENKINS_REL=${JENKINS_REL}/g" Dockerfile
sed -i '' -E "s/JENKINS_VER=(.*)/JENKINS_VER=${JENKINS_VER}/g; s/JENKINS_REL=(.*)/JENKINS_REL=${JENKINS_REL}/g" Dockerfile.debug
sed -i '' -E "s/JENKINS_VER=(.*)/JENKINS_VER=${JENKINS_VER}/g; s/JENKINS_REL=(.*)/JENKINS_REL=${JENKINS_REL}/g" examples/Dockerfile
