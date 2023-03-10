package committee.nova.skillful.event.impl

import committee.nova.skillful.api.ISkill

import java.util.UUID

case class SkillXpEvent(private val player: UUID, private val skill: ISkill, private var amount: Int) extends SkillEvent(player, skill) {
  def getAmount: Int = amount

  def setAmount(newAmount: Int): Unit = amount = newAmount

  override def isCancelable: Boolean = true
}
