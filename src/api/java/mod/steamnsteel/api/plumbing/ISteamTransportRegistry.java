package mod.steamnsteel.api.plumbing;

import mod.steamnsteel.api.plumbing.ISteamTransport;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

//FIXME: Add reference to int z, int, worldId
public interface ISteamTransportRegistry
{
    ISteamTransport registerSteamTransport(BlockPos pos, World world, EnumFacing[] enumFacing);

    void destroySteamTransport(BlockPos pos, World world);
}