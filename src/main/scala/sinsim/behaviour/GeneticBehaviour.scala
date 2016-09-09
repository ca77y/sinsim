package sinsim.behaviour

import jade.core.AID
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage
import jade.util.Logger
import sinsim.agent.Wallet
import sinsim.common.{ConversationTypes, EnvironmentConstants}

import scala.collection.mutable
import scala.util.Random

object GeneticBehaviour {

  class GeneticMatingBehaviour(agents: List[AID]) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.GENETIC
    val agentWallets = mutable.Map[AID, Wallet]()

    def spawnValue(v1: Int, v2:Int) = {
      val s1 = String.format("%08d", v1.toBinaryString.toLong.asInstanceOf[Object])
      val s2 = String.format("%08d", v2.toBinaryString.toLong.asInstanceOf[Object])
      val ns1 = s1.substring(0, 4) + s2.substring(4,8)
      val ns2 = s2.substring(0, 4) + s1.substring(4,8)
      val nv1 = Integer.parseInt(ns1, 2)
      val nv2 = Integer.parseInt(ns2, 2)
      val r1 = if(nv1 > 100) 100 else nv1
      val r2 = if(nv2 > 100) 100 else nv2
      (r1, r2)
    }

    def spawnChildren(w1: Wallet, w2: Wallet) = {
      val (s1, s2) = spawnValue(w1.skill, w2.skill)
      val (c1, c2) = spawnValue(w1.cheat, w2.cheat)
      val (a1, a2) = spawnValue(w1.awareness, w2.awareness)
      List(
        Wallet(EnvironmentConstants.INIT_MONEY, s1, c1, a1),
        Wallet(EnvironmentConstants.INIT_MONEY, s2, c2, a2)
      )
    }

    def crossover(leaders: List[Wallet]) = {
      val List(seq1, seq2) = Random.shuffle(leaders).grouped(leaders.size / 2).toList
      val children = for((w1, w2) <- seq1.zip(seq2)) yield {spawnChildren(w1, w2)}
      children.flatten
    }

    def mutateValue(value: Int) = {
      val mutator = math.pow(2, Random.nextInt(8)).toInt
      val newValue = value ^ mutator
      if(newValue > 100) {
        100
      } else {
        newValue
      }
    }

    def mutate(children: List[Wallet]) = {
      for(child <- children) {
        child.skill = mutateValue(child.skill)
        child.cheat = mutateValue(child.cheat)
        child.awareness = mutateValue(child.awareness)
      }
      children
    }

    def selection() = {
      agentWallets.toSeq.sortBy(_._2.money)
      val ordered = agentWallets.toSeq.sortBy(_._2.money)
      val List(losers, leaders) = ordered.grouped(ordered.size / 2).toList
      (mutable.Map(leaders:_*), mutable.Map(losers:_*))
    }

    def newGeneration(msg: ACLMessage): Unit = {
      agentWallets += msg.getSender -> Wallet(msg.getContent)
      if(agentWallets.size == EnvironmentConstants.NUMBER_OF_AGENTS) {
        val (leaders, losers) = selection()
        val children = crossover(leaders.values.toList)
        val mutants = mutate(children)
        for ((a, w) <- losers.keys.toList.zip(mutants)) {
          sendMessage(a, ACLMessage.PROPOSE, w.toContent())
        }
        for(a <- leaders.keys) {
          sendMessage(a, ACLMessage.INFORM)
        }
        agentWallets.clear()
      }
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.INFORM => newGeneration(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  class GeneticAgentBehaviour(wallet: Wallet) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.GENETIC

    def initGenetic(msg: ACLMessage): Unit = {
      sendMessage(msg.getSender, ACLMessage.INFORM, wallet.toContent())
    }

    def reborn(msg: ACLMessage): Unit = {
      logger.fine(s"reborn ${myAgent.getLocalName}")
      val newWallet = Wallet(msg.getContent)
      wallet.update(newWallet)
    }

    def survive(msg: ACLMessage): Unit = {
      logger.fine(s"survive ${myAgent.getLocalName}")
      sendMessage(ConversationTypes.MATING, msg.getSender, ACLMessage.SUBSCRIBE)
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.SUBSCRIBE => initGenetic(msg)
        case ACLMessage.PROPOSE => reborn(msg)
        case ACLMessage.INFORM => survive(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  def agent(wallet: Wallet) = {
    new GeneticAgentBehaviour(wallet)
  }

  def supervisor(agents: List[AID]) = {
    new GeneticMatingBehaviour(agents)
  }
}
