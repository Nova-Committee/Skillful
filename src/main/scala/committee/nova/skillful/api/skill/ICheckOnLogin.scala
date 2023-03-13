package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait ICheckOnLogin {
  def check(player: EntityPlayerMP, instance: SkillInstance): Unit
}
