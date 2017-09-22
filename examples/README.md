## setup_security
The `setup_security.groovy` script does the following:

  * Set [executors](https://support.cloudbees.com/hc/en-us/articles/216456477-What-is-a-Jenkins-Executor-and-how-can-I-best-utilize-my-executors) to 0
  * Set [quiet period](https://jenkins.io/blog/2010/08/11/quiet-period-feature/) to 5
  * Set [slave agent port](http://javadoc.jenkins-ci.org/jenkins/model/Jenkins.html#setSlaveAgentPort-int-) to a user provided environment variable
  * Set [jenkins url](http://javadoc.jenkins-ci.org/jenkins/model/JenkinsLocationConfiguration.html#setUrl-java.lang.String-) to a user provided environment variable
  * Create a admin user
    * Sets email address to user provided environment variable
    * Sets ssh keypair to user provided environment variable
  * Setup new GithubSecurityRealm for [github-oauth](https://plugins.jenkins.io/github-oauth)
  * Setup new RoleBasedAuthorizationStrategy for [role-strategy](https://plugins.jenkins.io/role-strategy)
    * Define admin, developer, authenticated, and anonymous user roles and permissions

### Usage

1. Extend the base image and include the script

  ```dockerfile
  FROM target/jenkins-docker-master:latest

  COPY setup_security.groovy   /usr/share/jenkins/ref/init.groovy.d/setup_security.groovy.override
  ```

  **Note**: The security script requires that the [git](https://plugins.jenkins.io/git), [github](https://plugins.jenkins.io/github), [github-oauth](https://plugins.jenkins.io/github-oauth), [matrix-auth](https://plugins.jenkins.io/matrix-auth), and [role-strategy](https://plugins.jenkins.io/role-strategy) plugins are installed

1. Create a GitHub OAuth App by following the instructions on [GitHub](https://developer.github.com/enterprise/2.10/v3/oauth/)
  * Example homepage url - `https://accounting.jenkins.company.com`
  * Example Authorization callback URL - `https://accounting.jenkins.company.com/securityRealm/finishLogin`
  * Take note of the Client ID and Client secret as they will be used later

1. Define company specific environment variables

| Variable                          | Description | Example |
| --------------------------------- | ----------- | ------- |
| GHE_KEY                           | TheGitHub OAuth Key | `abcdef0123456789abcd` |
| GHE_SECRET                        | TheGitHub OAuth Secret | `h5qosu6bmrgrl8dgwynfps4e1z2jnio4hid2u3gp` |
| JENKINS_ACL_MEMBERS_admin         | A GitHub Org*Team to be admins of this instance | `target*Admins` |
| JENKINS_ACL_MEMBERS_developer     | A GitHub Org*Team to be developers on this instance (optional) |  `jenkins*Contributors` |
| ADMIN_SSH_PUBKEY                  | An ssh public key for the admin team to manage this instance |  `ssh-rsa AAAAB3N....9CUz` |
| JENKINS_URL                       | Specify the public URL used to access this instance | `https://accoutning.jenkins.company.com/` |
| JENKINS_ADMIN_EMAIL               | Specify the email address associated with the owners of this instance | `owners@company.com` |
| GHE_ADMIN                         | The default GitHub Org*Team to be admins of this instance | `Jenkins*Admins`

## setup_reporting
The `setup_reporting.groovy` script does the following:

  * Sets up Graphite Metrics Reporting

### Usage

1. Extend the base image by including the reporting script and installing the [jmx2graphite jar](https://github.com/logzio/jmx2graphite)

  ```dockerfile
  FROM target/jenkins-docker-master:latest

  COPY setup_reporting.groovy   /usr/share/jenkins/ref/init.groovy.d/setup_reporting.groovy.override
  RUN curl -sL https://github.com/logzio/jmx2graphite/releases/download/v1.1.0/jmx2graphite-1.1.0-javaagent.jar > /usr/share/jenkins/jmx2graphite.jar
  ```

  **Note**: The reporting script requires that the [metrics](https://plugins.jenkins.io/metrics) and [metrics-graphite](https://plugins.jenkins.io/metrics-graphite) plugins are installed

1. Define company specific environment variables

| Variable                          | Description | Example |
| --------------------------------- | ----------- | ------- |
| GRAPHITE_HOST                     | Specify the URL or IP address where to send graphite metrics | `graphite.company.com` |
| NAME                           | Name of the Jenkins master | `accounting` |
| TEAM                           | Team using the Jenkins master | `stores` |


## Installing plugins
Installing additional plugins is as simple as creating a text file with the list of plugins and extending the base image to include it.

The examples/base-plugins.txt file is a list of our favorite plugins to install

### Usage

  ```dockerfile
  FROM target/jenkins-docker-master:latest

  COPY base-plugins.txt /usr/share/jenkins/base-plugins.txt
  RUN cat /usr/share/jenkins/base-plugins.txt | xargs /usr/local/bin/install-plugins.sh
  ```
