package committee.nova.skillful.event.impl

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.eventbus.api.Event

abstract class SkillEvent(private val player: ServerPlayerEntity, private val skill: SkillInstance) extends Event {
  def getPlayer: ServerPlayerEntity = player

  def getSkillInstance: SkillInstance = skill
}
