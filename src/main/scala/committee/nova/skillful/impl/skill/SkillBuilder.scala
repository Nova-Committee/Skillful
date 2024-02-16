package committee.nova.skillful.impl.skill

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.{ITextComponent, Style, TextComponentTranslation, TextFormatting}
import org.apache.logging.log4j.util.BiConsumer

import java.util.function.{BiFunction, IntFunction}

object SkillBuilder {
  def create(id: ResourceLocation): SkillBuilder = new SkillBuilder(id)
}

class SkillBuilder(val id: ResourceLocation) {
  private var maxLevel = 100
  private var required: Int => Int = i => i * 200
  private var color: TextFormatting = TextFormatting.WHITE
  private var shouldActOnLevelChange$: Boolean = false
  private var shouldCheckOnLogin$: Boolean = false
  private var shouldChangeXPAfterSleep$: Boolean = false
  private var change: (EntityPlayerMP, SkillInstance, Boolean) => Unit = (_, _, _) => {}
  private var check: (EntityPlayerMP, SkillInstance) => Unit = (_, _) => {}
  private var sleep: (EntityPlayerMP, SkillInstance) => Int = (_, _) => 0

  def setMaxLevel(maxLevel: Int): SkillBuilder = {
    this.maxLevel = maxLevel max 1
    this
  }

  def setLevelRequiredXP(required: Int => Int): SkillBuilder = {
    this.required = required
    this
  }

  def setLevelRequiredXP(required: IntFunction[Integer]): SkillBuilder = {
    this.required = i => required.apply(i)
    this
  }

  def setColor(color: TextFormatting): SkillBuilder = {
    this.color = color
    this
  }

  def actOnLevelChange(change: (EntityPlayerMP, SkillInstance, Boolean) => Unit): SkillBuilder = {
    this.shouldActOnLevelChange$ = true
    this.change = change
    this
  }

  trait LevelChange {
    def change(player: EntityPlayerMP, skill: SkillInstance, isUp: Boolean): Unit
  }

  def actOnLevelChange(change: LevelChange): SkillBuilder = actOnLevelChange((p, s, i) => change.change(p, s, i))

  def checkOnLogin(check: (EntityPlayerMP, SkillInstance) => Unit): SkillBuilder = {
    this.shouldCheckOnLogin$ = true
    this.check = check
    this
  }

  def checkOnLogin(check: BiConsumer[EntityPlayerMP, SkillInstance]): SkillBuilder = checkOnLogin((p, s) => check.accept(p, s))

  def changeXPAfterSleep(sleep: (EntityPlayerMP, SkillInstance) => Int): SkillBuilder = {
    this.shouldChangeXPAfterSleep$ = true
    this.sleep = sleep
    this
  }

  def changeXPAfterSleep(sleep: BiFunction[EntityPlayerMP, SkillInstance, Int]): SkillBuilder = changeXPAfterSleep((p, s) => sleep.apply(p, s))

  def build: ISkill = new ISkill {
    private var nameCache: ITextComponent = _

    override def getId: ResourceLocation = id

    override def getColor: TextFormatting = color

    override def getMaxLevel: Int = maxLevel

    override def getLevelRequiredXp(level: Int): Int = required.apply(level)

    override def shouldActOnLevelChange: Boolean = shouldActOnLevelChange$

    override def actOnLevelChange(player: EntityPlayerMP, instance: SkillInstance, isUp: Boolean): Unit = change.apply(player, instance, isUp)

    override def shouldChangeXPAfterSleep: Boolean = shouldChangeXPAfterSleep$

    override def changeXPAfterSleep(player: EntityPlayerMP, instance: SkillInstance): Int = sleep.apply(player, instance)

    override def shouldCheckOnLogin: Boolean = shouldCheckOnLogin$

    override def checkOnLogin(player: EntityPlayerMP, instance: SkillInstance): Unit = check.apply(player, instance)

    override def getName: ITextComponent = {
      if (nameCache == null) nameCache = new TextComponentTranslation(s"skill.${getId.getNamespace}.${getId.getPath}")
        .setStyle(new Style().setColor(getColor))
      nameCache
    }
  }
}