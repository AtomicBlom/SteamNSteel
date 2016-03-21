package mod.steamnsteel.entity.ai;

import com.google.common.base.Predicate;
import mod.steamnsteel.entity.ISwarmer;
import mod.steamnsteel.utility.position.ChunkBlockCoord;
import mod.steamnsteel.utility.position.ChunkCoord;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

//TODO add mutex bits
abstract class AISwarmBase<T extends EntityLiving & ISwarmer> extends EntityAIBase
{
    public static final Predicate<EntityPlayer> selector = entity -> !entity.capabilities.isCreativeMode;
    protected T entity;

    public AISwarmBase(T entity)
    {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return !entity.isDead && entity.getSwarm() != null;
    }

    @SuppressWarnings("unchecked")
    protected List<EntityPlayer> getNearbyThreats(ChunkCoord homeChunk, int range) {
        ChunkBlockCoord homeBlockCoord = entity.getSwarm().getHomeBlockCoord();
        return entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class,
                AxisAlignedBB.fromBounds((homeChunk.getX() * 16) - range, homeBlockCoord.getY() - 16, (homeChunk.getZ() * 16) - range,
                        (homeChunk.getX() * 16) + 16 + range, homeBlockCoord.getY() + 16, (homeChunk.getZ() * 16) + 16 + range), selector);
    }
}
