package sinsim.behaviour

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import jade.core.{AID, Runtime}
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage
import jade.util.Logger
import sinsim.agent.Wallet
import sinsim.common.{ConversationTypes, EnvironmentConstants}

object StatsBehaviour {

  class SaveStatsBehaviour extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.STATS
    val stringBuilder = new StringBuilder
    var agentCount = 1
    stringBuilder.append("name;money;skill;cheat;awarness\n")

    def save(msg: ACLMessage): Unit = {
      stringBuilder.append(msg.getContent + "\n")
      if (agentCount < EnvironmentConstants.NUMBER_OF_AGENTS) {
        agentCount = agentCount + 1
      } else {
        logger.info(s"saving results for ${agent.getLocalName}")
        val filename = System.currentTimeMillis.toString
        val file = Paths.get(s"./results/$filename.txt")
        Files.createDirectories(file.getParent)
        val pw = new PrintWriter(file.toFile, "UTF-8")
        pw.write(stringBuilder.toString())
        pw.close()
      }
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => save(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  class SendStatsBehaviour(statsAgent: AID, wallet: Wallet) extends CyclicBehaviour with BaseBehaviour {
    protected[this] val logger = Logger.getMyLogger(getClass.getCanonicalName)
    val conversationType = ConversationTypes.STATS

    def send(msg: ACLMessage): Unit = {
      val msg = s"${agent.getLocalName};${wallet.money};${wallet.skill};${wallet.cheat};${wallet.awareness}"
      sendMessage(statsAgent, ACLMessage.REQUEST, msg)
    }

    override def handleMessage(msg: ACLMessage): Unit = {
      msg.getPerformative match {
        case ACLMessage.REQUEST => send(msg)
        case _ => logger.severe(s"Unknown message $msg")
      }
    }
  }

  def save() = {
    new SaveStatsBehaviour()
  }

  def agent(statsAgent: AID, wallet: Wallet) = {
    new SendStatsBehaviour(statsAgent, wallet)
  }
}
