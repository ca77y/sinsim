package sinsim.common
import jade.core.{ProfileImpl, Runtime}
import jade.util.leap.Properties

trait BaseExperiment {
  private[this] final val MainConfigFile = "/jade-main.properties"
  private[this] final val AgentConfigFile = "/jade-agent.properties"

  def agentName: String

  def readProperties(filename: String) = {
    val prop = new Properties()
    prop.load(getClass.getResourceAsStream(filename))
    Properties.toLeapProperties(prop)
  }

  def readProfile(filename: String) = {
    val prop = readProperties(filename)
    new ProfileImpl(prop)
  }

  def run(): Unit = {
    Runtime.instance().setCloseVM(true)
    val mainProfile = readProfile(MainConfigFile)
    val agentProfile = readProfile(AgentConfigFile)
    val mainContainer = Runtime.instance().createMainContainer(mainProfile)
    val agentContainer = Runtime.instance().createAgentContainer(agentProfile)
    mainContainer.start()
    agentContainer.start()

    val statsAgent = agentContainer.createNewAgent("Stats", "sinsim.agent.StatsAgent", Array())
    statsAgent.start()
    val matingAgent = agentContainer.createNewAgent("Mating", "sinsim.agent.MatingAgent", Array())
    Thread.sleep(3000)
    for (i <- Range(0, EnvironmentConstants.NUMBER_OF_AGENTS) ) {
      val agent = agentContainer.createNewAgent("Invest" + i, agentName, Array())
      agent.start()
    }
    matingAgent.start()
  }
}
