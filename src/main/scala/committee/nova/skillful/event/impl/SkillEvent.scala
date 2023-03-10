package committee.nova.skillful.event.impl

import committee.nova.skillful.api.ISkill
import net.minecraftforge.fml.common.eventhandler.Event

import java.util.UUID

abstract class SkillEvent(private val player: UUID, private val skill: ISkill) extends Event {
  def getPlayerUUID: UUID = player

  def getSkill: ISkill = skill
}
