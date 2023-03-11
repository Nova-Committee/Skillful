package committee.nova.skillful.event.impl

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

object SkillLevelEvent {
  class Up(player: EntityPlayerMP, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel) {
    override def isUp: Boolean = true
  }

  class Down(player: EntityPlayerMP, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel) {
    override def isUp: Boolean = false
  }
}

abstract class SkillLevelEvent(private val player: EntityPlayerMP, private val skill: SkillInstance, private val currentLevel: Int) extends SkillEvent(player, skill) {
  def getCurrentLevel: Int = currentLevel

  def isUp: Boolean

  def isDown: Boolean = !isUp
}
