package mod.steamnsteel.plumbing.Impl;

import mod.steamnsteel.api.plumbing.ISteamTransport;
import net.minecraft.util.EnumFacing;

public class SteamTransport implements ISteamTransport
    {
        private final SteamTransportLocation _steamTransportLocation;

	    public SteamTransport(SteamTransportLocation steamTransportLocation)
        {
            _steamTransportLocation = steamTransportLocation;
            _maximumSteam = 1000;
            _maximumWater = 800;
        }

        private double _waterStored = 0;
        private double _steamStored = 0;

        private double _temperature;
	    private double _heatConductivity;

        private double _maximumWater;
        private double _maximumSteam;

        final ISteamTransport[] _adjacentTransports = new ISteamTransport[6];
		final boolean[] _canConnect = new boolean[6];

        private boolean _debug;
	    public boolean StructureChanged;

	    public void addSteam(double unitsOfSteam)
        {
            if (_steamStored + unitsOfSteam >= _maximumSteam)
            {   
                _steamStored = _maximumSteam;
                return;
            }

            _steamStored += unitsOfSteam;
        }

        public void addCondensate(double unitsOfWater)
        {
            if (_waterStored + unitsOfWater >= _maximumWater)
            {
                _waterStored = _maximumWater;
                return;
            }

            _waterStored += unitsOfWater;
        }

        public double takeSteam(double desiredUnitsOfSteam)
        {
	        if (_steamStored <= 0)
	        {
		        _steamStored = 0;
		        return 0;
	        }
            if (desiredUnitsOfSteam <= _steamStored)
            {
                _steamStored -= desiredUnitsOfSteam;
                return desiredUnitsOfSteam;
            }

			double actualUnitsOfSteam = _steamStored;
            _steamStored = 0;
            return actualUnitsOfSteam;
        }

        public double takeCondensate(double desiredUnitsOfWater)
        {
	        if (_waterStored <= 0)
	        {
		        _waterStored = 0;
		        return 0;
	        }

            if (desiredUnitsOfWater <= _waterStored)
            {
                _waterStored -= desiredUnitsOfWater;
                return desiredUnitsOfWater;
            }

			double actualUnitsOfSteam = _waterStored;
            _waterStored = 0;
            return actualUnitsOfSteam;
        }

        public void setMaximumSteam(double maximumUnitsOfSteam)
        {
            _maximumSteam = maximumUnitsOfSteam;
        }

        public void setMaximumCondensate(double maximimUnitsOfWater)
        {
            _maximumWater = maximimUnitsOfWater;
        }

        public void toggleDebug()
        {
            _debug = !_debug;
        }

        public boolean getShouldDebug()
        {
            return _debug;
        }

        public double getSteamStored()
        {
            return _steamStored;
        }

        public double getWaterStored()
        {
            return _waterStored;
        }

        public double getMaximumWater()
        {
            return _maximumWater;
        }

        public double getMaximumSteam()
        {
            return _maximumSteam;
        }

        public double getTemperature()
        {
            return _temperature;
        }

		public void setTemperature(double temperature)
		{
			_temperature = temperature;
		}

		public double getHeatConductivity()
		{
			return _heatConductivity;
		}
		
        public void setCanConnect(EnumFacing direction, boolean canConnect)
        {
            _canConnect[direction.ordinal()] = canConnect;
        }

        public boolean canConnect(EnumFacing direction)
        {
            return _canConnect[direction.ordinal()];
        }

        public void setAdjacentTransport(EnumFacing direction, ISteamTransport transport)
        {
            if (canConnect(direction))

            _adjacentTransports[direction.ordinal()] = transport;
	        StructureChanged = true;
        }

        public ISteamTransport getAdjacentTransport(EnumFacing direction)
        {
            return _adjacentTransports[direction.ordinal()];
        }

        public boolean canTransportAbove()
        {
            return _adjacentTransports[EnumFacing.UP.ordinal()] != null;
        }

        public boolean canTransportBelow()
        {
            return _adjacentTransports[EnumFacing.DOWN.ordinal()] != null;
        }

		@Deprecated
        public boolean canTransportWest()
        {
            return _adjacentTransports[EnumFacing.WEST.ordinal()] != null;
        }

		@Deprecated
		public boolean canTransportEast()
        {
            return _adjacentTransports[EnumFacing.EAST.ordinal()] != null;
        }

        public SteamTransportLocation getTransportLocation()
        {
            return _steamTransportLocation;
        }
    }
