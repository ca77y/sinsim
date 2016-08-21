package sinsim.agent

import jade.util.Logger
import sinsim.strategy.InvestStrategies.InvestStrategy
import sinsim.strategy.{InvestStrategies, MatingStrategies}
import sinsim.strategy.MatingStrategies.MatingStrategy

class CheatAgent extends InvestAgent {
  protected val logger = Logger.getMyLogger(getClass.getCanonicalName)

  override def matingStrategy: MatingStrategy = MatingStrategies.simple()

  override def investStrategy: InvestStrategy = InvestStrategies.cheat()
}
