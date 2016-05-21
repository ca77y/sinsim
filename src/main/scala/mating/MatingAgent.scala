package mating

import common.AgentTypes
import common.agent.BaseAgent
import jade.util.Logger

class MatingAgent extends BaseAgent {
  protected[this] val logger = Logger.getJADELogger(getClass.getName)

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF(AgentTypes.MATING)
    addBehaviour(new MatingBehaviour())
  }
}
