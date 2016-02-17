package mod.steamnsteel.tileentity.debug;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by codew on 15/02/2016.
 */
public class DummySteamTransportTE extends TileEntity
{
    private float steamDensity;
    private float condensation;

    public float getSteamDensity()
    {
        return steamDensity;
    }

    public void setSteamDensity(float steamDensity)
    {
        this.steamDensity = steamDensity;
    }

    public float getCondensation()
    {
        return condensation;
    }

    public void setCondensation(float condensation)
    {
        this.condensation = condensation;
    }
}
