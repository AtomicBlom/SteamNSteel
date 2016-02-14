package mod.steamnsteel.plumbing.Impl;

import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.api.plumbing.ISteamTransportRegistry;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import java.util.concurrent.ConcurrentHashMap;

public class SteamTransportRegistry implements ISteamTransportRegistry
    {
        private final ConcurrentHashMap<SteamTransportLocation, SteamTransport> _steamTransports = new ConcurrentHashMap<>();

        public ISteamTransport registerSteamTransport(int x, int y, int z, World world, EnumFacing[] initialAllowedDirections)
        {
			SteamTransportLocation steamTransportLocation = SteamTransportLocation.create(x, y, z, world.provider.getDimensionId());
            SteamTransport result = _steamTransports.putIfAbsent(steamTransportLocation, new SteamTransport(steamTransportLocation));

			boolean[] allowedDirections = new boolean[6];

			for (EnumFacing initialAllowedDirection : initialAllowedDirections)
			{
				allowedDirections[initialAllowedDirection.ordinal()] = true;
			}

	        for (EnumFacing direction : EnumFacing.VALUES)
	        {
		        boolean canConnect = allowedDirections[direction.ordinal()];
		        result.setCanConnect(direction, canConnect);
	        }

	        TheMod.SteamTransportStateMachine.addTransport(result);
			return result;
        }

        public void destroySteamTransport(int x, int y, int z, World world)
        {
			SteamTransportLocation steamTransportLocation = SteamTransportLocation.create(x, y);

			final SteamTransport transport = _steamTransports.remove(steamTransportLocation);
			TheMod.SteamTransportStateMachine.removeTransport(transport);

        }

		public ISteamTransport getSteamTransportAtLocation(SteamTransportLocation steamTransportLocation)
		{
			SteamTransport value =_steamTransports.getOrDefault(steamTransportLocation, null);
			return value;
		}
	}
