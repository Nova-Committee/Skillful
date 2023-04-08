package committee.nova.skillful.impl.skill.instance

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.INBTSerializable

class SkillInstance(val skill: ISkill) extends INBTSerializable[NBTTagCompound] {
  private var currentXp: Int = 0

  private var currentLevel: Int = 0

  def getSkill: ISkill = skill

  def getCurrentXp: Int = currentXp

  def getCurrentLevel: Int = currentLevel

  def isAcquired: Boolean = getCurrentLevel > 0

  def isClueless: Boolean = !isAcquired && currentXp == 0

  def isCompleted: Boolean = getCurrentLevel >= getSkill.getMaxLevel

  def addXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (player.isFake) return
    val event = new SkillXpEvent.Pre(player, this, xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _addXp(player, event.getAmount)
  }

  private def _addXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (player.isFake) return
    if (xp == 0) return
    if (xp < 0) {
      _reduceXp(player, -xp)
      return
    }
    if (isCompleted) return
    currentXp += xp min (Int.MaxValue - currentXp)
    while (currentXp >= skill.getLevelRequiredXp(currentLevel)) {
      currentXp -= skill.getLevelRequiredXp(currentLevel)
      currentLevel += 1
      if (currentLevel > skill.getMaxLevel) complete()
      else MinecraftForge.EVENT_BUS.post(new SkillLevelEvent.Up(player, this, currentLevel))
    }
    MinecraftForge.EVENT_BUS.post(new SkillXpEvent.Post(player, this, xp))
    player.syncSkills()
  }

  def reduceXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (player.isFake) return
    val event = new SkillXpEvent.Pre(player, this, -xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _reduceXp(player, -event.getAmount)
  }

  private def _reduceXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (player.isFake) return
    if (xp == 0) return
    if (xp < 0) {
      _addXp(player, -xp)
      return
    }
    if (isClueless) return
    currentXp -= xp
    while (currentXp < 0) {
      currentLevel -= 1
      currentXp += skill.getLevelRequiredXp(currentLevel)
      if (currentLevel < 0) makeClueless()
      else MinecraftForge.EVENT_BUS.post(new SkillLevelEvent.Down(player, this, currentLevel))
    }
    MinecraftForge.EVENT_BUS.post(new SkillXpEvent.Post(player, this, -xp))
    player.syncSkills()
  }

  def clear(player: EntityPlayerMP): Unit = reduceXp(player, Int.MaxValue)

  def cheat(player: EntityPlayerMP): Unit = addXp(player, Int.MaxValue)

  def changeLevel(player: EntityPlayerMP, lvl: Int): Unit = {
    if (player.isFake) return
    val real = MathHelper.clamp(lvl, 0, skill.getMaxLevel)
    val old = getCurrentLevel
    currentLevel = real
    currentXp = 0
    if (old == real) return
    MinecraftForge.EVENT_BUS.post(if (real > old) new SkillLevelEvent.Up(player, this, real) else new SkillLevelEvent.Down(player, this, real))
  }

  private def makeClueless(): Unit = {
    currentXp = 0
    currentLevel = 0
  }

  private def complete(): Unit = {
    currentXp = 0
    currentLevel = skill.getMaxLevel
  }

  override def serializeNBT(): NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setString("skill", skill.getId.toString)
    tag.setInteger("level", currentLevel)
    tag.setInteger("xp", currentXp)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    currentLevel = tag.getInteger("level")
    currentXp = tag.getInteger("xp")
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: SkillInstance => that.skill.equals(skill)
    case _ => false
  }

  override def hashCode(): Int = skill.hashCode()
}
