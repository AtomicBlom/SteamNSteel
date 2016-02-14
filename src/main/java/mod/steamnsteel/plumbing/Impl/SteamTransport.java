package mod.steamnsteel.plumbing.Impl;

import mod.steamnsteel.api.plumbing.ISteamTransport;
import net.minecraft.util.EnumFacing;

public class SteamTransport implements ISteamTransport
    {
        private final SteamTransportLocation steamTransportLocation;

	    public SteamTransport(SteamTransportLocation steamTransportLocation)
        {
            this.steamTransportLocation = steamTransportLocation;
            maximumSteam = 1000;
            maximumWater = 800;
        }

        private double waterStored = 0;
        private double steamStored = 0;

        private double temperature;
	    private double heatConductivity;

        private double maximumWater;
        private double maximumSteam;

        final ISteamTransport[] adjacentTransports = new ISteamTransport[6];
		final boolean[] canDirectionConnect = new boolean[6];

        private boolean debug;
	    public boolean StructureChanged;

	    public void addSteam(double unitsOfSteam)
        {
            if (steamStored + unitsOfSteam >= maximumSteam)
            {   
                steamStored = maximumSteam;
                return;
            }

            steamStored += unitsOfSteam;
        }

        public void addCondensate(double unitsOfWater)
        {
            if (waterStored + unitsOfWater >= maximumWater)
            {
                waterStored = maximumWater;
                return;
            }

            waterStored += unitsOfWater;
        }

        public double takeSteam(double desiredUnitsOfSteam)
        {
	        if (steamStored <= 0)
	        {
		        steamStored = 0;
		        return 0;
	        }
            if (desiredUnitsOfSteam <= steamStored)
            {
                steamStored -= desiredUnitsOfSteam;
                return desiredUnitsOfSteam;
            }

			double actualUnitsOfSteam = steamStored;
            steamStored = 0;
            return actualUnitsOfSteam;
        }

        public double takeCondensate(double desiredUnitsOfWater)
        {
	        if (waterStored <= 0)
	        {
		        waterStored = 0;
		        return 0;
	        }

            if (desiredUnitsOfWater <= waterStored)
            {
                waterStored -= desiredUnitsOfWater;
                return desiredUnitsOfWater;
            }

			double actualUnitsOfSteam = waterStored;
            waterStored = 0;
            return actualUnitsOfSteam;
        }

        public void setMaximumSteam(double maximumUnitsOfSteam)
        {
            maximumSteam = maximumUnitsOfSteam;
        }

        public void setMaximumCondensate(double maximimUnitsOfWater)
        {
            maximumWater = maximimUnitsOfWater;
        }

        public void toggleDebug()
        {
            debug = !debug;
        }

        public boolean getShouldDebug()
        {
            return debug;
        }

        public double getSteamStored()
        {
            return steamStored;
        }

        public double getWaterStored()
        {
            return waterStored;
        }

        public double getMaximumWater()
        {
            return maximumWater;
        }

        public double getMaximumSteam()
        {
            return maximumSteam;
        }

        public double getTemperature()
        {
            return temperature;
        }

		public void setTemperature(double temperature)
		{
			this.temperature = temperature;
		}

		public double getHeatConductivity()
		{
			return heatConductivity;
		}
		
        public void setCanConnect(EnumFacing direction, boolean canConnect)
        {
            canDirectionConnect[direction.ordinal()] = canConnect;
        }

        public boolean canConnect(EnumFacing direction)
        {
            return canDirectionConnect[direction.ordinal()];
        }

        public void setAdjacentTransport(EnumFacing direction, ISteamTransport transport)
        {
            if (canConnect(direction))

            adjacentTransports[direction.ordinal()] = transport;
	        StructureChanged = true;
        }

        public ISteamTransport getAdjacentTransport(EnumFacing direction)
        {
            return adjacentTransports[direction.ordinal()];
        }

        public boolean canTransportAbove()
        {
            return adjacentTransports[EnumFacing.UP.ordinal()] != null;
        }

        public boolean canTransportBelow()
        {
            return adjacentTransports[EnumFacing.DOWN.ordinal()] != null;
        }

		@Deprecated
        public boolean canTransportWest()
        {
            return adjacentTransports[EnumFacing.WEST.ordinal()] != null;
        }

		@Deprecated
		public boolean canTransportEast()
        {
            return adjacentTransports[EnumFacing.EAST.ordinal()] != null;
        }

        public SteamTransportLocation getTransportLocation()
        {
            return steamTransportLocation;
        }
    }
