package committee.nova.skillful.player.capabilities

import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.api.{ISkill, SkillInstance}
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.nbt.{NBTBase, NBTTagCompound, NBTTagList}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet
import java.util.function.Predicate
import scala.collection.JavaConverters._

object Skills {
  class Provider extends ICapabilitySerializable[NBTTagCompound] {
    private val instance = new Impl

    private val storage = new Storage

    override def serializeNBT(): NBTTagCompound = {
      val tag = new NBTTagCompound
      tag.setTag("skillful", storage.writeNBT(skillfulCap, instance, null))
      tag
    }

    override def deserializeNBT(nbt: NBTTagCompound): Unit = storage.readNBT(skillfulCap, instance, null, nbt.getTagList("skillful", 10))

    override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = capability == skillfulCap

    override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = if (capability == skillfulCap) skillfulCap.cast(instance) else null.asInstanceOf[T]
  }

  class Storage extends IStorage[ISkills] {
    override def writeNBT(capability: Capability[ISkills], instance: ISkills, side: EnumFacing): NBTBase = {
      val tag = new NBTTagList
      instance.getSkills.asScala.foreach(s => tag.appendTag(s.serializeNBT()))
      tag
    }

    override def readNBT(capability: Capability[ISkills], instance: ISkills, side: EnumFacing, nbt: NBTBase): Unit = {
      nbt match {
        case tag: NBTTagList => tag.asScala.foreach(n => {
          val t = n.asInstanceOf[NBTTagCompound]
          val skill = SkillfulStorage.getSkill(new ResourceLocation(t.getString("skill")))
          val i = new SkillInstance(skill, instance.getUUID)
          i.deserializeNBT(t)
          instance.getSkills.removeIf(new Predicate[SkillInstance] {
            override def test(t: SkillInstance): Boolean = t.getSkill.equals(skill)
          })
          instance.getSkills.add(i)
        })
        case _ =>
      }
    }
  }

  class Impl extends ISkills {
    private val skills: CopyOnWriteArraySet[SkillInstance] = new CopyOnWriteArraySet[SkillInstance]()

    private val skillInfos: CopyOnWriteArraySet[SkillInfo] = new CopyOnWriteArraySet[SkillInfo]()

    private var uuid: UUID = _

    override def getUUID: UUID = uuid

    override def setUUID(uuid: UUID): Unit = this.uuid = uuid

    override def getSkills: CopyOnWriteArraySet[SkillInstance] = skills

    override def getSkill(skill: ISkill): SkillInstance = {
      skills.asScala.foreach(s => if (s.getSkill.equals(skill)) return s)
      val instance = new SkillInstance(skill, uuid)
      skills.add(instance)
      instance
    }

    override def getSkillInfos: CopyOnWriteArraySet[SkillInfo] = skillInfos

    override def getSkillInfo(id: ResourceLocation): SkillInfo = {
      skillInfos.asScala.foreach(i => if (i.getId.equals(id)) return i)
      val instance = new SkillInfo(id)
      skillInfos.add(instance)
      instance
    }
  }
}
