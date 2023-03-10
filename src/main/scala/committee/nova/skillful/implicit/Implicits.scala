package committee.nova.skillful.`implicit`

import committee.nova.skillful.api.{ISkill, SkillInstance}
import committee.nova.skillful.player.capabilities.ISkills
import committee.nova.skillful.util.Utilities
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation

object Implicits {
  implicit class EntityPlayerMPImplicit(val player: EntityPlayerMP) {
    def getSkills: ISkills = Utilities.getPlayerSkills(player)

    def getSkillStat(skill: ISkill): SkillInstance = Utilities.getPlayerSkillStat(player, skill)

    def getSkillStat(id: ResourceLocation): SkillInstance = Utilities.getPlayerSkillStat(player, id)
  }
}
