package committee.nova.skillful.event.impl

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.Event

abstract class SkillEvent(private val player: EntityPlayerMP, private val skill: SkillInstance) extends Event {
  def getPlayer: EntityPlayerMP = player

  def getSkillInstance: SkillInstance = skill
}
