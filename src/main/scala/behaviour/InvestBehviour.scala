package behaviour

import jade.core.behaviours.CyclicBehaviour

class InvestBehviour extends CyclicBehaviour {
  override def action(): Unit = {
    val msg = myAgent.receive();
    print(msg)
  }


}
