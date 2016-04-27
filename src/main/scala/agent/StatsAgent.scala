package agent

import jade.core.Agent
import jade.core.behaviours.CyclicBehaviour
import jade.util.Logger

class StatsAgent extends Agent {
  private[this] val logger = Logger.getJADELogger(getClass.getName)

  override def setup() = {
    addBehaviour(new CyclicBehaviour() {
      override def action(): Unit = {
        val msg = myAgent.receive()
        if(msg != null) {
          logger.info("message received in 1 behaviour")
        } else {
          block()
        }
      }
    })
    addBehaviour(new CyclicBehaviour() {
      override def action(): Unit = {
        val msg = myAgent.receive()
        if(msg != null) {
          logger.info("message received in 2 behaviour")
        } else {
          block()
        }
      }
    })
  }
}
