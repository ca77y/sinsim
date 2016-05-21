package stats

import common.AgentTypes
import jade.core.Agent
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.util.Logger

import scala.collection.mutable.ArrayBuffer

class StatsAgent extends Agent {
  private[this] val logger = Logger.getJADELogger(getClass.getName)
  private[this] var stats = ArrayBuffer[String]()
  private[this] var turn = 0

  def nextTurn(): Unit = {
    turn += 1
    logger.info(stats.mkString("\n"))
    stats.clear()
  }

  def logStats(line:String): Unit = {
    stats += line
  }

  def registerInDF() = {
    val dfd = new DFAgentDescription()
    dfd.setName(getAID)
    val sd = new ServiceDescription()
    sd.setType(AgentTypes.MATING)
    sd.setName(getName)
    dfd.addServices(sd)
    try {
      DFService.register(this, dfd)
    }
    catch {
      case fe: FIPAException =>
        logger.log(Logger.SEVERE, "registering service failed", fe)
    }
  }

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF()
    addBehaviour(new StatsGatherBehaviour(this))
    addBehaviour(new StatsPingBehaviour(this, 1000))
  }
}
