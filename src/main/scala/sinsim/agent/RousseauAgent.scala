package sinsim.agent

import jade.util.Logger
import sinsim.strategy.InvestStrategies.InvestStrategy
import sinsim.strategy.{InvestStrategies, MatingStrategies}
import sinsim.strategy.MatingStrategies.MatingStrategy

class RousseauAgent extends InvestAgent {
  protected val logger = Logger.getMyLogger(getClass.getCanonicalName)

  override def matingStrategy: MatingStrategy = MatingStrategies.memory()

  override def investStrategy: InvestStrategy = InvestStrategies.cheatAndCheck()
}
