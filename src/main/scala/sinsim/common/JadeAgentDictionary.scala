package sinsim.common

import sinsim.common.AgentTypes.AgentType
import jade.core.{AID, Agent}
import jade.domain.FIPAAgentManagement.{DFAgentDescription, SearchConstraints, ServiceDescription}
import jade.domain.{DFService, FIPAException}
import jade.util.Logger

trait JadeAgentDictionary {
  protected[this] def logger: Logger
  def agent: Agent

  def registerInDF(agentType: AgentType) = {
    val dfd = new DFAgentDescription()
    dfd.setName(agent.getAID)
    val sd = new ServiceDescription()
    sd.setType(agentType)
    sd.setName(agent.getName)
    dfd.addServices(sd)
    try {
      DFService.register(agent, dfd)
    }
    catch {
      case fe: FIPAException =>
        logger.log(Logger.SEVERE, "registering service failed", fe)
    }
  }

  def findAgents(agentType: AgentType): List[AID] = {
    val template = new DFAgentDescription()
    val sd = new ServiceDescription()
    sd.setType(agentType)
    template.addServices(sd)
    try {
      val sc = new SearchConstraints()
      sc.setMaxResults(EnvironmentConstants.NUMBER_OF_AGENTS.toLong)
      return DFService.search(agent, template, sc).map(i => i.getName).toList
    } catch {
      case ex: FIPAException =>
        logger.log(Logger.SEVERE, "searching for services failed", ex)
    }
    Nil
  }

  def findAgent(agentType: AgentType): Option[AID] = {
    findAgents(agentType) match {
      case i :: _ => Some(i)
      case Nil => None
    }
  }
}
