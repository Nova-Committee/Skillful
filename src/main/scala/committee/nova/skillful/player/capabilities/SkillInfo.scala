package committee.nova.skillful.player.capabilities

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.BossInfo
import net.minecraft.world.BossInfo.{Color, Overlay}

import java.util.UUID

class SkillInfo(private val skill: ResourceLocation) extends BossInfo(UUID.randomUUID(),
  // TODO:
  new TextComponentString(skill.getPath), Color.BLUE, Overlay.PROGRESS) {
  private var expireTime: Int = 20

  def getId: ResourceLocation = skill

  def isActive: Boolean = expireTime > 0

  def tick(): Boolean = {
    expireTime -= 1
    !isActive
  }

  def activate(): Unit = expireTime = 20

  override def equals(obj: Any): Boolean = obj match {
    case s: SkillInfo => s.skill.equals(skill)
    case _ => false
  }

  override def hashCode(): Int = skill.hashCode()
}
