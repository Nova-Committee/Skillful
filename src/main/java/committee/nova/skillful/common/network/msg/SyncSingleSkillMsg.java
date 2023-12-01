package committee.nova.skillful.common.network.msg;

import committee.nova.skillful.client.network.NetworkActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSingleSkillMsg {
    private final ResourceLocation id;
    private final long level;
    private final long xp;

    public SyncSingleSkillMsg(ResourceLocation id, long level, long xp) {
        this.id = id;
        this.level = level;
        this.xp = xp;
    }

    public SyncSingleSkillMsg(FriendlyByteBuf buf) {
        this.id = buf.readResourceLocation();
        this.level = buf.readLong();
        this.xp = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeLong(level);
        buf.writeLong(xp);
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> NetworkActions.syncSingleSkill(id, level, xp)));
    }
}
