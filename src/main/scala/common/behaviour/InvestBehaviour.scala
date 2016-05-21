package common.behaviour

import common.ConversationTypes
import jade.core.AID
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.{ACLMessage, MessageTemplate}
import jade.util.Logger
import rousseau.RousseauAgent

class InvestBehaviour(agent: RousseauAgent, matingAgent: AID) extends CyclicBehaviour {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  def registerAsInvesting(msg: ACLMessage): Unit = {
    val content = msg.getContent
    val aid = new AID(content, AID.ISLOCALNAME)
    val msgToAgent = new ACLMessage(ACLMessage.PROPOSE)
    msgToAgent.addReceiver(aid)
    msgToAgent.addReplyTo(myAgent.getAID)
    msgToAgent.setContent(agent.moneyToInvest().toString)
    msgToAgent.setConversationId(ConversationTypes.INVESTING)
    myAgent.send(msgToAgent)
  }

  def handleMating(msg: ACLMessage): Unit = {
    msg.getPerformative match {
      case ACLMessage.ACCEPT_PROPOSAL => registerAsInvesting(msg)
      case ACLMessage.REJECT_PROPOSAL => ;
    }
  }

  def invest(msg: ACLMessage): Unit = {
    val senderMoney = Integer.valueOf(msg.getContent)
    val share = agent.profitAndShare(senderMoney)
    val msgToAgent = new ACLMessage(ACLMessage.INFORM)
    msgToAgent.addReceiver(msg.getSender)
    msgToAgent.addReplyTo(myAgent.getAID)
    msgToAgent.setContent(share.toString)
    msgToAgent.setConversationId(ConversationTypes.INVESTING)
    myAgent.send(msgToAgent)
  }

  def earn(msg: ACLMessage): Unit = {
    val money = msg.getContent.toInt
    agent.earn(money)
    startInvesting()
  }

  def startInvesting(): Unit = {
    val msgToAgent = new ACLMessage(ACLMessage.PROPOSE)
    msgToAgent.addReceiver(matingAgent)
    msgToAgent.addReplyTo(myAgent.getAID)
    msgToAgent.setConversationId(ConversationTypes.MATING)
    myAgent.send(msgToAgent)
  }

  def handleInvest(msg: ACLMessage): Unit = {
    msg.getPerformative match {
      case ACLMessage.PROPOSE => invest(msg)
      case ACLMessage.INFORM => earn(msg)
      case _ => logger.severe(s"Unknown message $msg")
    }
  }

  def handleMessage(msg: ACLMessage): Unit = {
    msg.getConversationId match {
      case ConversationTypes.MATING => handleMating(msg)
      case ConversationTypes.INVESTING => handleInvest(msg)
      case _ => logger.severe(s"Unknown message $msg")
    }
  }

  override def action(): Unit = {
    val mt = MessageTemplate.or(
      MessageTemplate.MatchConversationId(ConversationTypes.MATING),
      MessageTemplate.MatchConversationId(ConversationTypes.INVESTING)
    )
    val msg = Option(myAgent.receive(mt))
    msg match {
      case Some(m) => handleMessage(m)
      case None => block()
    }
  }
}
