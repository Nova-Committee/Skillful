package committee.nova.skillful.player.capabilities

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.BossInfo.Overlay
import net.minecraft.world.BossInfoServer

class SkillInfo(private val skill: SkillInstance) extends BossInfoServer(
  new TextComponentTranslation(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}", skill.getCurrentLevel.toString), skill.getSkill.getColor, Overlay.PROGRESS) {
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
