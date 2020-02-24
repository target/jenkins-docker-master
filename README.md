# jenkins-docker-master

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)
[![release](https://img.shields.io/github/release/target/jenkins-docker-master.svg)](https://github.com/target/jenkins-docker-master/releases/latest)
[![docker](https://img.shields.io/docker/automated/target/jenkins-docker-master.svg)](https://hub.docker.com/r/target/jenkins-docker-master)

## How to use this image

### Modify the image to company specific settings

  ```dockerfile
  FROM target/jenkins-docker-master:2.176.1
  COPY scripts-directory /usr/share/jenkins/ref/init.groovy.d/script.groovy
  ```

### Create the Docker Swarm service

  Example docker service command:

  ```bash
  docker service create --name <name> --mount type=bind,src=/path/to/source,dst=/var/jenkins_home
  -e JENKINS_URL=https://<jenkins url> -e JENKINS_SLAVE_AGENT_PORT=<jnlp port>
  --network <network name> --publish <jnlp port> --restart-condition on-failure
  target/jenkins-docker-master:2.176.1
  ```

  Example [gelvedere](https://github.com/target/gelvedere) command:

  ```bash
  gelvedere --user-config /jenkins/user-configs/test.json --admin-config /jenkins/admin-configs/test.json --domain acme.com
  ```

### Configuration

The majority of base configuration should be done with groovy scripts when the image is created or started for the first time and as such, we have provided example groovy scripts [here](https://github.com/target/jenkins-docker-master/blob/master/examples).

#### Authentication and Authorization

The images take advantage of a few plugins to handle auth:

- [github-oauth](https://plugins.jenkins.io/github-oauth)
- [matrix-auth](https://plugins.jenkins.io/matrix-auth)
- [role-strategy](https://plugins.jenkins.io/role-strategy)

An example setup script can be found [here](https://github.com/target/jenkins-docker-master/blob/master/examples/files/setup_security.groovy)

#### Reporting

  Various ways exist to report metrics from Jenkins, so we won't go into deep details of available options. However, we have had good luck with using the [jmx2graphite jar](https://github.com/logzio/jmx2graphite) in conjunction with [metrics](https://plugins.jenkins.io/metrics) and [metrics-graphite](https://plugins.jenkins.io/metrics-graphite) plugins.

  An example reporting script can be found [here](https://github.com/target/jenkins-docker-master/blob/master/examples/files/setup_reporting.groovy)

#### Environment variables

The following environment variables can be used to set up Jenkins:

| Variable                 | Description | Example |
| ------------------------ | ----------- | ------- |
| JENKINS_SLAVE_AGENT_PORT | The TCP port for the slave agent to use. Must be unique to the cluster. | `5001` |
| JAVA_OPTS | Additional java options for running. | `--server -XX:+UseG1GC` |

## Image Variants

The `jenkins-docker-master` images come in a couple of flavors, each designed for a specific use case. All of the images extend the official [Jenkins LTS images](https://hub.docker.com/r/jenkins/jenkins)
and as such, many of the options prescribed there apply to this image as well.

### `jenkins-docker-master:2.204.2-1`

This is the de facto image. It is based off of `jenkins/jenkins:2.204.2` and includes a few modifications. It was created with the intention to be extended by using groovy scripts to setup ACLs using various plugins and as such, we have provided example groovy scripts [here](https://github.com/target/jenkins-docker-master/blob/master/examples).

### `jenkins-docker-master:debug-2.204.2-1`

This image is based off of the `jenkins-docker-master:2.204.2-1` image. The noticeable difference is in relation to increasing log verbosity for troubleshooting.
