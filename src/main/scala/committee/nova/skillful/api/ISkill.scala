package committee.nova.skillful.api

import net.minecraft.util.ResourceLocation

trait ISkill {
  def getId: ResourceLocation

  def getMaxLevel: Int

  def getLevelRequiredXp(level: Int): Int

  override def equals(obj: Any): Boolean = obj match {
    case that: ISkill => that.getId.equals(getId)
    case _ => false
  }

  override def hashCode(): Int = getId.hashCode()
}
