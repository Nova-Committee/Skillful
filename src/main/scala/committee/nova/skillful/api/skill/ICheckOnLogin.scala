package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity

trait ICheckOnLogin {
  def check(player: ServerPlayerEntity, instance: SkillInstance): Unit
}
