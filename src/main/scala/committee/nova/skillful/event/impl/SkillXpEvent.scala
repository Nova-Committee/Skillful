package committee.nova.skillful.event.impl

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

object SkillXpEvent {
  class Pre(private val player: EntityPlayerMP, private val skill: SkillInstance, private var amount: Int) extends SkillXpEvent(player, skill, amount) {
    def setAmount(newAmount: Int): Unit = amount = newAmount

    override def isCancelable: Boolean = true
  }

  class Post(private val player: EntityPlayerMP, private val skill: SkillInstance, private var amount: Int) extends SkillXpEvent(player, skill, amount)
}

class SkillXpEvent(private val player: EntityPlayerMP, private val skill: SkillInstance, private var amount: Int) extends SkillEvent(player, skill) {
  def getAmount: Int = amount
}