package common.behaviour

import jade.core.behaviours.TickerBehaviour
import rousseau.RousseauAgent

class DeathBehaviour(agent: RousseauAgent, tick: Long) extends TickerBehaviour(agent, tick) {
  override def onTick(): Unit = {
    agent.consume()
  }
}
