package mod.steamnsteel.plumbing.Impl;

import mod.steamnsteel.TheMod;

public class SteamTransportTransientData
	{
		private Object lockObj = new Object();
		private int tickLastUpdated = 0;
		public SteamTransportTransientData(SteamTransport transport)
		{
			this.transport = transport;
		}

		public void verifyTick()
		{
			synchronized(lockObj)
			{
				if (tickLastUpdated != TheMod.SteamTransportStateMachine.getCurrentTick())
				{
					previousState.setSteamStored(transport.getSteamStored());
					previousState.setCondensationStored(transport.getWaterStored());
					previousState.setTemperature(transport.getTemperature());
					previousState.setMaximumCondensation(transport.getMaximumWater());
					previousState.setActualMaximumSteam(SteamMaths.calculateMaximumSteam(
						previousState.getCondensationStored(),
						transport.getMaximumWater(), 
						transport.getMaximumSteam()
					));
					previousState.setSteamDensity(SteamMaths.calculateSteamDensity(previousState.getSteamStored(), previousState.getActualMaximumSteam()));
					condensationAdded = 0;
					steamAdded = 0;
					tickLastUpdated = TheMod.SteamTransportStateMachine.getCurrentTick();
				}
			}
		}

		private final SteamTransport transport;
		private double condensationAdded;
		private double steamAdded;
		private final PreviousTransportState previousState = new PreviousTransportState();

		public PreviousTransportState getPreviousState()
		{
			return previousState;
		}

		public double takeSteam(double amount)
		{
			double amountTaken = transport.takeSteam(amount);
			//TODO: subtract from SteamAdded?
			return amountTaken;
		}

		public double takeCondensate(double amount)
		{
			double amountTaken = transport.takeCondensate(amount);
			//TODO: subtract from CondensationAdded?
			return amountTaken;
		}

		public void addCondensate(double waterGained)
		{
			transport.addCondensate(waterGained);
			condensationAdded += waterGained;
		}

		public void addSteam(double amount)
		{
			transport.addSteam(amount);
			steamAdded += amount;
		}

		public double getCondensationAdded()
		{
			return condensationAdded;
		}

		public double getSteamAdded()
		{
			return steamAdded;
		}

		public double getTemperature()
		{
				return transport.getTemperature();
		}

		public void setTemperature(double value) { 
			double temperature = value;
			if (temperature > 100)
			{
				temperature = 100;
			}
			if (temperature < 0)
			{
				temperature = 0;
			}
			transport.setTemperature(temperature);
		}

		public double getUsableSteam()
		{
			return previousState.getSteamStored() - steamAdded;
		}

		public double getUsableWater()
		{
			return previousState.getCondensationStored() - condensationAdded;
		}

		public boolean getDebug()
		{
			return transport.getShouldDebug();
		}

		public int getTickLastUpdated()
		{
			return tickLastUpdated;
		}

		public SteamTransport getTransport()
		{
			return transport;
		}
	}
