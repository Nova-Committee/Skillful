package committee.nova.skillful.common.skill;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.network.handler.NetworkHandler;
import committee.nova.skillful.common.network.msg.SyncSingleSkillMsg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

public interface ISkillRecord extends INBTSerializable<CompoundTag> {

    ResourceLocation getId();

    long getLevel();

    long getXp();

    void setLevel(long level);

    void setXp(long xp);

    @Override
    default CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putString("skill", getId().toString());
        tag.putLong("level", getLevel());
        tag.putLong("xp", getXp());
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
        if (!tag.getString("skill").equals(getId().toString())) {
            Skillful.LOGGER.warn("Trying to deserialize on a mismatched SkillInstance.");
            return;
        }
        setLevel(tag.getLong("level"));
        setXp(tag.getLong("xp"));
    }

    default void syncToClient(ServerPlayer player) {
        NetworkHandler.getInstance().send(PacketDistributor.PLAYER.with(() -> player),
                new SyncSingleSkillMsg(getId(), getLevel(), getXp()));
    }
}
