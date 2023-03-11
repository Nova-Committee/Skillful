package committee.nova.skillful.skills

import committee.nova.skillful.api.ISkill
import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.INBTSerializable

class SkillInstance(val skill: ISkill) extends INBTSerializable[NBTTagCompound] {
  private var currentXp: Int = 0

  private var currentLevel: Int = 0

  def getSkill: ISkill = skill

  def getCurrentXp: Int = currentXp

  def getCurrentLevel: Int = currentLevel

  def addXp(player: EntityPlayerMP, xp: Int): Unit = {
    val event = new SkillXpEvent.Pre(player, this, xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _addXp(player, event.getAmount)
  }

  def _addXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (xp == 0) return
    if (xp < 0) {
      _reduceXp(player, -xp)
      return
    }
    currentXp += xp
    while (currentXp >= skill.getLevelRequiredXp(currentLevel)) {
      currentXp -= skill.getLevelRequiredXp(currentLevel)
      currentLevel += 1
      if (currentLevel > skill.getMaxLevel) cheat()
      else MinecraftForge.EVENT_BUS.post(new SkillLevelEvent.Up(player, this, currentLevel))
    }
    MinecraftForge.EVENT_BUS.post(new SkillXpEvent.Post(player, this, xp))
  }

  def reduceXp(player: EntityPlayerMP, xp: Int): Unit = {
    val event = new SkillXpEvent.Pre(player, this, -xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _reduceXp(player, -event.getAmount)
  }

  def _reduceXp(player: EntityPlayerMP, xp: Int): Unit = {
    if (xp == 0) return
    if (xp < 0) {
      _addXp(player, -xp)
      return
    }
    currentXp -= xp
    while (currentXp < 0) {
      currentLevel -= 1
      currentXp += skill.getLevelRequiredXp(currentLevel)
      if (currentLevel < 0) clear()
      else MinecraftForge.EVENT_BUS.post(new SkillLevelEvent.Down(player, this, currentLevel))
    }
    MinecraftForge.EVENT_BUS.post(new SkillXpEvent.Post(player, this, -xp))
  }

  def clear(): Unit = {
    currentLevel = 0
    currentXp = 0
  }

  def cheat(): Unit = {
    currentLevel = skill.getMaxLevel
    currentXp = skill.getLevelRequiredXp(currentLevel) - 1
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
