package mod.steamnsteel.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpiderFactoryEntity extends EntityMob {

    public static final String NAME = "spiderFactory";
    private Swarm swarm;

    public SpiderFactoryEntity(World p_i1738_1_) {
        super(p_i1738_1_);
        setSize(0.8F, 1.5F);
        setRotationAndSize(EnumFacing.EAST);
    }

    @Override
    protected void applyEntityAttributes()
    {
        //TODO attributes
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(10000000D);
    }

    @Override
    public boolean isAIDisabled()
    {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        setRotationAndSize(EnumFacing.EAST);
    }

    private void setRotationAndSize(EnumFacing dir) {
        //TODO this kinda needs to be fixed :/
/*        this.boundingBox.maxX = this.boundingBox.minX + (width * (dir.offsetX + 1));
        this.boundingBox.maxZ = this.boundingBox.minZ + (width * (dir.offsetZ + 1));
        this.boundingBox.maxY = this.boundingBox.minY + height;*/
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        super.collideWithEntity(entity);
    }

    @Override
    public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_)
    {

    }
}
