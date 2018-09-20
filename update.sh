#!/bin/bash
# This is a simple script to update the version of jenkins being used

JENKINS_VER=2.138.1
RELEASE=1

for fname in Dockerfile Dockerfile.debug files/jenkins_wrapper.sh examples/Dockerfile examples/files/jenkins_wrapper.sh
do
    sed -i '' -E "s/JENKINS_VER=(.*)/JENKINS_VER=${JENKINS_VER}/g; s/RELEASE=(.*)/RELEASE=${RELEASE}/g" $fname
done

sed -i '' -E "s/(jenkins-docker-master:)[0-9]{1,2}\.[0-9]{1,3}\.[0-9]{1,2}-[0-9]{1,2}/jenkins-docker-master:${JENKINS_VER}-${RELEASE}/g" README.md
sed -i '' -E "s/debug-[0-9]{1,2}\.[0-9]{1,3}\.[0-9]{1,2}-[0-9]{1,2}/debug-${JENKINS_VER}-${RELEASE}/g" README.md
sed -i '' -E "s/jenkins\/jenkins:[0-9]{1,2}\.[0-9]{1,3}\.[0-9]{1,2}/jenkins\/jenkins:${JENKINS_VER}/g" README.md
