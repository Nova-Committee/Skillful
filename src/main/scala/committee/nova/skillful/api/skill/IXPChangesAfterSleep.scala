package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity

trait IXPChangesAfterSleep {
  def change(player: ServerPlayerEntity, instance: SkillInstance): Int
}
