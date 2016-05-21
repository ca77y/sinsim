package mating

import common.ConversationTypes
import jade.core.AID
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.{ACLMessage, MessageTemplate}
import jade.util.Logger

import scala.collection.mutable

class MatingBehaviour extends CyclicBehaviour {
  private[this] val logger = Logger.getJADELogger(getClass.getName)
  private val agents = mutable.Queue[AID]()

  def sendMateFound(sender: AID, agent: AID): Unit = {
    val msgToSender = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL)
    msgToSender.addReceiver(sender)
    msgToSender.addReplyTo(myAgent.getAID)
    msgToSender.setContent(agent.getLocalName)
    msgToSender.setConversationId(ConversationTypes.MATING)
    myAgent.send(msgToSender)

    val msgToAgent = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL)
    msgToAgent.addReceiver(agent)
    msgToAgent.addReplyTo(myAgent.getAID)
    msgToAgent.setContent(sender.getLocalName)
    msgToAgent.setConversationId(ConversationTypes.MATING)
    myAgent.send(msgToAgent)
  }

  def sendMateNotFound(sender: AID): Unit = {
    val message = new ACLMessage(ACLMessage.REJECT_PROPOSAL)
    message.addReceiver(sender)
    message.addReplyTo(myAgent.getAID)
    message.setConversationId(ConversationTypes.MATING)
    myAgent.send(message)
  }

  def findMate(msg: ACLMessage): Unit = {
    if(agents.nonEmpty) {
      val agent = agents.dequeue()
      sendMateFound(msg.getSender, agent)
    } else {
      agents += msg.getSender
      sendMateNotFound(msg.getSender)
    }
  }

  def handleMessage(msg: ACLMessage): Unit = {
    msg.getPerformative match {
      case ACLMessage.PROPOSE => findMate(msg)
      case _ => logger.severe(s"Unknown message $msg")
    }
  }

  override def action(): Unit = {
    val mt = MessageTemplate.MatchConversationId(ConversationTypes.MATING)
    val msg = Option(myAgent.receive(mt))
    msg match {
      case Some(m) => handleMessage(m)
      case None => block()
    }
  }
}
