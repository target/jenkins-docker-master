import jenkins.model.*

import java.util.*
import java.lang.reflect.*
import net.sf.json.*
import net.sf.json.groovy.*
import java.util.logging.ConsoleHandler
import java.util.logging.LogManager

import jenkins.metrics.api.MetricsAccessKey
import jenkins.metrics.impl.graphite.GraphiteServer

def env = System.getenv()
def logger = LogManager.getLogManager().getLogger("hudson.WebAppMain")

def instance = Jenkins.get()

logger.info("Setting up Graphite Metrics Reporting")

graphite = new jenkins.metrics.impl.graphite.GraphiteServer(env['GRAPHITE_HOST'],2003,env['TEAM']+"."+env['NAME'])
List<GraphiteServer> graphiteServers = new ArrayList<GraphiteServer>()
graphiteServers.add(graphite)
GraphiteServer.DescriptorImpl graphiteImpl = new GraphiteServer.DescriptorImpl()
graphiteImpl.setServers(graphiteServers)
instance.save()
