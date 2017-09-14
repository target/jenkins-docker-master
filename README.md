# jenkins-docker-master

## About this repo
This is the Git repo of the Jenkins Dockerfile used in the Jenkins at Your Service (JAYS) architecture.

JAYS is a platform for providing multiple Jenkins instances in a Docker Swarm cluster.

More information about JAYS coming soon.

## Usage

This image extends the official [Jenkins LTS images](https://hub.docker.com/r/jenkins/jenkins)
and as such, many of the options prescribed there apply to this image as well.

The base image is intended to be extended by using groovy scripts to setup ACLs using the `github-oauth` plugin. We have provided example groovy scripts in the `examples` directory of this Github repo.

The following environment variables can be used to set up Jenkins:

| Variable                          | Description | Example |
| --------------------------------- | ----------- | ------- |
| JENKINS_SLAVE_AGENT_PORT          | Specify the TCP port for the slave agent to use. Must be unique to the cluster | `5001` |
| JAVA_OPTS                         | Specify any additional java options for running. | `-Djava.util.logging.config.file=/var/jenkins_home/log.properties` |

## Supported tags and respective `Dockerfile` links

 * `latest`, `2.60.3-1`
 * `debug-latest`, `debug-2.60.3-1`
