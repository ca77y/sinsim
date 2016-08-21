package sinsim.strategy

import sinsim.agent.Wallet
import sinsim.common.EnvironmentConstants

object InvestStrategies {

  sealed trait InvestStrategy {
    def calculateProfit(w1: Wallet, w2: Wallet) = {
      val i1 = w1.investMoney()
      val i2 = w2.investMoney()
      val profit = i1 + i2
      val skills = w1.skill + w2.skill
      val p1 = math.floor(profit * w1.skill / skills.toDouble).toInt
      val p2 = profit - p1
      (p1 - i1, p2 - i2)
    }

    def validate(wallet: Wallet, profit: Int, otherWallet: Wallet, otherProfit: Int): (Boolean, Boolean)

    def invest(w1: Wallet, w2: Wallet): (Int, Int)
  }

  class SkillInvestStrategy extends InvestStrategy {
    override def invest(w1: Wallet, w2: Wallet) = {
      calculateProfit(w1, w2)
    }

    override def validate(w1: Wallet, p1: Int, w2: Wallet, p2: Int) = {
      (true, false)
    }
  }

  class CheatInvestStrategy extends InvestStrategy {
    override def invest(w1: Wallet, w2: Wallet) = {
      val i1 = w1.investMoney()
      val (p1, p2) = calculateProfit(w1, w2)
      if (w1.cheat > EnvironmentConstants.MIN_CHEAT_VALUE) {
        val cp1 = math.round(w1.cheat * EnvironmentConstants.CHEAT_FACTOR * (p1 + i1) / 100.0).toInt
        (p1 + cp1, p2 - cp1)
      } else {
        (p1, p2)
      }
    }

    override def validate(w1: Wallet, p1: Int, w2: Wallet, p2: Int) = {
      (true, false)
    }
  }

  class CheatAndCheckInvestStrategy extends CheatInvestStrategy {
    override def validate(w1: Wallet, p1: Int, w2: Wallet, p2: Int) = {
      val (vp2, vp1) = calculateProfit(w2, w1)
      if (p1 == vp1 && p2 == vp2) {
        (true, true)
      } else {
        (false, true)
      }
    }
  }

  def skill() = {
    new SkillInvestStrategy()
  }

  def cheat() = {
    new CheatInvestStrategy()
  }

  def cheatAndCheck() = {
    new CheatAndCheckInvestStrategy()
  }
}
