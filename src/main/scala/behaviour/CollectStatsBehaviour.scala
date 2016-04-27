package behaviour

import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage

class CollectStatsBehaviour extends CyclicBehaviour {

  def dispatchMessage(msg: ACLMessage): Unit = {

  }

  override def action(): Unit = {
    val msg = Option(myAgent.receive())
    msg match {
      case Some(m) => dispatchMessage(m)
      case None => block()
    }
  }
}
