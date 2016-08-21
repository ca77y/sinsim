package sinsim

import sinsim.common.{BaseExperiment, EnvironmentConstants}

object Cheat extends BaseExperiment {
  val agentName = "sinsim.agent.CheatAgent"

  def main(args:Array[String]):Unit = {
    EnvironmentConstants.NUMBER_OF_AGENTS = args(0).toInt
    run()
  }
}
