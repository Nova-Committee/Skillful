package committee.nova.skillful.api

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait ICheckOnLogin {
  def check(player: EntityPlayerMP, instance: SkillInstance): Unit
}
