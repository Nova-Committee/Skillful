package committee.nova.skillful

import committee.nova.skillful.command.init.CommandHandler
import committee.nova.skillful.event.handler.{FMLEventHandler, ForgeEventHandler}
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.impl.Skills
import committee.nova.skillful.storage.SkillfulStorage
import committee.nova.skillful.storage.SkillfulStorage.{SkillRegisterEvent, SkillRelatedFoodRegisterEvent}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, CapabilityManager}
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import org.apache.logging.log4j.Logger

import java.util.concurrent.Callable

@Mod(modid = Skillful.MODID, useMetadata = true, modLanguage = "scala", acceptableRemoteVersions = "*")
object Skillful {
  final val MODID = "skillful"
  private var logger: Logger = _

  def getLogger: Logger = logger

  @CapabilityInject(classOf[ISkills])
  def setCap(cap: Capability[ISkills]): Unit = skillfulCap = cap

  var skillfulCap: Capability[ISkills] = _

  @EventHandler def preInit(e: FMLPreInitializationEvent): Unit = {
    logger = e.getModLog
    FMLEventHandler.init()
    ForgeEventHandler.init()
    CapabilityManager.INSTANCE.register(classOf[ISkills], new Skills.Storage, new Callable[ISkills] {
      override def call(): ISkills = new Skills.Impl
    })
    NetworkHandler.init(e)
  }

  @EventHandler def init(e: FMLInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS.post(new SkillRegisterEvent)
    SkillfulStorage.freezeSkillReg()
    MinecraftForge.EVENT_BUS.post(new SkillRelatedFoodRegisterEvent)
  }

  @EventHandler def serverStarting(e: FMLServerStartingEvent): Unit = CommandHandler.init(e)
}
