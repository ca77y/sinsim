package sinsim

import sinsim.common.{BaseExperiment, EnvironmentConstants}

object Rousseau extends BaseExperiment {
  val agentName = "sinsim.agent.RousseauAgent"

  def main( args:Array[String] ):Unit = {
    EnvironmentConstants.NUMBER_OF_AGENTS = args(0).toInt
    run()
  }
}
