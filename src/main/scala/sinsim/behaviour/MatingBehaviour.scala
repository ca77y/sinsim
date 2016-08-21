package sinsim.behaviour

import jade.core.AID
import jade.core.behaviours.{CyclicBehaviour, OneShotBehaviour}
import jade.lang.acl.ACLMessage
import jade.util.Logger
import sinsim.agent.Wallet
import sinsim.common.{ConversationTypes, EnvironmentConstants, JadeAgentDictionary, JadeMessaging}
import sinsim.strategy.MatingStrategies.MatingStrategy

import scala.util.Random

object MatingBehaviour {

  class InitMatingBehaviour extends OneShotBehaviour with JadeMessaging with JadeAgentDictionary {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.MATING

    def agent = getAgent

    override def action(): Unit = {
      sendMessage(conversationType, myAgent.getAID, ACLMessage.PROPAGATE)
    }
  }

  class SupervisorMatingBehaviour(agents: List[AID]) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.MATING
    val len = agents.length / 2
    var turn = 0
    var turnTick = 1

    def initMating(): Unit = {
      turn = turn + 1
      logger.info(s"turn: $turn")
      val randomAgents = Random.shuffle(agents)
      val senders = randomAgents.take(len)
      val receivers = randomAgents.takeRight(len)
      for ((sender, receiver) <- senders.zip(receivers)) {
        sendMessage(sender, ACLMessage.REQUEST, receiver.toString)
      }
    }

    def doneMating(): Unit = {
      for (agent <- agents) {
        sendMessage(ConversationTypes.STATS, agent, ACLMessage.REQUEST)
      }
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.SUBSCRIBE =>
          logger.finer(s"tick: $turnTick")
          if (turnTick < len) {
            turnTick = turnTick + 1
          } else {
            if (turn < EnvironmentConstants.NUMBER_OF_TURNS) {
              turnTick = 1
              initMating()
            } else {
              logger.info(s"simulation done on turn: $turn")
              doneMating()
            }
          }
        case ACLMessage.PROPAGATE => initMating()
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  class AgentMatingBehaviour(matingAgent: AID, wallet: Wallet, strategy: MatingStrategy) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    override val conversationType = ConversationTypes.MATING

    def foundMate(msg: ACLMessage): Unit = {
      if(!wallet.canInvest()) {
        sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
        return
      }
      val mate = str2AID(msg.getContent)
      val (accept, cost) = strategy.acceptMate(mate)
      wallet.balance(cost)
      if(accept) {
        sendMessage(mate, ACLMessage.PROPOSE)
      } else {
        wallet.balance(EnvironmentConstants.CHEATER_PREMIUM)
        sendMessage(mate, ACLMessage.FAILURE)
        sendMessage(matingAgent, ACLMessage.SUBSCRIBE)
      }
    }

    def mate(msg: ACLMessage): Unit = {
      if(!wallet.canInvest()) {
        sendMessage(ConversationTypes.MATING, matingAgent, ACLMessage.SUBSCRIBE)
        return
      }
      val (accept, cost) = strategy.acceptMate(msg.getSender)
      wallet.balance(cost)
      if (accept) {
        sendMessage(msg.getSender, ACLMessage.ACCEPT_PROPOSAL)
      } else {
        wallet.balance(EnvironmentConstants.CHEATER_PREMIUM)
        sendMessage(msg.getSender, ACLMessage.FAILURE)
        sendMessage(matingAgent, ACLMessage.SUBSCRIBE)
      }
    }

    def invest(msg: ACLMessage): Unit = {
      sendMessage(ConversationTypes.INVESTING, msg.getSender, ACLMessage.REQUEST)
    }

    def cheater(msg: ACLMessage): Unit = {
      wallet.balance(EnvironmentConstants.CHEATER_PENALTY)
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => foundMate(msg)
        case ACLMessage.PROPOSE => mate(msg)
        case ACLMessage.ACCEPT_PROPOSAL => invest(msg)
        case ACLMessage.FAILURE => cheater(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  def init() = {
    new InitMatingBehaviour()
  }

  def supervisor(agents: List[AID]) = {
    new SupervisorMatingBehaviour(agents)
  }

  def agent(matingAgent: AID, wallet: Wallet, strategy: MatingStrategy) = {
    new AgentMatingBehaviour(matingAgent, wallet, strategy)
  }
}

