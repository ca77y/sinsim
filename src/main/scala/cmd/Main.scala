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
    Runtime.instance().setCloseVM(true)
    val mainProfile = readProfile(MainConfigFile)
    val agentProfile = readProfile(AgentConfigFile)
    val mainContainer = Runtime.instance().createMainContainer(mainProfile)
    val agentContainer = Runtime.instance().createAgentContainer(agentProfile)
    mainContainer.start()
    agentContainer.start()
    val matingAgent = agentContainer.createNewAgent("Mating", "mating.MatingAgent", Array())
    matingAgent.start()
    for (i <- Range(0, 4) ) {
      val agent = agentContainer.createNewAgent("Rousseau" + i, "rousseau.RousseauAgent", Array())
      agent.start()
    }
    val statsAgent = agentContainer.createNewAgent("Stats", "stats.StatsAgent", Array())
    statsAgent.start()
  }
}
