package mod.steamnsteel.entity.ai;

import mod.steamnsteel.entity.ISwarmer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AISwarmWander<T extends EntityCreature & ISwarmer> extends AISwarmBase<T>
{
    private final int chance;
    private final float speed;
    public double xPos;
    public double yPos;
    public double zPos;

    /**
     * An AI task that makes the entity only wander within its home region.
     * @param entity
     * @param speed
     */
    public AISwarmWander(T entity, int chance, float speed)
    {
        super(entity);
        this.chance = chance;
        this.speed = speed;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        if (entity.getSwarm() != null)
        if ((entity.getPosition().getX() >> 4) != entity.getSwarm().getHomeChunkCoord().getX() ||
                (entity.getPosition().getZ() >> 4) != entity.getSwarm().getHomeChunkCoord().getZ())
        {
            entity.setCustomNameTag("OUT OF BOUNDS " + entity.getSwarm().getHomeChunkCoord());
        }else
        {
            entity.setCustomNameTag("IN SWARM " + entity.getSwarm().getHomeChunkCoord());
        }

        if (entity.getNavigator().noPath() && entity.getRNG().nextInt(chance) == 0)
        {
            Vec3d target = findRandomTarget();
            if (target != null)
            {
                xPos = target.xCoord;
                yPos = target.yCoord;
                zPos = target.zCoord;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting()
    {
        return !entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting()
    {
        entity.getNavigator().tryMoveToXYZ(xPos, yPos, zPos, speed);
    }

    private Vec3d findRandomTarget()
    {
        if (entity.getSwarm() != null)
        {
            double x = (entity.getSwarm().getHomeChunkCoord().getX() * 16) + entity.getRNG().nextInt(16);
            double z = (entity.getSwarm().getHomeChunkCoord().getZ() * 16) + entity.getRNG().nextInt(16);
            double y = entity.getSwarm().getHomeBlockCoord().getY() + MathHelper.getRandomDoubleInRange(entity.getRNG(), -3, 3);

            return new Vec3d(x, y, z);
        }
        else
        {
            return RandomPositionGenerator.findRandomTarget(entity, 16, 4);
        }
    }
}
