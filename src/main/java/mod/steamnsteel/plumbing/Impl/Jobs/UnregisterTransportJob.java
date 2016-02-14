package mod.steamnsteel.plumbing.Impl.Jobs;

import mod.steamnsteel.plumbing.Impl.SteamTransport;
import mod.steamnsteel.plumbing.Impl.SteamTransportStateMachine;
import mod.steamnsteel.plumbing.Jobs.IJob;

//FIXME: This is probably not required, and is only here to proxy creation requests to the tick thread.
	public class UnregisterTransportJob implements IJob
	{
		private final SteamTransportStateMachine steamTransportStateMachine;
		private final SteamTransport transport;

		public UnregisterTransportJob(SteamTransportStateMachine steamTransportStateMachine, SteamTransport transport)
		{
			this.steamTransportStateMachine = steamTransportStateMachine;
			this.transport = transport;
		}

		public void execute()
		{
			steamTransportStateMachine.removeTransportInternal(transport);
		}
	}
