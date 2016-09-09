package sinsim.agent

import sinsim.behaviour.{GeneticBehaviour, InvestBehaviour, MatingBehaviour, StatsBehaviour}
import sinsim.common.{AgentTypes, EnvironmentConstants}
import sinsim.strategy.InvestStrategies.InvestStrategy
import sinsim.strategy.MatingStrategies.MatingStrategy

import scala.util.Random

trait InvestAgent extends BaseAgent {
  def matingStrategy: MatingStrategy

  def investStrategy: InvestStrategy

  override def setup(): Unit = {
    logger.info(s"starting $getName")
    registerInDF(AgentTypes.INVEST)
    val wallet = Wallet(
      EnvironmentConstants.INIT_MONEY,
      Random.nextInt(EnvironmentConstants.MAX_SKILL) + 1,
      Random.nextInt(EnvironmentConstants.MAX_SKILL) + 1,
      Random.nextInt(EnvironmentConstants.MAX_SKILL) + 1)

    findAgent(AgentTypes.MATING) match {
      case Some(a) =>
        addBehaviour(MatingBehaviour.agent(a, wallet, matingStrategy))
        addBehaviour(InvestBehaviour.agent(a, wallet, investStrategy, matingStrategy))
        addBehaviour(GeneticBehaviour.agent(wallet))
      case None => logger.severe("no mating agent found")
    }

    findAgent(AgentTypes.STATS) match {
      case Some(a) => addBehaviour(StatsBehaviour.agent(a, wallet, matingStrategy))
      case None => logger.severe("no stats agent found")
    }
  }
}
