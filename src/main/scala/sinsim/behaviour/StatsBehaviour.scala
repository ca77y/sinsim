package sinsim.behaviour

import java.io.PrintWriter
import java.nio.file.{Files, Paths}

import jade.core.AID
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage
import jade.util.Logger
import sinsim.agent.Wallet
import sinsim.common.{ConversationTypes, EnvironmentConstants}
import sinsim.strategy.MatingStrategies.MatingStrategy

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object StatsBehaviour {

  class SaveStatsBehaviour extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.STATS
    val stringBuilder = new StringBuilder
    val genStats = mutable.Map[String, mutable.ListBuffer[Int]]()
    var agentCount = 1
    var genAgentCount = 1
    val filename = System.currentTimeMillis.toString
    stringBuilder.append("name;money;skill;cheat;awarness\n")

    def save(msg: ACLMessage): Unit = {
      stringBuilder.append(msg.getContent + "\n")
      if (agentCount < EnvironmentConstants.NUMBER_OF_AGENTS) {
        agentCount = agentCount + 1
      } else {
        logger.info(s"saving results for ${agent.getLocalName}")
        val file = Paths.get(s"./results/$filename.txt")
        val genfile = Paths.get(s"./results/$filename-gen.txt")
        Files.createDirectories(file.getParent)
        val pw = new PrintWriter(file.toFile, "UTF-8")
        pw.write(stringBuilder.toString())
        pw.close()
        val gpw = new PrintWriter(genfile.toFile, "UTF-8")
        val gensb = new StringBuilder
        for ((k,v) <- genStats) {
          gensb.append(s"$k;" + v.mkString(";") + "\n")
        }
        gpw.write(gensb.toString())
        gpw.close()
      }
    }

    def saveGen(msg: ACLMessage): Unit = {
      val name = msg.getSender.getLocalName
      val wallet = Wallet(msg.getContent)
      genStats.get(name) match {
        case Some(v) => v += wallet.cheat
        case None => genStats.put(name, ListBuffer(wallet.cheat))
      }
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => save(msg)
        case ACLMessage.INFORM => saveGen(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  class SendStatsBehaviour(statsAgent: AID, wallet: Wallet, strategy: MatingStrategy) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.STATS

    def send(msg: ACLMessage): Unit = {
      val msg = s"${agent.getLocalName};${wallet.money};${wallet.skill};${wallet.cheat};${wallet.awareness}"
      sendMessage(statsAgent, ACLMessage.REQUEST, msg)
    }

    def sendGen(msg: ACLMessage): Unit = {
      sendMessage(statsAgent, ACLMessage.INFORM, wallet.toContent())
      strategy.cleanup(wallet)
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => send(msg)
        case ACLMessage.INFORM => sendGen(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  def save() = {
    new SaveStatsBehaviour()
  }

  def agent(statsAgent: AID, wallet: Wallet, strategy: MatingStrategy) = {
    new SendStatsBehaviour(statsAgent, wallet, strategy)
  }
}
