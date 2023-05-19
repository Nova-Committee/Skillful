package committee.nova.skillful

import committee.nova.skillful.event.handler.{CommonEventHandler, ForgeEventHandler}
import committee.nova.skillful.impl.skill.SkillBuilder
import committee.nova.skillful.manager.SkillfulManager
import committee.nova.skillful.network.handler.NetworkHandler
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.scorge.lang.ScorgeModLoadingContext

class Skillful {
  MinecraftForge.EVENT_BUS.register(new ForgeEventHandler)
  ScorgeModLoadingContext.get.getModEventBus.register(new CommonEventHandler)
  if (!FMLEnvironment.production) SkillfulManager.addSkill(
    SkillBuilder.create(new ResourceLocation("example", "test"))
      .setMaxLevel(100)
      .setLevelRequiredXP(i => i * 200)
      .setColor(BossInfo.Color.WHITE)
      .build
  )
  NetworkHandler.registerMessage()
}
