package committee.nova.skillful.api

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait IActOnLevelChange {
  def act(player: EntityPlayerMP, instance: SkillInstance, isUp: Boolean): Unit
}
