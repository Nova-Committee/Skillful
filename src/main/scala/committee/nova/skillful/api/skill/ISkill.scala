package committee.nova.skillful.api.skill

import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

trait ISkill {
  def getId: ResourceLocation

  def getColor: BossInfo.Color

  def getMaxLevel: Int

  def getLevelRequiredXp(level: Int): Int

  override def equals(obj: Any): Boolean = obj match {
    case that: ISkill => that.getId.equals(getId)
    case _ => false
  }

  override def hashCode(): Int = getId.hashCode()
}
