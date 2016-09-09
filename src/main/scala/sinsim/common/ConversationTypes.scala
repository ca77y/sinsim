package sinsim.common

case object ConversationTypes {
  type ConversationType = String
  val MATING: ConversationType = "mating"
  val INVESTING: ConversationType = "investing"
  val STATS: ConversationType = "stats"
  val GENETIC: ConversationType = "genetic"
}
