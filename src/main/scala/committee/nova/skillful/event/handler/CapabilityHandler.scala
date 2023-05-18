package committee.nova.skillful.event.handler

import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.impl.Skills
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, CapabilityManager}
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

object CapabilityHandler {
  @CapabilityInject(classOf[ISkills])
  def setCap(cap: Capability[ISkills]): Unit = skillfulCap = cap

  var skillfulCap: Capability[ISkills] = _
}

class CapabilityHandler {
  @SubscribeEvent
  def onSetup(e: FMLCommonSetupEvent): Unit = {
    e.enqueueWork(new Runnable {
      override def run(): Unit = CapabilityManager.INSTANCE.register(classOf[ISkills], new Skills.Storage, () => new Skills.Impl)
    })
  }
}
