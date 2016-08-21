package sinsim.behaviour

import jade.core.AID
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage
import jade.util.Logger
import sinsim.agent.Wallet
import sinsim.common.ConversationTypes
import sinsim.strategy.InvestStrategies.InvestStrategy
import sinsim.strategy.MatingStrategies.MatingStrategy

object InvestBehaviour {

  class InvestBehaviour(
                         matingAgent: AID,
                         wallet: Wallet,
                         investStrategy: InvestStrategy,
                         matingStrategy: MatingStrategy)
    extends CyclicBehaviour with BaseBehaviour {

    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.INVESTING

    private def makeWalletMessage(wallet: Wallet) = {
      s"${wallet.money}:${wallet.skill}:${wallet.cheat}:${wallet.awareness}"
    }

    def makeProfitMessage(wallet: Wallet, myProfit: Int, otherProfit: Int) = {
      s"${wallet.money}:${wallet.skill}:${wallet.cheat}:${wallet.awareness}:$myProfit:$otherProfit"
    }

    def makeOffer(msg: ACLMessage): Unit = {
      if (wallet.canInvest()) {
        sendMessage(msg.getSender, ACLMessage.INFORM, makeWalletMessage(wallet))
      } else {
        sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
      }
    }

    def handleOffer(msg: ACLMessage): Unit = {
      if (wallet.canInvest()) {
        val otherWallet = Wallet(msg.getContent)
        val (myProfit, otherProfit) = investStrategy.invest(wallet, otherWallet)
        sendMessage(msg.getSender, ACLMessage.PROPOSE, makeProfitMessage(wallet, myProfit, otherProfit))
      } else {
        sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
      }
    }

    def handleResult(msg: ACLMessage): Unit = {
      val Array(money, skill, cheat, awareness, otherProfit, myProfit) = msg.getContent.split(':')
      val otherWallet = Wallet(money.toInt, skill.toInt, cheat.toInt, awareness.toInt)
      val (valid, checked) = investStrategy.validate(wallet, myProfit.toInt, otherWallet, otherProfit.toInt)
      if (valid) {
        wallet.balance(myProfit.toInt)
        sendMessage(msg.getSender, ACLMessage.ACCEPT_PROPOSAL, otherProfit)
        if (checked) {
          matingStrategy.trust(msg.getSender)
        }
      } else {
        if (checked) {
          matingStrategy.quarantine(msg.getSender)
        }
        sendMessage(msg.getSender, ACLMessage.REJECT_PROPOSAL, msg.getContent)
      }
    }

    def handleAccept(msg: ACLMessage): Unit = {
      wallet.balance(msg.getContent.toInt)
      sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
    }

    def handleReject(msg: ACLMessage): Unit = {
      sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => makeOffer(msg)
        case ACLMessage.INFORM => handleOffer(msg)
        case ACLMessage.PROPOSE => handleResult(msg)
        case ACLMessage.ACCEPT_PROPOSAL => handleAccept(msg)
        case ACLMessage.REJECT_PROPOSAL => handleReject(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  def agent(matingAgent: AID,
            wallet: Wallet,
            investStrategy: InvestStrategy,
            matingStrategy: MatingStrategy) = {
    new InvestBehaviour(matingAgent, wallet, investStrategy, matingStrategy)
  }
}
