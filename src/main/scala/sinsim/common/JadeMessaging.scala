package sinsim.common

import java.io.StringReader

import sinsim.common.ConversationTypes.ConversationType
import jade.core.{AID, Agent}
import jade.lang.acl.{ACLMessage, StringACLCodec}
import jade.util.Logger

trait JadeMessaging {
  protected[this] def logger: Logger
  def agent: Agent

  def sendMessage(conversationType: ConversationType, receiver: AID, messageType: Int, content: String = ""): Unit = {
    val msgToAgent = new ACLMessage(messageType)
    msgToAgent.addReceiver(receiver)
    msgToAgent.addReplyTo(agent.getAID)
    msgToAgent.setContent(content)
    msgToAgent.setConversationId(conversationType)
    agent.send(msgToAgent)
    logger.finest(s"sending message from ${agent.getName} to $receiver with type:$conversationType data:$content")
  }

  def str2AID(aid: String): AID = new StringACLCodec(new StringReader(aid), null).decodeAID()
}
