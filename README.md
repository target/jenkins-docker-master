# jenkins-docker-master

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)

## Supported tags and respective `Dockerfile` links

- [`latest`, `2.73.3-1` (*Dockerfile*)](https://github.com/target/jenkins-docker-master/blob/master/Dockerfile)
- [`debug-latest`, `debug-2.73.3-1` (*Dockerfile.debug*)](https://github.com/target/jenkins-docker-master/blob/master/Dockerfile.debug)

## Quick reference

- **Where to get help**:
  [The Jenkins users google group](https://groups.google.com/forum/?nomobile=true#!forum/jenkinsci-users) or [Stack Overflow](https://stackoverflow.com/search?tab=newest&q=jenkins)

- **Where to file issues**:
  [https://github.com/target/jenkins-docker-master/issues](https://github.com/target/jenkins-docker-master/issues)

- **Maintained by**:
  [The JAYS Maintainers](https://github.com/target/jenkins-docker-master/blob/master/MAINTAINERS)

## How to use this image

### Modify the image to company specific settings

  ```dockerfile
  FROM target/jenkins-docker-master:latest
  COPY scripts-directory /usr/share/jenkins/ref/init.groovy.d/script.groovy
  ```

### Create the Docker Swarm service

  Example docker service command:

  ```console
  $ docker service create --name <name> --mount type=bind,src=/path/to/source,dst=/var/jenkins_home
  -e JENKINS_URL=https://<jenkins url> -e JENKINS_SLAVE_AGENT_PORT=<jnlp port>
  --network <network name> --publish <jnlp port> --restart-condition on-failure
  target/jenkins-docker-master:<version>
  ```

  Example [gelvedere](https://github.com/target/gelvedere) command:

  ```console
  gelvedere --user-config /jenkins/user-configs/test.json --admin-config /jenkins/admin-configs/test.json --domain acme.com
  ```

### Configuration

The majority of base configuration should be done with groovy scripts when the image is created or started for the first time and as such, we have provided example groovy scripts [here](https://github.com/target/jenkins-docker-master/blob/master/examples).

#### Authentication and Authorization

The images take advantage of a few plugins to handle auth:

- [github-oauth](https://plugins.jenkins.io/github-oauth)
- [matrix-auth](https://plugins.jenkins.io/matrix-auth)
- [role-strategy](https://plugins.jenkins.io/role-strategy)

An example setup script can be found [here](https://github.com/target/jenkins-docker-master/blob/master/examples/setup_security.groovy)

#### Reporting

  Various ways exist to report metrics from Jenkins, so we won't go into deep details of available options. However, we have had good luck with using the [jmx2graphite jar](https://github.com/logzio/jmx2graphite) in conjunction with [metrics](https://plugins.jenkins.io/metrics) and [metrics-graphite](https://plugins.jenkins.io/metrics-graphite) plugins.

  An example reporting script can be found [here](https://github.com/target/jenkins-docker-master/blob/master/examples/setup_reporting.groovy)

#### Environment variables

The following environment variables can be used to set up Jenkins:

| Variable                 | Description | Example |
| ------------------------ | ----------- | ------- |
| JENKINS_SLAVE_AGENT_PORT | The TCP port for the slave agent to use. Must be unique to the cluster. | `5001` |
| JAVA_OPTS | Additional java options for running. | `--server -XX:+UseG1GC` |

## Image Variants

The `jenkins-docker-master` images come in a couple of flavors, each designed for a specific use case. All of the images extend the official [Jenkins LTS images](https://hub.docker.com/r/jenkins/jenkins)
and as such, many of the options prescribed there apply to this image as well.

### `jenkins-docker-master:<lts version>-<image version>`

This is the de facto image. It is based off of `jenkins/jenkins<version>` and includes a few modifications. It was created with the intention to be extended by using groovy scripts to setup ACLs using various plugins and as such, we have provided example groovy scripts [here](https://github.com/target/jenkins-docker-master/blob/master/examples).

### `jenkins-docker-master:debug-<lts version>-<image version>`

This image is based off of the `jenkins-docker-master:<version>` image. The noticeable difference is in relation to increasing log verbosity for troubleshooting.
