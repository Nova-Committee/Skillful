package committee.nova.skillful.event.impl

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

object SkillLevelEvent {
  class Up(player: EntityPlayerMP, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel)

  class Down(player: EntityPlayerMP, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel) {
    override def isUp: Boolean = false
  }
}

class SkillLevelEvent(private val player: EntityPlayerMP, private val skill: SkillInstance, private val currentLevel: Int) extends SkillEvent(player, skill) {
  def getCurrentLevel: Int = currentLevel

  def isUp: Boolean = true

  def isDown: Boolean = !isUp
}
