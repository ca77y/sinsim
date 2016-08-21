package sinsim.behaviour

import sinsim.common.ConversationTypes.ConversationType
import sinsim.common.{JadeAgentDictionary, JadeMessaging}
import jade.core.AID
import jade.core.behaviours.Behaviour
import jade.lang.acl.{ACLMessage, MessageTemplate}
import jade.util.Logger

import scala.language.implicitConversions

trait BaseBehaviour extends Behaviour with JadeMessaging with JadeAgentDictionary {
  protected[this] def logger: Logger

  def agent = getAgent

  def conversationType: ConversationType

  def sendMessage(receiver: AID, messageType: Int): Unit = {
    sendMessage(conversationType, receiver, messageType)
  }

  def sendMessage(receiver: AID, messageType: Int, content: String): Unit = {
    sendMessage(conversationType, receiver, messageType, content)
  }

  def handleMessage(msg: ACLMessage): Unit

  override def action(): Unit = {
    val mt = MessageTemplate.MatchConversationId(conversationType)
    val msg = Option(getAgent.receive(mt))
    msg match {
      case Some(m) => handleMessage(m)
      case None => block()
    }
  }
}
