package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity

trait IActOnLevelChange {
  def act(player: ServerPlayerEntity, instance: SkillInstance, isUp: Boolean): Unit
}
