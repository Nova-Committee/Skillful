package committee.nova.skillful.skills

import committee.nova.skillful.api.{ISkill, ISkillRelatedFood}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.{ItemFood, ItemStack}

import java.util.function.BiFunction

class SkillRelatedFood(food: ItemFood, skill: ISkill, change: (EntityPlayerMP, ItemStack) => Int) extends ISkillRelatedFood {
  //Java Compatibility
  def this(food: ItemFood, skill: ISkill, change: BiFunction[EntityPlayerMP, ItemStack, Int]) = this(food, skill, (p, s) => change.apply(p, s))

  override def getItemFood: ItemFood = food

  override def getRelatedSkill: ISkill = skill

  override def getChange(player: EntityPlayerMP, stack: ItemStack): Int = change.apply(player, stack)
}
