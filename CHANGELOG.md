# changelog

## 2.164.1-1
* Bump LTS

## 2.121.2-1
* Bump LTS

## 2.121.1-1
* Bump LTS

## 2.107.3-1

* Bump LTS

## 2.107.2-1

* Bump LTS

## 2.107.1-1

* Bump LTS

## 2.89.4-1

* Bump LTS

## 2.89.3-1

* Bump LTS
* Fix Dockerfile to use correct path for [tini](https://github.com/krallin/tini)

## 2.89.2-2

* Enabling CSRF Protection in the setup_security.groovy script.
* Limiting the agent protocols to JNLP4 and CLI2 in the setup_security.groovy script per security and deprecation warnings.

## 2.89.2-1

* Bump LTS for security fixes
* Fix typos

## 2.89.1-1

* Bump LTS

## 2.73.3-2

* Order `MAINTAINERS` alphabetically
* Use environment variables in `jenkins_wrapper.sh` instead of having to sed in Dockerfile
* Format `README.md` to more clearly have Jenkins versions displayed
* Update formatting on `examples/files/README.md`
* Format `Dockerfile` to get rid `Empty continuation line found` errors
* Remove `chmod` from `Dockerfile` in favor of committing files to Github with correct permissions

## 2.73.3-1

* Bump LTS

## 2.73.2-2

* Fix Dockerfile argument scoping issues for `jenkins.install.UpgradeWizard.state` and `jenkins.install.InstallUtil.lastExecVersion`

## 2.73.2-1

* Bump Jenkins LTS to 2.73.2
