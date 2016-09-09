package sinsim

import jade.util.leap.{Properties => LeapProperties}
import sinsim.common.{BaseExperiment, EnvironmentConstants}

object Honest extends BaseExperiment {
  val agentName = "sinsim.agent.HonestAgent"

  def main( args:Array[String] ):Unit = {
    EnvironmentConstants.NUMBER_OF_AGENTS = args(0).toInt
    EnvironmentConstants.NUMBER_OF_GENERATIONS = args(1).toInt
    run()
  }
}
