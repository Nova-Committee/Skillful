package committee.nova.skillful.impl.related

import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.api.skill.ISkill
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.{Item, ItemStack}

import java.util.function.BiFunction
import java.util.{Map => JMap}
import scala.jdk.CollectionConverters._

class SkillRelatedFood(food: Item, change: (ServerPlayerEntity, ItemStack) => Map[ISkill, Int]) extends ISkillRelatedFood {
  //Java Compatibility
  def this(food: Item, change: BiFunction[ServerPlayerEntity, ItemStack, JMap[ISkill, Int]]) = this(food, (p, s) => change.apply(p, s).asScala.toMap)

  override def getItemFood: Item = food

  override def getChange(player: ServerPlayerEntity, stack: ItemStack): Map[ISkill, Int] = change.apply(player, stack)
}
