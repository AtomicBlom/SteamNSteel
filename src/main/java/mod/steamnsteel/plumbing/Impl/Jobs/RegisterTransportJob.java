package mod.steamnsteel.plumbing.Impl.Jobs;

import mod.steamnsteel.plumbing.Impl.SteamTransport;
import mod.steamnsteel.plumbing.Impl.SteamTransportStateMachine;
import mod.steamnsteel.plumbing.Jobs.IJob;

//FIXME: This is probably not required, and is only here to proxy creation requests to the tick thread.
	public class RegisterTransportJob implements IJob
	{
		private final SteamTransportStateMachine _steamTransportStateMachine;
		private final SteamTransport _transport;

		public RegisterTransportJob(SteamTransportStateMachine steamTransportStateMachine, SteamTransport transport)
		{
			_steamTransportStateMachine = steamTransportStateMachine;
			_transport = transport;
		}

		public void execute()
		{
			_steamTransportStateMachine.addTransportInternal(_transport);
		}
	}
