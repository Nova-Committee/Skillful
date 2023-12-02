package committee.nova.skillful;

import com.mojang.logging.LogUtils;
import committee.nova.skillful.client.config.ClientConfig;
import committee.nova.skillful.common.network.handler.NetworkHandler;
import committee.nova.skillful.common.test.TestManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Skillful.MODID)
public class Skillful {
    public static final String MODID = "skillful";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Skillful() {
        if (!FMLEnvironment.production) MinecraftForge.EVENT_BUS.register(new TestManager());
        NetworkHandler.registerMsg();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CFG);
    }
}
