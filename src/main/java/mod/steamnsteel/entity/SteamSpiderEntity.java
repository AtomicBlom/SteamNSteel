package mod.steamnsteel.entity;

import mod.steamnsteel.entity.ai.*;
import mod.steamnsteel.proxy.Proxies;
import mod.steamnsteel.utility.position.ChunkCoord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SteamSpiderEntity extends EntityMob implements ISwarmer, IRangedAttackMob
{
    public static final String NAME = "SSSteamSpider";

    public static final String SWARM_HOME = "swarmHome";

    private static final DataParameter<Byte> IS_HOSTILE = EntityDataManager.<Byte>createKey(SteamSpiderEntity.class, DataSerializers.BYTE);

    private Swarm swarm;

    public SteamSpiderEntity(World world)
    {
        super(world);
        //TODO Proper AI tasks
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAILeapAtTarget(this, 0.5F));
        tasks.addTask(1, new AISwarmReturnHome<>(this, 256, 1.2F, true));
        //tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        tasks.addTask(2, new AIRangeBurstAttack<>(this, 1.2D, 4F, 40, 1200));
        tasks.addTask(4, new AISwarmWander<>(this, 60, 1.0F));
        //tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        //tasks.addTask(8, new EntityAILookIdle(this));
        tasks.addTask(8, new AISwarmSeek<>(this, 0, 500, 100, 3, 1200, false)); //This should be removed if we want spiders to become "dumb" when their host is killed
        targetTasks.addTask(1, new AISwarmOnHurt<>(this));
        targetTasks.addTask(2, new AISwarmDefendHome<>(this, 16));
        setSize(0.35F, 0.8F);
        setRenderDistanceWeight(128F);
    }

    @Override
    protected void applyEntityAttributes()
    {
        //TODO attributes
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D); //Same speed as player walking
        //getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue();
        //getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(3.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        this.getDataManager().set(IS_HOSTILE, (byte) 0); //Hostile status
    }

    @Override
    public boolean isAIDisabled()
    {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        //Weird stuff to actually get us to face in the correct directions
        renderYawOffset = rotationYaw;
        rotationPitch = 0; //0 pitch cause we can't actually look "up"
        if (getAttackTarget() != null)
        {
            faceEntity(getAttackTarget(), 10F, 0F);
        }

        //Steam particles
        if (worldObj.isRemote)
        {
            //Position of point on a circle. Used to calculate where the exit pipes are for smoke/steam
            double rot = Math.toRadians(renderYawOffset - 90F + (rand.nextBoolean() ? 12F : -12F));
            double radius = 0.27D;
            double x = posX + (radius * Math.cos(rot));
            double z = posZ + (radius * Math.sin(rot));
            if (isInWater())
            {
                worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, posY + 0.61, z, 0, 0, 0);
            }
            else
            {
                Proxies.render.spawnParticle("smoke", worldObj, x, posY + 0.61, z, 0, 0, 0, 0.5F);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity p_70652_1_)
    {
        this.setLastAttacker(p_70652_1_);
        return false;
    }

    @Override
    public EntityLivingBase getAttackTarget()
    {
        EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
        return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
    }

    @Override
    public float getEyeHeight()
    {
        return 0.2F;
    }

    @Override
    protected boolean isValidLightLevel()
    {
        return true;
    }

    @Override
    public Swarm getSwarm()
    {
        return swarm;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setSwarm(Swarm swarm)
    {
        if (this.swarm != null)
        {
            swarm.removeEntity(this);
        }

        this.swarm = swarm;
        if (swarm != null)
        {
            swarm.addEntity(this);
        }
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompund)
    {
        super.writeToNBT(tagCompund);

        if (swarm != null)
            tagCompund.setIntArray(SWARM_HOME, new int[]{swarm.getHomeChunkCoord().getX(), swarm.getHomeChunkCoord().getZ()});
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund)
    {
        super.readFromNBT(tagCompund);

        if (tagCompund.hasKey(SWARM_HOME))
        {
            final int[] uChunkLoc = tagCompund.getIntArray(SWARM_HOME);
            final ChunkCoord chunk = ChunkCoord.of(uChunkLoc[0], uChunkLoc[1]);

            final Swarm<SteamSpiderEntity> swarm = SwarmManager.swarmManagers.get(this.worldObj).getSwarmAt(chunk, SteamSpiderEntity.class);

            if (swarm != null)
            {
                setSwarm(swarm);
                swarm.addEntity(this);
            }
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase targetEntity, float range)
    {
        if (worldObj.getTotalWorldTime() % 1 == 0)
        {
            //TODO reduce number of SteamProjectileEntity spawns. Two every two ticks is not ideal
            double rot;
            double radius = 0.325;
            SteamProjectileEntity steamProj = new SteamProjectileEntity(worldObj, this, 0.9F + MathHelper.randomFloatClamp(getRNG(), -0.2F, 0.1F));
            rot = Math.toRadians(renderYawOffset + 90F + 12F);
            steamProj.setPosition(posX + (radius * Math.cos(rot)), posY + getEyeHeight(), posZ + (radius * Math.sin(rot)));
            steamProj.motionX += MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
            steamProj.motionZ += MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
            worldObj.spawnEntityInWorld(steamProj);

            steamProj = new SteamProjectileEntity(worldObj, this, 0.9F + MathHelper.randomFloatClamp(getRNG(), -0.2F, 0.1F));
            rot = Math.toRadians(renderYawOffset + 90F - 12F);
            steamProj.setPosition(posX + (radius * Math.cos(rot)), posY + getEyeHeight(), posZ + (radius * Math.sin(rot)));
            steamProj.motionX += MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
            steamProj.motionZ += MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
            worldObj.spawnEntityInWorld(steamProj);
        }
    }
}
