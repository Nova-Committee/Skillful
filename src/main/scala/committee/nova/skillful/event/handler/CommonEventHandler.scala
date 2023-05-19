package committee.nova.skillful.event.handler

import committee.nova.skillful.manager.SkillfulManager
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.impl.Skills
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent

class CommonEventHandler {
  @SubscribeEvent
  def onSetup(e: FMLCommonSetupEvent): Unit = {
    e.enqueueWork(new Runnable {
      override def run(): Unit = CapabilityManager.INSTANCE.register(classOf[ISkills], new Skills.Storage, () => new Skills.Impl)
    })
  }

  @SubscribeEvent
  def onLoaded(e: FMLServerAboutToStartEvent): Unit = SkillfulManager.setAfterInit()
}
