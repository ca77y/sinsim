package rousseau

import behaviour._
import common.behaviour.{InvestBehaviour, StatsSenderBehaviour, DeathBehaviour}
import common.{ConversationTypes, AgentTypes}
import jade.core.{AID, Agent}
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.lang.acl.ACLMessage
import jade.util.Logger

import scala.util.Random

class RousseauAgent extends Agent {
  private[this] val logger = Logger.getJADELogger(getClass.getName)
  var money = 100
  val honesty = Random.nextInt(100)

  private def generateProfit(investMoney: Int, senderMoney: Int) = {
    val maxProfit = math.floor((investMoney + senderMoney) * 0.5).toInt
    val profit = Random.nextInt(maxProfit) - math.floor(maxProfit * 0.5).toInt + 1
    profit
  }

  def profitAndShare(senderMoney: Int): Int = {
    val investMoney = moneyToInvest()
    val profit = generateProfit(investMoney, senderMoney)
    if (profit == 0) {
      return 0
    }
    val split = if(senderMoney == 0) {
      0
    } else if (investMoney == 0) {
      1
    } else {
      senderMoney.toDouble / (senderMoney + investMoney).toDouble
    }
    val share = math.floor(senderMoney * split).toInt
    earn(profit - share)
    share
  }

  def earn(profit: Int) = {
    money += profit
  }

  def consume(): Unit = {
    money -= 50
    if (money < 0) {
      money = 0
    }
  }

  def moneyToInvest() = {
    val invest = Random.nextInt(10) + 1
    if (money - invest < 0) {
      val balancedInvest = invest - money
      money = 0
      balancedInvest
    } else {
      invest
    }
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
      case Some(a) =>
        addBehaviour(new InvestBehaviour(this, a))
        addBehaviour(new StatsSenderBehaviour(this))
        addBehaviour(new DeathBehaviour(this, 50))
        startInvesting(a)
      case None => logger.severe("Mating agent missing")
    }
  }
}
