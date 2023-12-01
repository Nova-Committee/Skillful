package committee.nova.skillful.common.network.msg;

import committee.nova.skillful.client.network.NetworkActions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillsMsg {
    private final CompoundTag tag;

    public SyncSkillsMsg(ListTag list) {
        final CompoundTag tag = new CompoundTag();
        tag.put("skills", list);
        this.tag = tag;
    }

    public SyncSkillsMsg(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> NetworkActions.syncSkills(tag.getList("skills", 10))));
    }
}
