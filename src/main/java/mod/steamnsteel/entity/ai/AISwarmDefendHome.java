package mod.steamnsteel.entity.ai;

import mod.steamnsteel.entity.ISwarmer;
import mod.steamnsteel.utility.position.ChunkCoord;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AISwarmDefendHome<T extends EntityCreature & ISwarmer> extends AISwarmTarget<T>
{
    //Defends against any enemy that enters into its range. TODO should this check be in SpiderSwarm instead? Means less calls
    public AISwarmDefendHome(T entity, int range)
    {
        super(entity, range);
    }

    @Override
    public boolean shouldExecute()
    {
        if (super.shouldExecute() && entity.getAttackTarget() == null)
        {
            final List<EntityPlayer> playerList = getNearbyThreats(entity.getSwarm().getHomeChunkCoord(), range);

            return (playerList != null && playerList.size() > 0);
        }

        return false;
    }

    @Override
    public void startExecuting()
    {
        final List<EntityPlayer> playerList = getNearbyThreats(entity.getSwarm().getHomeChunkCoord(), range);

        if (playerList != null)
        {
            final ChunkCoord homeChunk = entity.getSwarm().getHomeChunkCoord();
            final BlockPos worldBlockCoord = homeChunk.localToWorldCoords(entity.getSwarm().getHomeBlockCoord());

            //Get closest player. TODO should we have better threat determining? Look in Swarm
            EntityPlayer closestPlayer = null;
            double closestDistance = 0;
            for (EntityPlayer player : playerList) {
                double dis = player.getDistance(worldBlockCoord.getX(), worldBlockCoord.getY(), worldBlockCoord.getZ());
                if (closestPlayer == null || dis < closestDistance)
                {
                    closestPlayer = player;
                    closestDistance = dis;
                }
            }
            if (closestPlayer != null)
            {
                entity.getSwarm().setAttackTarget(closestPlayer);
            }
        }
        super.startExecuting();
    }
}
