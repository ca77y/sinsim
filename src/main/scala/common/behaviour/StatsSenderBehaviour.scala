package common.behaviour

import common.ConversationTypes
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.{ACLMessage, MessageTemplate}
import jade.util.Logger
import rousseau.RousseauAgent

class StatsSenderBehaviour(agent: RousseauAgent) extends CyclicBehaviour {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  def sendStats(msg: ACLMessage): Unit = {
    val msgToSender = new ACLMessage(ACLMessage.INFORM)
    msgToSender.addReceiver(msg.getSender)
    msgToSender.addReplyTo(myAgent.getAID)
    msgToSender.setConversationId(ConversationTypes.STATS)
    msgToSender.setContent(s"${agent.getLocalName}:${agent.money}")
    myAgent.send(msgToSender)
  }

  def handleMessage(msg: ACLMessage): Unit = {
    msg.getPerformative match {
      case ACLMessage.REQUEST => sendStats(msg)
      case _ => logger.severe(s"Unknown message $msg")
    }
  }

  override def action(): Unit = {
    val mt = MessageTemplate.MatchConversationId(ConversationTypes.STATS)
    val msg = Option(myAgent.receive(mt))
    msg match {
      case Some(m) => handleMessage(m)
      case None => block()
    }
  }
}
