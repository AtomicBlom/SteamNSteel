package mod.steamnsteel.plumbing.Impl;

import com.google.common.collect.Maps;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.api.plumbing.ISteamTransportRegistry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SteamTransportRegistry implements ISteamTransportRegistry
    {
        private final Map<SteamTransportLocation, SteamTransport> _steamTransports = Maps.newConcurrentMap();

        @Override
		public ISteamTransport registerSteamTransport(BlockPos blockPos, World world, EnumFacing[] initialAllowedDirections)
        {
			final SteamTransportLocation steamTransportLocation = SteamTransportLocation.create(blockPos, world.provider.getDimensionId());
			final SteamTransport result = _steamTransports.computeIfAbsent(steamTransportLocation, SteamTransport::new);

			final boolean[] allowedDirections = new boolean[6];

			for (final EnumFacing initialAllowedDirection : initialAllowedDirections)
			{
				allowedDirections[initialAllowedDirection.ordinal()] = true;
			}

	        for (final EnumFacing direction : EnumFacing.VALUES)
	        {
		        final boolean canConnect = allowedDirections[direction.ordinal()];
		        result.setCanConnect(direction, canConnect);
	        }

	        TheMod.SteamTransportStateMachine.addTransport(result);
			return result;
        }

        @Override
		public void destroySteamTransport(BlockPos pos, World world)
        {
			final SteamTransportLocation steamTransportLocation = SteamTransportLocation.create(pos, world.provider.getDimensionId());

			final SteamTransport transport = _steamTransports.remove(steamTransportLocation);
			TheMod.SteamTransportStateMachine.removeTransport(transport);

        }

		public ISteamTransport getSteamTransportAtLocation(SteamTransportLocation steamTransportLocation)
		{
			return _steamTransports.getOrDefault(steamTransportLocation, null);
		}
	}
