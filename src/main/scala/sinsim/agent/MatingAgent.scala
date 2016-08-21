package sinsim.agent

import sinsim.behaviour.MatingBehaviour
import sinsim.common.AgentTypes
import jade.util.Logger

class MatingAgent extends BaseAgent {
  protected val logger = Logger.getMyLogger(getClass.getCanonicalName)

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF(AgentTypes.MATING)

    val agents = findAgents(AgentTypes.INVEST)
    addBehaviour(MatingBehaviour.supervisor(agents))
    addBehaviour(MatingBehaviour.init())
  }
}
