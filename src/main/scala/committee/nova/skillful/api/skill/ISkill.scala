package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

object ISkill {
  final val EMPTY = new ISkill {
    override def getId: ResourceLocation = new ResourceLocation("dummy:dummy")

    override def getColor: BossInfo.Color = BossInfo.Color.WHITE

    override def getMaxLevel: Int = Int.MaxValue

    override def getLevelRequiredXp(level: Int): Int = Int.MaxValue
  }
}

trait ISkill {
  def getId: ResourceLocation

  def getColor: BossInfo.Color

  def getMaxLevel: Int

  def getLevelRequiredXp(level: Int): Int

  def shouldActOnLevelChange: Boolean = this.isInstanceOf[IActOnLevelChange]

  def actOnLevelChange(player: ServerPlayerEntity, instance: SkillInstance, isUp: Boolean): Unit = this match {
    case i: IActOnLevelChange => i.act(player, instance, isUp)
    case _ =>
  }

  def shouldCheckOnLogin: Boolean = this.isInstanceOf[ICheckOnLogin]

  def checkOnLogin(player: ServerPlayerEntity, instance: SkillInstance): Unit = this match {
    case i: ICheckOnLogin => i.check(player, instance)
    case _ =>
  }

  def shouldChangeXPAfterSleep: Boolean = this.isInstanceOf[IXPChangesAfterSleep]

  def changeXPAfterSleep(player: ServerPlayerEntity, instance: SkillInstance): Int = this match {
    case i: IXPChangesAfterSleep => i.change(player, instance)
    case _ => 0
  }

  def isDummy: Boolean = this.equals(ISkill.EMPTY)

  override def equals(obj: Any): Boolean = obj match {
    case that: ISkill => that.getId.equals(getId)
    case _ => false
  }

  override def hashCode(): Int = getId.hashCode()
}
