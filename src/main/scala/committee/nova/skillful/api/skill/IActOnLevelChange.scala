package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait IActOnLevelChange {
  def act(player: EntityPlayerMP, instance: SkillInstance, isUp: Boolean): Unit
}
