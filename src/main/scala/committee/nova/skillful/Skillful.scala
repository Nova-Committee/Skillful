package committee.nova.skillful

import committee.nova.skillful.event.handler.{FMLEventHandler, ForgeEventHandler}
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.player.capabilities.{ISkills, Skills}
import committee.nova.skillful.storage.SkillfulStorage
import committee.nova.skillful.storage.SkillfulStorage.SkillRegisterEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, CapabilityManager}
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import org.apache.logging.log4j.Logger

import java.util.concurrent.Callable

@Mod(modid = Skillful.MODID, useMetadata = true, modLanguage = "scala", acceptableRemoteVersions = "*")
object Skillful {
  final val MODID = "skillful"
  private var logger: Logger = _

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
    SkillfulStorage.freeze()
  }

  def getLogger: Logger = logger
}
