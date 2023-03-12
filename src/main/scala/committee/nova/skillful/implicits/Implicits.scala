package committee.nova.skillful.implicits

import committee.nova.skillful.api.ISkill
import committee.nova.skillful.player.capabilities.{ISkills, SkillInfo}
import committee.nova.skillful.skills.SkillInstance
import committee.nova.skillful.util.Utilities
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation

object Implicits {
  implicit class EntityPlayerMPImplicit(val player: EntityPlayerMP) {
    def getSkills: ISkills = Utilities.getPlayerSkills(player)

    def getSkillStat(skill: ISkill): SkillInstance = Utilities.getPlayerSkillStat(player, skill)

    def getSkillStat(id: ResourceLocation): SkillInstance = Utilities.getPlayerSkillStat(player, id)

    def getSkillInfo(skill: ISkill): SkillInfo = Utilities.getPlayerSkillInfo(player, skill)

    def getSkillInfo(id: ResourceLocation): SkillInfo = Utilities.getPlayerSkillInfo(player, id)

    def sendSkillInfo(instance: SkillInstance): Unit = Utilities.sendSkillInfo(player, instance)
  }
}
