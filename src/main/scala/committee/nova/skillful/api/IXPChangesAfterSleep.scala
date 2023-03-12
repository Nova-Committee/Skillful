package committee.nova.skillful.api

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait IXPChangesAfterSleep {
  def change(player: EntityPlayerMP, instance: SkillInstance): Int
}
