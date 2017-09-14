import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.SimpleFormatter
import java.util.logging.LogManager
import jenkins.model.Jenkins

// Log into the console
def WebAppMainLogger = LogManager.getLogManager().getLogger("hudson.WebAppMain")
WebAppMainLogger.addHandler (new ConsoleHandler())

// Log into a file
def RunLogger = LogManager.getLogManager().getLogger("hudson.model.Run")
def logsDir = new File(Jenkins.instance.rootDir, "logs")
if(!logsDir.exists()){logsDir.mkdirs()}
FileHandler handler = new FileHandler(logsDir.absolutePath+"/hudson.model.Run-%g.log", 1024 * 1024, 10, true);
handler.setFormatter(new SimpleFormatter());
RunLogger.addHandler(handler)
