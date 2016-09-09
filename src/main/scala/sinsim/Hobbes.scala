package sinsim

import sinsim.common.{BaseExperiment, EnvironmentConstants}

object Hobbes extends BaseExperiment {
  val agentName = "sinsim.agent.HobbesAgent"

  def main( args:Array[String] ):Unit = {
    EnvironmentConstants.NUMBER_OF_AGENTS = args(0).toInt
    EnvironmentConstants.NUMBER_OF_GENERATIONS = args(1).toInt
    run()
  }
}
