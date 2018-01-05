import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*
import org.jenkinsci.plugins.GithubSecurityRealm
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*
import net.sf.json.*
import net.sf.json.groovy.*
import java.util.logging.ConsoleHandler
import java.util.logging.LogManager

import hudson.model.Hudson.CloudList;
import hudson.slaves.Cloud;

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

def env = System.getenv()
def logger = LogManager.getLogManager().getLogger("hudson.WebAppMain")

def instance = Jenkins.getInstance()

// Set executors to 0
instance.setNumExecutors(0)

// Set quiet period - https://jenkins.io/blog/2010/08/11/quiet-period-feature/
instance.setQuietPeriod(5)

// Set slave agent port - http://javadoc.jenkins-ci.org/jenkins/model/Jenkins.html#setSlaveAgentPort-int-
int port = env['JENKINS_SLAVE_AGENT_PORT'].toInteger()
instance.setSlaveAgentPort(port)

// Set agent protocol list to JNLP4 and CLI2 by default. - http://javadoc.jenkins.io/jenkins/model/Jenkins.html#getAgentProtocols--
Set<String> agentProtocolsList = ['JNLP4-connect', 'Ping', 'CLI2-connect']
if(!instance.getAgentProtocols().equals(agentProtocolsList)) {
    instance.setAgentProtocols(agentProtocolsList)
    logger.info("Agent Protocols have changed.  Setting: ${agentProtocolsList}")
    instance.save()
} else {
    logger.info("Nothing changed.  Agent Protocols already configured: ${instance.getAgentProtocols()}")
}

// Set URL location - http://javadoc.jenkins-ci.org/jenkins/model/JenkinsLocationConfiguration.html#setUrl-java.lang.String-
loc = JenkinsLocationConfiguration.get()
loc.setUrl(env['JENKINS_URL'])

// Create admin user - http://javadoc.jenkins-ci.org/hudson/model/User.html
jknadm = hudson.model.User.get('jenkins-admin')
jknadm.setFullName('Jenkins Admin User')
jknadm.setDescription('This account is used by the Jenkins management team to administer this instance. Please do not delete it.')

String emailAddress = env['JENKINS_ADMIN_EMAIL']

email = jknadm.getProperty(hudson.tasks.Mailer.UserProperty)
if (email == null) {
  logger.info("Adding email to admin user")
  email = new hudson.tasks.Mailer.UserProperty(emailAddress)
  jknadm.addProperty(email)
} else {
  if (!email.	getAddress().equals(emailAddress)) {
    logger.warning("Resetting email to admin user")
    email = new hudson.tasks.Mailer.UserProperty(emailAddress)
    jknadm.addProperty(email)
  }
}

// https://github.com/jenkinsci/ssh-cli-auth-module/blob/master/src/main/java/org/jenkinsci/main/modules/cli/auth/ssh/UserPropertyImpl.java
keys = jknadm.getProperty(org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl)
if (keys == null) {
  logger.info("Adding new admin ssh key")
  keys = new org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl(env['ADMIN_SSH_PUBKEY'])
  jknadm.addProperty(keys)
}

jknadm.save()

logger.info("Setting up GHE Auth")

String gheKey = env['GHE_KEY']
String gheSecret = env['GHE_SECRET']
String gheUrl = env['GHE_URL']
String gheApiUrl = env['GHE_API_URL']

def githubRealm = new GithubSecurityRealm(
  gheUrl,
  gheApiUrl,
  gheKey,
  gheSecret,
  "read:org,user:email"
)
instance.setSecurityRealm(githubRealm)
instance.save()

def authz
def authStrategy = instance.getAuthorizationStrategy()

if(authStrategy instanceof RoleBasedAuthorizationStrategy){
  logger.info("Reusing Role Based Authorization Strategy")
  authz = (RoleBasedAuthorizationStrategy) authStrategy

} else {
  logger.info("Setting Role Based Authorization Strategy")
  authz = new RoleBasedAuthorizationStrategy()
  instance.setAuthorizationStrategy(authz)
  instance.save()
}

// Define base roles
logger.info("Defining base roles")

// Make constructors available
Constructor[] constrs = Role.class.getConstructors();
for (Constructor<?> c : constrs) {
  c.setAccessible(true);
}

// Make the method assignRole accessible
Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
assignRoleMethod.setAccessible(true);

Method getRoleMapMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("getRoleMap", String.class);
getRoleMapMethod.setAccessible(true);

roleMap = authz.getRoleMap(RoleBasedAuthorizationStrategy.GLOBAL)

String gheAdmin = env['GHE_ADMIN']

// First:  Define a datastructure that represents the roles+permissions we want to have at *install* time
// Members defined in this list here will *always* get added back during a restart/upgrade
permissionMap = [
  admin:[
    members: ["jenkins-admin",gheAdmin],
    permissions: ["hudson.model.Hudson.Administer"]
  ],
  developer:[
    members: [],
    permissions: ["hudson.model.View.Delete",
      "hudson.model.Item.Workspace",
      "hudson.model.Item.Configure",
      "hudson.model.View.Create",
      "hudson.model.View.Read",
      "hudson.model.Run.Delete",
      "hudson.model.Item.Discover",
      "hudson.model.Hudson.Read",
      "hudson.model.Item.Delete",
      "hudson.model.View.Configure",
      "hudson.model.Item.ViewStatus",
      "hudson.model.Item.Move",
      "hudson.model.Item.Read",
      "hudson.model.Item.Build",
      "hudson.model.Item.Create",
      "hudson.model.Item.Cancel",
      "hudson.model.Run.Update"]
  ],
  authenticated: [
    members: ["authenticated"],
    permissions: ["hudson.model.Item.Workspace",
      "hudson.model.View.Read",
      "hudson.model.Item.Discover",
      "hudson.model.Hudson.Read",
      "hudson.model.Item.ViewStatus",
      "hudson.model.Item.Read"]
  ],
  anonymous: [
    members: ["anonymous"],
    permissions: ["hudson.model.Item.Discover"]
  ]
]

logger.info("Assigning default permissions")

// Loop through the permisisons we just defined
permissionMap.each { roleName,rset ->
  Set<Permission> permissions = new HashSet<Permission>()
  rset.permissions.each { p ->
    permission = Permission.fromId(p)
    // If the permission we defined as a string does not exist (ie- its from
    // a plugin we didnt install) then just skip it
    if (permission != null) {
      permissions.add(permission)
    }
  }
  // Create a *new* Role object with the desired name and permissions
  Role role = new Role(roleName,permissions)
  // This will only add the role if the role is not already added. So local modifications will trump
  // this line. See https://github.com/jenkinsci/role-strategy-plugin/blob/master/src/main/java/com/michelin/cio/hudson/plugins/rolestrategy/RoleMap.java#L155-L159
  authz.addRole(RoleBasedAuthorizationStrategy.GLOBAL,role)
  // Loop through the members, adding them to the role. This will happen every time, so you cannot remove the
  // members defined here
  rset.members.each { member ->
    authz.assignRole(RoleBasedAuthorizationStrategy.GLOBAL,role,member)
  }

  // Inject additonal members based on environment variables. This happens every time, but the env's might change
  // from run to run
  if (env['JENKINS_ACL_MEMBERS_'+roleName] != null) {
    env['JENKINS_ACL_MEMBERS_'+roleName].tokenize(',').each { member ->
      authz.assignRole(RoleBasedAuthorizationStrategy.GLOBAL,role,member)
    }
  }
}

instance.save()
