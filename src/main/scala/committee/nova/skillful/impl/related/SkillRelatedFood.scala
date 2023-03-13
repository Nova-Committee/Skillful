package committee.nova.skillful.impl.related

import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.api.skill.ISkill
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.{ItemFood, ItemStack}

import java.util.function.BiFunction
import java.util.{Map => JMap}
import scala.collection.JavaConverters._

class SkillRelatedFood(food: ItemFood, change: (EntityPlayerMP, ItemStack) => Map[ISkill, Int]) extends ISkillRelatedFood {
  //Java Compatibility
  def this(food: ItemFood, change: BiFunction[EntityPlayerMP, ItemStack, JMap[ISkill, Int]]) = this(food, (p, s) => change.apply(p, s).asScala.toMap)

  override def getItemFood: ItemFood = food

  override def getChange(player: EntityPlayerMP, stack: ItemStack): Map[ISkill, Int] = change.apply(player, stack)
}
