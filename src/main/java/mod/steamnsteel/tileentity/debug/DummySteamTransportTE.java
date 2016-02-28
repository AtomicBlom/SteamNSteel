package mod.steamnsteel.tileentity.debug;

import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class DummySteamTransportTE extends TileEntity implements ITickable
{
    private ISteamTransport steamTransport;

    public double getSteamDensity()
    {
        return steamTransport.getSteamDensity();
    }

    public void addSteam(double amount)
    {
        steamTransport.addSteam(amount);
    }

    public double getCondensation()
    {
        return steamTransport.getWaterStored();
    }

    public void setCondensation(double amount)
    {
        steamTransport.addCondensate(amount);
    }

    @Override
    public void onLoad()
    {
        steamTransport = TheMod.SteamTransportRegistry.registerSteamTransport(pos, worldObj, EnumFacing.VALUES);
    }

    @Override
    public void onChunkUnload()
    {
        TheMod.SteamTransportRegistry.destroySteamTransport(pos, worldObj);
    }

    @Override
    public void update()
    {
        markDirty();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }
}
