package committee.nova.skillful.network.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.network.message.{InfoClearMessage, SkillsSyncMessage}
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, SimpleNetworkWrapper}
import net.minecraftforge.fml.relauncher.Side

object NetworkHandler {
  val instance: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Skillful.MODID)

  var nextID: Int = 0

  def init(e: FMLPreInitializationEvent): Unit = {
    registerMessage(classOf[MessageHandler.SkillsSyncHandler], classOf[SkillsSyncMessage], Side.CLIENT)
    registerMessage(classOf[MessageHandler.InfoClearHandler], classOf[InfoClearMessage], Side.CLIENT)
  }

  def registerMessage[REQ <: IMessage, REPLY <: IMessage](msgHandler: Class[_ <: IMessageHandler[REQ, REPLY]], requestMsgType: Class[REQ], side: Side): Unit = {
    nextID += 1
    instance.registerMessage(msgHandler, requestMsgType, nextID, side)
  }
}
