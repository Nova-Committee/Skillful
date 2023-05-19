package committee.nova.skillful.network.message

import committee.nova.skillful.player.capabilities.impl.Skills.skillfulCap
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent

import java.util.function.Supplier

class SkillsSyncMessage {
  private var tag: CompoundNBT = new CompoundNBT

  def getTag: CompoundNBT = tag

  def this(buffer: PacketBuffer) {
    this()
    tag = buffer.readCompoundTag()
  }

  def toBytes(buffer: PacketBuffer): Unit = {
    buffer.writeCompoundTag(tag)
  }

  def handler(ctx: Supplier[NetworkEvent.Context]): Unit = {
    val c = ctx.get()
    c.enqueueWork(() => {
      val tag = getTag.getCompound("skills")
      Minecraft.getInstance().player.getCapability(skillfulCap).ifPresent(c => {
        val storage = skillfulCap.getStorage
        storage.readNBT(skillfulCap, c, null, tag)
      })
    })
    c.setPacketHandled(true)
  }
}
