package sinsim.agent

import jade.util.Logger
import sinsim.behaviour.StatsBehaviour
import sinsim.common.AgentTypes

class StatsAgent extends BaseAgent {
  protected val logger = Logger.getMyLogger(getClass.getCanonicalName)

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF(AgentTypes.STATS)

    addBehaviour(StatsBehaviour.save())
  }
}
