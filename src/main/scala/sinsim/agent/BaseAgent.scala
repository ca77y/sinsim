package sinsim.agent

import sinsim.common.{JadeAgentDictionary, JadeMessaging}
import jade.core.Agent
import jade.util.Logger

trait BaseAgent extends Agent with JadeMessaging with JadeAgentDictionary {
  protected[this] val logger: Logger
  val agent = this
}
