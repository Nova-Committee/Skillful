package committee.nova.skillful.api.related

import committee.nova.skillful.api.skill.ISkill
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.{Item, ItemStack}

trait ISkillRelatedFood {
  def getItemFood: Item

  def getChange(player: ServerPlayerEntity, stack: ItemStack): Map[ISkill, Int]
}
