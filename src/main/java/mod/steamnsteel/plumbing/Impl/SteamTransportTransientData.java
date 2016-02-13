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
				if (tickLastUpdated != TheMod.CurrentTick)
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
					tickLastUpdated = TheMod.CurrentTick;
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

		public class PreviousTransportState
		{
			private double steamStored;
			private double temperature;
			private double condensationStored;
			private double maximumCondensation;
			private double actualMaximumSteam;
			private double steamDensity;

			public double getSteamStored()
			{
				return steamStored;
			}

			public void setSteamStored(double steamStored)
			{
				this.steamStored = steamStored;
			}

			public double getTemperature()
			{
				return temperature;
			}

			public void setTemperature(double temperature)
			{
				this.temperature = temperature;
			}

			public double getCondensationStored()
			{
				return condensationStored;
			}

			public void setCondensationStored(double condensationStored)
			{
				this.condensationStored = condensationStored;
			}

			public double getMaximumCondensation()
			{
				return maximumCondensation;
			}

			public void setMaximumCondensation(double maximumCondensation)
			{
				this.maximumCondensation = maximumCondensation;
			}

			public double getActualMaximumSteam()
			{
				return actualMaximumSteam;
			}

			public void setActualMaximumSteam(double actualMaximumSteam)
			{
				this.actualMaximumSteam = actualMaximumSteam;
			}

			public double getSteamDensity()
			{
				return steamDensity;
			}

			public void setSteamDensity(double steamDensity)
			{
				this.steamDensity = steamDensity;
			}
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