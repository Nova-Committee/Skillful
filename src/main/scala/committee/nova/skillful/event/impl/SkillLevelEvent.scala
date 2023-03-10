package committee.nova.skillful.event.impl

import committee.nova.skillful.skills.SkillInstance

import java.util.UUID

object SkillLevelEvent {
  case class Up(player: UUID, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel) {
    override def isUp: Boolean = true
  }

  case class Down(player: UUID, skill: SkillInstance, currentLevel: Int) extends SkillLevelEvent(player, skill, currentLevel) {
    override def isUp: Boolean = false
  }
}

abstract class SkillLevelEvent(private val player: UUID, private val skill: SkillInstance, private val currentLevel: Int) extends SkillEvent(player, skill) {
  def getCurrentLevel: Int = currentLevel

  def isUp: Boolean

  def isDown: Boolean = !isUp
}
