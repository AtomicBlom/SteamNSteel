package mod.steamnsteel.tileentity;

import com.foudroyantfactotum.tool.structure.registry.StructureDefinition;
import mod.steamnsteel.tileentity.structure.SteamNSteelStructureTE;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

public class SpiderFactoryTE extends SteamNSteelStructureTE implements ITickable
{
    public static final String CURR_HEALTH = "health";

    public static final int MAX_HEALTH = 50;

    //Repair static values
    public static final int REPAIR_AMOUNT_PERCENT = 10;
    public static final int REPAIR_TIME = 200;
    public static final int REPAIR_COOLDOWN = 600;

    //Lockdown static values
    public static final int LOCKDOWN_TIME = 200;
    public static final int LOCKDOWN_COOLDOWN = 600;

    private float health = 50;

    //Repair values
    private int repairTime = -1;
    private float healthToRepair;

    //Lockdown values
    private int lockdownTime = -1;

    public SpiderFactoryTE()
    {
        //noop
    }

    public SpiderFactoryTE(StructureDefinition sd, EnumFacing orientation, boolean mirror)
    {
        super(sd, orientation, mirror);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        health = nbt.getFloat(CURR_HEALTH);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setFloat(CURR_HEALTH, health);
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            //Repair
            if (repairTime > -1) {
                //Check if we should continue repairing
                if (health >= MAX_HEALTH || repairTime >= REPAIR_TIME) {
                    repairTime = -REPAIR_COOLDOWN;
                }
                else {
                    repairTime++;
                    health = MathHelper.clamp_float(health + (healthToRepair / REPAIR_TIME), 0, MAX_HEALTH);
                }
            }
            else if (repairTime < -1) {
                repairTime++;
            }

            //Lockdown
            else if (lockdownTime > -1) {
                lockdownTime++;
            }
            else if (lockdownTime < -1) {
                lockdownTime++;
            }

            if (repairTime == -1 && health < (MAX_HEALTH / 2)) {
                //Begin repair phase
                healthToRepair = (MAX_HEALTH / 100F) * (REPAIR_AMOUNT_PERCENT * worldObj.getDifficulty().getDifficultyId());
                repairTime = 0;
                //worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, TheMod.MOD_ID + ":block.welding", 1.0F, worldObj.rand.nextFloat()  * 0.1F + 0.5F);
                //Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147675_a(new ResourceLocation(TheMod.MOD_ID, "block.welding"), xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F));

                Logger.info("Beginning repair phase for factory %s to repair %s health over %s ticks", pos, healthToRepair, repairTime);
            }
        }
    }
}
