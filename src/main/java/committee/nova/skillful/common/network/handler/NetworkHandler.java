package committee.nova.skillful.common.network.handler;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.network.msg.SyncSingleSkillMsg;
import committee.nova.skillful.common.network.msg.SyncSkillsMsg;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel instance;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static void registerMsg() {
        instance = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Skillful.MODID, "msg"),
                () -> VERSION,
                VERSION::equals,
                VERSION::equals
        );
        instance.messageBuilder(SyncSingleSkillMsg.class, nextId())
                .encoder(SyncSingleSkillMsg::toBytes)
                .decoder(SyncSingleSkillMsg::new)
                .consumer(SyncSingleSkillMsg::handler)
                .add();
        instance.messageBuilder(SyncSkillsMsg.class, nextId())
                .encoder(SyncSkillsMsg::toBytes)
                .decoder(SyncSkillsMsg::new)
                .consumer(SyncSkillsMsg::handler)
                .add();
    }

    public static int nextId() {
        return id++;
    }

    public static SimpleChannel getInstance() {
        return instance;
    }
}
