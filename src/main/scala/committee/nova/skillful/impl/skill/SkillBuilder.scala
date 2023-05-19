package committee.nova.skillful.impl.skill

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo
import org.apache.logging.log4j.util.BiConsumer

import java.util.function.{BiFunction, IntFunction}

object SkillBuilder {
  /**
   * @param id The skill's identifier. Characters allowed: 0-9 a-z A-Z _
   * @return A SkillBuilder instance.
   */
  def create(id: ResourceLocation): SkillBuilder = new SkillBuilder(id)
}

class SkillBuilder(val id: ResourceLocation) {
  private var maxLevel = 100
  private var required: Int => Int = i => i * 200
  private var color: BossInfo.Color = BossInfo.Color.PINK
  private var shouldActOnLevelChange$: Boolean = false
  private var shouldCheckOnLogin$: Boolean = false
  private var shouldChangeXPAfterSleep$: Boolean = false
  private var change: (ServerPlayerEntity, SkillInstance, Boolean) => Unit = (_, _, _) => {}
  private var check: (ServerPlayerEntity, SkillInstance) => Unit = (_, _) => {}
  private var sleep: (ServerPlayerEntity, SkillInstance) => Int = (_, _) => 0

  def setMaxLevel(maxLevel: Int): SkillBuilder = {
    this.maxLevel = maxLevel max 1
    this
  }

  def setLevelRequiredXP(required: Int => Int): SkillBuilder = {
    this.required = required
    this
  }

  def setLevelRequiredXP(required: IntFunction[java.lang.Integer]): SkillBuilder = setLevelRequiredXP(i => required.apply(i))

  def setColor(color: BossInfo.Color): SkillBuilder = {
    this.color = color
    this
  }

  def actOnLevelChange(change: (ServerPlayerEntity, SkillInstance, Boolean) => Unit): SkillBuilder = {
    this.shouldActOnLevelChange$ = true
    this.change = change
    this
  }

  trait LevelChange {
    def change(player: ServerPlayerEntity, skill: SkillInstance, isUp: Boolean): Unit
  }

  def actOnLevelChange(change: LevelChange): SkillBuilder = actOnLevelChange((p, s, i) => change.change(p, s, i))

  def checkOnLogin(check: (ServerPlayerEntity, SkillInstance) => Unit): SkillBuilder = {
    this.shouldCheckOnLogin$ = true
    this.check = check
    this
  }

  def checkOnLogin(check: BiConsumer[ServerPlayerEntity, SkillInstance]): SkillBuilder = checkOnLogin((p, s) => check.accept(p, s))

  def changeXPAfterSleep(sleep: (ServerPlayerEntity, SkillInstance) => Int): SkillBuilder = {
    this.shouldChangeXPAfterSleep$ = true
    this.sleep = sleep
    this
  }

  def changeXPAfterSleep(sleep: BiFunction[ServerPlayerEntity, SkillInstance, Int]): SkillBuilder = changeXPAfterSleep((p, s) => sleep.apply(p, s))

  def build: ISkill = new ISkill {
    override def getId: ResourceLocation = id

    override def getColor: BossInfo.Color = color

    override def getMaxLevel: Int = maxLevel

    override def getLevelRequiredXp(level: Int): Int = required.apply(level)

    override def shouldActOnLevelChange: Boolean = shouldActOnLevelChange$

    override def actOnLevelChange(player: ServerPlayerEntity, instance: SkillInstance, isUp: Boolean): Unit = change.apply(player, instance, isUp)

    override def shouldChangeXPAfterSleep: Boolean = shouldChangeXPAfterSleep$

    override def changeXPAfterSleep(player: ServerPlayerEntity, instance: SkillInstance): Int = sleep.apply(player, instance)

    override def shouldCheckOnLogin: Boolean = shouldCheckOnLogin$

    override def checkOnLogin(player: ServerPlayerEntity, instance: SkillInstance): Unit = check.apply(player, instance)
  }
}
