package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP

trait IXPChangesAfterSleep {
  def change(player: EntityPlayerMP, instance: SkillInstance): Int
}
