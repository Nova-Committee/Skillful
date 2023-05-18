package committee.nova.skillful

import committee.nova.example.event.handler.ExampleEventHandler
import committee.nova.skillful.event.handler.{CapabilityHandler, ForgeEventHandler}
import committee.nova.skillful.event.impl.{SkillRegisterEvent, SkillRelatedFoodRegisterEvent}
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.scorge.lang.ScorgeModLoadingContext

class Skillful {
  if (!FMLEnvironment.production) MinecraftForge.EVENT_BUS.register(new ExampleEventHandler)
  MinecraftForge.EVENT_BUS.register(new ForgeEventHandler)
  ScorgeModLoadingContext.get.getModEventBus.register(new CapabilityHandler)
  NetworkHandler.registerMessage()
  MinecraftForge.EVENT_BUS.post(new SkillRegisterEvent)
  MinecraftForge.EVENT_BUS.post(new SkillRelatedFoodRegisterEvent)
  SkillfulStorage.setAfterInit()
}
