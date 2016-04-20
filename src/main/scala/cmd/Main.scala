package cmd

import java.util.Properties

import jade.core.{ProfileImpl, Runtime}
import jade.util.leap.{Properties => LeapProperties}

object Main {
  private[this] final val MainConfigFile = "/jade-main.properties"
  private[this] final val AgentConfigFile = "/jade-agent.properties"

  def readProfile(filename: String) = {
    val prop = new Properties()
    prop.load(getClass.getResourceAsStream(filename))
    val lp = LeapProperties.toLeapProperties(prop)
    new ProfileImpl(lp)
  }

  def main( args:Array[String] ):Unit = {
    val mainProfile = readProfile(MainConfigFile)
    val agentProfile = readProfile(AgentConfigFile)
    val mainContainer = Runtime.instance().createMainContainer(mainProfile)
    val agentContainer = Runtime.instance().createAgentContainer(agentProfile)
    for (i <- Range(0, 10) ) {
      agentContainer.createNewAgent("test" + i, "agent.RousseauAgent", Array())
    }

  }
}
