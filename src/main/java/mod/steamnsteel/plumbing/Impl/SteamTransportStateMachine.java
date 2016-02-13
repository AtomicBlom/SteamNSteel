package mod.steamnsteel.plumbing.Impl;

import com.google.common.collect.Maps;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.plumbing.Impl.Jobs.ProcessTransportJob;
import mod.steamnsteel.plumbing.Impl.Jobs.RegisterTransportJob;
import mod.steamnsteel.plumbing.Impl.Jobs.UnregisterTransportJob;
import net.minecraft.util.EnumFacing;
import java.util.Collection;
import java.util.Map;

public class SteamTransportStateMachine implements INotifyTransportJobComplete
	{
		public SteamTransportStateMachine()
		{
			_steamNSteelConfiguration = new SteamNSteelConfiguration();
		}

		private Map<SteamTransportLocation, ProcessTransportJob> IndividualTransportJobs = Maps.newHashMap();
		private Map<ISteamTransport, SteamTransportTransientData> TransientData = Maps.newHashMap();
		private Barrier barrier = new Barrier(2);
		private SteamNSteelConfiguration _steamNSteelConfiguration;
		private int expectedJobs;
		private boolean expectingJobs;

		public void onTick()
		{
			processTransports();
		}

		private void processTransports()
		{
			if (expectedJobs > 0)
			{
				throw new InvalidOperationException("Attempt to run a second tick with already outstanding jobs?");
			}
			final Collection<ProcessTransportJob> jobs = IndividualTransportJobs.values();
			if (jobs.size() == 0)
			{
				expectingJobs = false;
				return;
			}

			expectedJobs = jobs.size();
			for(ProcessTransportJob job : jobs)
			{
				TheMod.JobManager.AddBackgroundJob(job);
			}

			expectingJobs = true;
		}

		public void postTick()
		{
			if (expectingJobs)
			{
//				Console.WriteLine($"{TheMod.CurrentTick} Waiting postTick");
				barrier.SignalAndWait();
				//Console.WriteLine($"{TheMod.CurrentTick} finished postTick");
			}
		}

		private void finished()
		{
			//Console.WriteLine($"{TheMod.CurrentTick} Waiting PostJobs");
			barrier.SignalAndWait();
			//Console.WriteLine($"{TheMod.CurrentTick} Released PostJobs");
		}

		public void addTransport(SteamTransport transport)
		{
			TheMod.JobManager.AddPreTickJob(new RegisterTransportJob(this, transport));
		}

		public void removeTransport(SteamTransport transport)
		{
			TheMod.JobManager.AddPreTickJob(new UnregisterTransportJob(this, transport));
		}

		public void addTransportInternal(SteamTransport transport)
		{
			SteamTransportLocation steamTransportLocation = transport.getTransportLocation();
			//Console.WriteLine($"{TheMod.CurrentTick} Adding Transport {steamTransportLocation}");
			TransientData.put(transport, new SteamTransportTransientData(transport));

			for (EnumFacing direction : EnumFacing.VALUES)
			{
				if (!transport.canConnect(direction)) continue;
				SteamTransportLocation altSteamTransportLocation = steamTransportLocation.offset(direction);
				
				ProcessTransportJob foundTransportJob;
                if (!IndividualTransportJobs.TryGetValue(altSteamTransportLocation, out foundTransportJob)) continue;
				SteamTransport foundTransport = foundTransportJob._transport;
				EnumFacing oppositeDirection = direction.getOpposite();
				if (!foundTransport.canConnect(oppositeDirection)) continue;

				transport.setAdjacentTransport(direction, foundTransport);
				foundTransport.setAdjacentTransport(oppositeDirection, transport);
			}

			IndividualTransportJobs.put(steamTransportLocation, new ProcessTransportJob(transport, this, _steamNSteelConfiguration));
		}

		public void removeTransportInternal(SteamTransport transport)
		{
			IndividualTransportJobs.remove(transport.getTransportLocation());
			TransientData.remove(transport);

			for (EnumFacing direction : EnumFacing.VALUES)
			{
				SteamTransport adjacentTransport = (SteamTransport)transport.getAdjacentTransport(direction);
				if (adjacentTransport == null) continue;

				adjacentTransport.setAdjacentTransport(direction.getOpposite(), null);
			}
		}

		public SteamTransportTransientData getJobDataForTransport(ISteamTransport processTransportJob)
		{
			return TransientData.get(processTransportJob);
		}

		public void jobComplete()
		{
			if (Interlocked.Decrement(ref expectedJobs) == 0)
			{
				finished();
			}
		}
	}

