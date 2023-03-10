package committee.nova.skillful.skills

import committee.nova.skillful.Skillful
import net.minecraft.util.ResourceLocation

object DefaultSkills {
  val MELEE = new Skill(new ResourceLocation(Skillful.MODID, "melee"), 100, i => i * 200)
}
