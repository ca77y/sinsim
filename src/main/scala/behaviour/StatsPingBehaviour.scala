package behaviour

import jade.core.{AID, Agent}
import jade.core.behaviours.TickerBehaviour
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.lang.acl.ACLMessage
import jade.util.Logger

class StatsPingBehaviour(agent: Agent, tick: Long) extends TickerBehaviour(agent, tick) {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  def findAgents(): Seq[AID] = {
    val template = new DFAgentDescription()
    val sd = new ServiceDescription()
    sd.setType(AgentTypes.ROUSSEAU)
    template.addServices(sd);
    try {
      val services = DFService.search(myAgent, template);
      return services.map(ad => ad.getName)
    }
    catch {
      case fe: FIPAException =>
        logger.log(Logger.SEVERE, "registering service failed", fe)
    }
    Seq()
  }

  def askForStats(agent: AID) = {
    val msg = new ACLMessage(ACLMessage.REQUEST)
    msg.addReceiver(agent)
    msg.addReplyTo(myAgent.getAID)
    msg.setConversationId(ConversationTypes.STATS)
    myAgent.send(msg)
  }

  override def onTick(): Unit = {
    val agents = findAgents()
    for (agent <- agents) {
      askForStats(agent)
    }
  }
}
