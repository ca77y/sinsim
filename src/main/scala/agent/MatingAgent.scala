package agent

import behaviour.{MatingBehaviour, AgentTypes}
import jade.core.Agent
import jade.domain.{FIPAException, DFService}
import jade.domain.FIPAAgentManagement.{ServiceDescription, DFAgentDescription}
import jade.util.Logger

class MatingAgent extends Agent {
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
    addBehaviour(new MatingBehaviour())
  }
}
