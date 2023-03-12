package committee.nova.skillful.network.message

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class SkillsSyncMessage extends IMessage {
  private var tag: NBTTagCompound = new NBTTagCompound

  def getTag: NBTTagCompound = tag

  override def fromBytes(buf: ByteBuf): Unit = tag = ByteBufUtils.readTag(buf)

  override def toBytes(buf: ByteBuf): Unit = ByteBufUtils.writeTag(buf, tag)
}
