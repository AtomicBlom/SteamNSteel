package mod.steamnsteel.plumbing.Jobs;

import mod.steamnsteel.utility.SteamNSteelException;

public class CancelJobManagerJob implements IJob
{
    @Override
    public void execute()
    {
        throw new SteamNSteelException("This is a poison job and should never be executed");
    }
}
