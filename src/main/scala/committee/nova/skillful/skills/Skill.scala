package committee.nova.skillful.skills

import committee.nova.skillful.api.ISkill
import net.minecraft.util.ResourceLocation

import java.util.function.IntFunction

class Skill(private val id: ResourceLocation, private val maxLevel: Int) extends ISkill {
  private var fun: Int => Int = i => 100 * i

  def this(id: ResourceLocation, maxLevel: Int, fun: Int => Int) = {
    this(id, maxLevel)
    this.fun = fun
  }

  //Java compatibility
  def this(id: ResourceLocation, maxLevel: Int, fun: IntFunction[Integer]) = {
    this(id, maxLevel)
    this.fun = i => fun.apply(i)
  }

  override def getId: ResourceLocation = id

  override def getMaxLevel: Int = maxLevel

  override def getLevelRequiredXp(level: Int): Int = fun.apply(level)
}
