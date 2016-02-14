package mod.steamnsteel.plumbing.Impl;

import com.google.common.collect.Maps;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.plumbing.Impl.Jobs.ProcessTransportJob;
import mod.steamnsteel.plumbing.Impl.Jobs.RegisterTransportJob;
import mod.steamnsteel.plumbing.Impl.Jobs.UnregisterTransportJob;
import mod.steamnsteel.plumbing.SteamNSteelConfiguration;
import mod.steamnsteel.utility.SteamNSteelException;
import net.minecraft.util.EnumFacing;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class SteamTransportStateMachine implements INotifyTransportJobComplete
	{
		private int currentTick;

		public SteamTransportStateMachine()
		{
			_steamNSteelConfiguration = new SteamNSteelConfiguration();
		}

		private Map<SteamTransportLocation, ProcessTransportJob> IndividualTransportJobs = Maps.newHashMap();
		private Map<ISteamTransport, SteamTransportTransientData> TransientData = Maps.newHashMap();
		private CyclicBarrier barrier = new CyclicBarrier(2);
		private SteamNSteelConfiguration _steamNSteelConfiguration;
		private AtomicInteger expectedJobs;
		private boolean expectingJobs;

		public void onTick()
		{
			currentTick++;
			processTransports();
		}

		private void processTransports()
		{
			if (expectedJobs.get() > 0)
			{
				throw new SteamNSteelException("Attempt to run a second tick with already outstanding jobs?");
			}
			final Collection<ProcessTransportJob> jobs = IndividualTransportJobs.values();
			if (jobs.isEmpty())
			{
				expectingJobs = false;
				return;
			}

			//FIXME: This might not be safe. Might need compare and set and/or a synchronized block.
			expectedJobs.set(jobs.size());
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
//				Console.WriteLine($"{currentTick} Waiting postTick");
				try
				{
					barrier.await();
				} catch (Exception e)
				{
					throw new SteamNSteelException(e);
				}
				//Console.WriteLine($"{TheMod.CurrentTick} finished postTick");
			}
		}

		private void finished()
		{
			//Console.WriteLine($"{TheMod.CurrentTick} Waiting PostJobs");
			try
			{
				barrier.await();
			} catch (Exception e)
			{
				throw new SteamNSteelException(e);
			}
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
				foundTransportJob = IndividualTransportJobs.get(altSteamTransportLocation);
				if (foundTransportJob == null) {
					continue;
				}

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
			if (expectedJobs.decrementAndGet() == 0)
			{
				finished();
			}
		}


		public int getCurrentTick()
		{
			return currentTick;
		}
	}

