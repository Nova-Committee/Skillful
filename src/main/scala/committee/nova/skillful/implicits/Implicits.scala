package committee.nova.skillful.implicits

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.info.SkillInfo
import committee.nova.skillful.util.Utilities
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.util.ResourceLocation

object Implicits {
  implicit class EntityPlayerImplicit(val player: EntityPlayer) {
    def getSkills: ISkills = Utilities.getPlayerSkills(player)

    def getSkillStat(skill: ISkill): SkillInstance = Utilities.getPlayerSkillStat(player, skill)

    def getSkillStat(id: ResourceLocation): SkillInstance = Utilities.getPlayerSkillStat(player, id)

    def getSkillStatStrictly(id: ResourceLocation): Option[SkillInstance] = Utilities.getPlayerSkillStatStrictly(player, id)

    def getSkillStatCleanly(id: ResourceLocation): Option[SkillInstance] = Utilities.getPlayerSkillStatCleanly(player, id)

    def getSkillStatCleanly(skill: ISkill): Option[SkillInstance] = Utilities.getPlayerSkillStatCleanly(player, skill)

    def getSkillInfo(skill: ISkill): SkillInfo = Utilities.getPlayerSkillInfo(player, skill)

    def getSkillInfo(id: ResourceLocation): SkillInfo = Utilities.getPlayerSkillInfo(player, id)

    def removeSkill(id: ResourceLocation): Boolean = Utilities.removePlayerSkill(player, id)

    def removeSkill(skill: ISkill): Boolean = Utilities.removePlayerSkill(player, skill)

    def sendSkillInfo(instance: SkillInstance, change: Int): Unit = Utilities.sendSkillInfo(player, instance, change)

    def clearSkillInfoCache(): Unit = Utilities.clearSkillInfoCache(player)

    def applySkillAttrs(): Unit = Utilities.applySkillAttrs(player)

    def syncSkills(): Unit = player match {
      case p: EntityPlayerMP => Utilities.syncSkills(p)
      case _ =>
    }
  }
}
