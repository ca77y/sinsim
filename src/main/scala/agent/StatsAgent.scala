package agent

import behaviour.{StatsGatherBehaviour, AgentTypes, StatsPingBehaviour}
import jade.core.Agent
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.util.Logger

class StatsAgent extends Agent {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  def registerInDF() = {
    val dfd = new DFAgentDescription()
    dfd.setName(getAID)
    val sd = new ServiceDescription()
    sd.setType(AgentTypes.MATING)
    sd.setName(getName)
    dfd.addServices(sd)
    try {
      DFService.register(this, dfd)
    }
    catch {
      case fe: FIPAException =>
        logger.log(Logger.SEVERE, "registering service failed", fe)
    }
  }

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF()
    addBehaviour(new StatsGatherBehaviour())
    addBehaviour(new StatsPingBehaviour(this, 5000))
  }
}
