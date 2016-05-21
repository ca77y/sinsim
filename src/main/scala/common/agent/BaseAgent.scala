package common.agent

import jade.core.Agent
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.util.Logger

trait BaseAgent extends Agent {
  protected[this] val logger: Logger

  def registerInDF(agentType: String) = {
    val dfd = new DFAgentDescription()
    dfd.setName(getAID)
    val sd = new ServiceDescription()
    sd.setType(agentType)
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
}
