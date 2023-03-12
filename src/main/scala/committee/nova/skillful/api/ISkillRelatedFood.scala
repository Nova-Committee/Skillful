package committee.nova.skillful.api

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.{ItemFood, ItemStack}

trait ISkillRelatedFood {
  def getItemFood: ItemFood

  def getRelatedSkill: ISkill

  def getChange(player: EntityPlayerMP, stack: ItemStack): Int
}
