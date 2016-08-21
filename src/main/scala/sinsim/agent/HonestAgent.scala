package sinsim.agent

import jade.util.Logger
import sinsim.strategy.InvestStrategies.InvestStrategy
import sinsim.strategy.MatingStrategies.MatingStrategy
import sinsim.strategy.{InvestStrategies, MatingStrategies}

class HonestAgent extends InvestAgent {
  protected val logger = Logger.getMyLogger(getClass.getCanonicalName)

  override def matingStrategy: MatingStrategy = MatingStrategies.simple()

  override def investStrategy: InvestStrategy = InvestStrategies.skill()
}


