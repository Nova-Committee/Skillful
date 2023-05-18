package committee.nova.example.event.handler

import committee.nova.skillful.impl.skill.SkillBuilder
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

class ExampleEventHandler {
  final val TEST_SKILL = SkillBuilder
    .create(new ResourceLocation("example", "test"))
    .setColor(BossInfo.Color.GREEN)
    .setLevelRequiredXP(i => i * 200)
    .setMaxLevel(100)
    .build

  //@SubscribeEvent
  //def onSkillRegister(e: SkillRegisterEvent): Unit = {
  //  e.addSkill(TEST_SKILL)
  //}
}
