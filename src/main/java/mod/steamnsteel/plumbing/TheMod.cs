using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Steam.API;
using SteamNSteel.Impl;
using SteamNSteel.Jobs;

namespace SteamNSteel
{
	public class TheMod
	{
		internal static SteamTransportRegistry SteamTransportRegistry;

		internal static JobManager JobManager;

		public static int CurrentTick { get; private set; }

		public static void OnSteamNSteelInitialized(SteamNSteelInitializedEvent evt)
		{
			SteamTransportRegistry = (SteamTransportRegistry) evt.GetSteamTransportRegistry();
			SteamTransportStateMachine = new SteamTransportStateMachine();
			JobManager = new JobManager();
			JobManager.Start();
        }

		public static void OnTick()
		{
			//TODO: Replace this with the world tick
			CurrentTick++;
			//TODO: Register JobManager for world tick events and handle this there.
			JobManager.DoPretickJobs();
			SteamTransportStateMachine.OnTick();
        }

		public static void PostTick()
		{
			SteamTransportStateMachine.PostTick();
		}

		public static void OnSteamNSteelShuttingDown()
		{
			JobManager.Stop();
		}
	}
}
