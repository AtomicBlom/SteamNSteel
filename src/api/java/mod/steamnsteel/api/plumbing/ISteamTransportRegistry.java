package mod.steamnsteel.api.plumbing;

import mod.steamnsteel.api.plumbing.ISteamTransport;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

//FIXME: Add reference to int z, int, worldId
public interface ISteamTransportRegistry
{
    ISteamTransport registerSteamTransport(int x, int y, int z, World world, EnumFacing[] enumFacing);

    void destroySteamTransport(int x, int y, int z, World world);
}