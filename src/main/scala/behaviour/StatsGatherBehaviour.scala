package behaviour

import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.{ACLMessage, MessageTemplate}
import jade.util.Logger

class StatsGatherBehaviour extends CyclicBehaviour {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  def storeStats(msg: ACLMessage): Unit = {
    val content = msg.getContent
    logger.info(content)
  }

  def handleMessage(msg: ACLMessage): Unit = {
    msg.getPerformative match {
      case ACLMessage.INFORM => storeStats(msg)
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
