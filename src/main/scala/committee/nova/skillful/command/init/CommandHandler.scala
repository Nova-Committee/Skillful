package committee.nova.skillful.command.init

import committee.nova.skillful.command.impl.SkillfulCommand
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

object CommandHandler {
  def init(e: FMLServerStartingEvent): Unit = e.registerServerCommand(new SkillfulCommand)
}
