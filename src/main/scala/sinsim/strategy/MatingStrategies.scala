package sinsim.strategy

import jade.core.AID
import sinsim.agent.Wallet
import sinsim.common.EnvironmentConstants

import scala.collection.mutable
import scala.util.Random

object MatingStrategies {


  sealed trait MatingStrategy {
    def cleanup(wallet: Wallet): Unit

    def cost: Int

    def quarantine(agent: AID)

    def trust(agent: AID)

    def acceptMate(agent: AID): (Boolean, Int)
  }

  class SimpleMatingStrategy extends MatingStrategy {
    override val cost = 0

    override def acceptMate(agent: AID) = (true, cost)

    def trust(agent: AID) = {}

    override def quarantine(agent: AID): Unit = {}

    def cleanup(wallet: Wallet): Unit = {
      wallet.money = 1000
    }
  }

  class MemoryMatingStrategy extends MatingStrategy {
    override val cost = 0

    val badAgents = mutable.Set[String]()
    val goodAgents = mutable.Set[String]()

    override def acceptMate(agent: AID) = {
      (!badAgents.contains(agent.getLocalName), cost)
    }

    def trust(agent: AID) = {
      goodAgents += agent.getLocalName
    }

    override def quarantine(agent: AID): Unit = {
      badAgents += agent.getLocalName
    }

    def cleanup(wallet: Wallet): Unit = {
      badAgents.clear()
      goodAgents.clear()
      wallet.money = 1000
    }
  }

  class SovereignMatingStrategy() extends MatingStrategy {
    override val cost = EnvironmentConstants.SOVEREIGN_COST
    val goodAgents = mutable.Set[String]()
    val badAgents = mutable.Set[String]()

    override def quarantine(agent: AID): Unit = {
      badAgents += agent.getLocalName
      SovereignMatingProperties.badAgents += agent.getLocalName
    }

    override def trust(agent: AID) = {
      goodAgents += agent.getLocalName
    }

    override def acceptMate(agent: AID): (Boolean, Int) = {
      if(goodAgents.contains(agent.getLocalName)) {
        if(SovereignMatingProperties.bank > 0) {
          SovereignMatingProperties.bank -= cost
          return (true, cost)
        }
      } else if(badAgents.contains(agent.getLocalName)) {
        return (false, 0)
      } else {
        if(Random.nextDouble() < EnvironmentConstants.SOVEREIGN_FACTOR) {
          SovereignMatingProperties.bank += cost
          (!SovereignMatingProperties.badAgents.contains(agent.getLocalName), -cost)
        }
      }
      (true, 0)
    }

    def cleanup(wallet: Wallet): Unit = {
      badAgents.clear()
      goodAgents.clear()
      wallet.money = 1000
      SovereignMatingProperties.bank = 0
    }
  }

  object SovereignMatingProperties {
    val badAgents = mutable.Set[String]()
    var bank = 0
  }

  def simple() = {
    new SimpleMatingStrategy()
  }

  def memory() = {
    new MemoryMatingStrategy()
  }

  def sovereign() = {
    new SovereignMatingStrategy()
  }
}
