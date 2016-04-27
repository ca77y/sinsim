package agent

import behaviour.{ConversationTypes, InvestBehaviour, AgentTypes}
import jade.core.{AID, Agent}
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.lang.acl.ACLMessage
import jade.util.Logger

import scala.util.Random

class RousseauAgent extends Agent {
  private[this] val logger = Logger.getJADELogger(getClass.getName)
  var money = 100

  def generateProfit(totalAmount: Int) = {
    math.floor(totalAmount * 1.2).toInt
  }

  def moneyToShare(profit: Int) = {
    val share = profit / 2
    earn(share)
    share
  }

  def earn(profit: Int) = {
    money += profit
  }

  def moneyToInvest() = {
    Random.nextInt(10) + 1
  }

  def matingAgent(): Option[AID] = {
    val template = new DFAgentDescription()
    val sd = new ServiceDescription()
    sd.setType(AgentTypes.MATING)
    template.addServices(sd)
    try {
      val services = DFService.search(this, template)
      return services.headOption match {
        case Some(ad) => Option(ad.getName)
      }
    } catch {
      case ex: FIPAException =>
        logger.log(Logger.SEVERE, "searching for services failed", ex)
    }
    None
  }

  def registerInDF() = {
    val dfd = new DFAgentDescription()
    dfd.setName(getAID)
    val sd = new ServiceDescription()
    sd.setType(AgentTypes.ROUSSEAU)
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

  def startInvesting(ma: AID): Unit = {
    val msgToAgent = new ACLMessage(ACLMessage.PROPOSE)
    msgToAgent.addReceiver(ma)
    msgToAgent.addReplyTo(getAID)
    msgToAgent.setConversationId(ConversationTypes.MATING)
    send(msgToAgent)
  }

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF()
    val ma = matingAgent()
    ma match {
      case Some(a) => {
        addBehaviour(new InvestBehaviour(this, a))
        startInvesting(a)
      }
      case None => logger.severe("Mating agent missing")
    }
  }
}
