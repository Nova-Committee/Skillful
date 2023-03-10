package committee.nova.skillful.event.impl

import committee.nova.skillful.api.SkillInstance

import java.util.UUID

object SkillXpEvent {
  case class Pre(private val player: UUID, private val skill: SkillInstance, private var amount: Int) extends SkillXpEvent(player, skill, amount) {
    def setAmount(newAmount: Int): Unit = amount = newAmount

    override def isCancelable: Boolean = true
  }

  case class Post(private val player: UUID, private val skill: SkillInstance, private var amount: Int) extends SkillXpEvent(player, skill, amount)
}

abstract class SkillXpEvent(private val player: UUID, private val skill: SkillInstance, private var amount: Int) extends SkillEvent(player, skill) {
  def getAmount: Int = amount
}