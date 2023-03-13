package committee.nova.skillful.api.related

import committee.nova.skillful.api.skill.ISkill
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.{ItemFood, ItemStack}

trait ISkillRelatedFood {
  def getItemFood: ItemFood

  def getChange(player: EntityPlayerMP, stack: ItemStack): Map[ISkill, Int]
}
