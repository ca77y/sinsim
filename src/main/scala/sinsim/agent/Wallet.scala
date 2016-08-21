package sinsim.agent

import sinsim.common.EnvironmentConstants

case class Wallet(var money: Int, skill: Int, cheat: Int, awareness: Int) {
  require(skill > 0 && skill <= 100)
  require(cheat > 0 && cheat <= 100)
  require(awareness > 0 && awareness <= 100)

  def balance(value: Int) = {
    if (money + value < 0) {
      money = 0
    } else {
      money += value
    }
  }

  def investMoney() = {
    math.round(money * EnvironmentConstants.INVEST_FACTOR).toInt
  }

  def canInvest() = {
    EnvironmentConstants.MIN_INVEST_MONEY < money
  }
}

object Wallet {
  def apply(value: String): Wallet = {
    val Array(money, skill, cheat, awareness) = value.split(':')
    new Wallet(money.toInt, skill.toInt, cheat.toInt, awareness.toInt)
  }
}
