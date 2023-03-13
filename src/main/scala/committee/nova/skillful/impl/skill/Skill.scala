package committee.nova.skillful.impl.skill

import committee.nova.skillful.api.skill.ISkill
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

import java.util.function.IntFunction

class Skill(private val id: ResourceLocation, private val maxLevel: Int, color: BossInfo.Color) extends ISkill {
  private var fun: Int => Int = i => 100 * i

  def this(id: ResourceLocation, maxLevel: Int, color: BossInfo.Color, fun: Int => Int) = {
    this(id, maxLevel, color)
    this.fun = fun
  }

  //Java compatibility
  def this(id: ResourceLocation, maxLevel: Int, color: BossInfo.Color, fun: IntFunction[Integer]) = {
    this(id, maxLevel, color)
    this.fun = i => fun.apply(i)
  }

  override def getId: ResourceLocation = id

  override def getMaxLevel: Int = maxLevel

  override def getLevelRequiredXp(level: Int): Int = fun.apply(level)

  override def getColor: BossInfo.Color = color
}
