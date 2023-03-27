package committee.nova.skillful.player.capabilities.info

import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.util.Utilities
import net.minecraft.world.BossInfo.Overlay
import net.minecraft.world.BossInfoServer

class SkillInfo(private val skill: SkillInstance) extends BossInfoServer(Utilities.getSkillDesc(skill, 0), skill.getSkill.getColor, Overlay.PROGRESS) {
  private var expireTime: Int = getMaxExpireTime

  def getMaxExpireTime: Int = 100

  def getSkillInstance: SkillInstance = skill

  def isActive: Boolean = expireTime > 0

  def tick(): Boolean = {
    expireTime -= 1
    !isActive
  }

  def activate(): Unit = expireTime = getMaxExpireTime

  override def equals(obj: Any): Boolean = obj match {
    case s: SkillInfo => s.skill.equals(skill)
    case _ => false
  }

  override def hashCode(): Int = skill.hashCode()
}
