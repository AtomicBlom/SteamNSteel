package mod.steamnsteel.plumbing.Jobs;

import mod.steamnsteel.utility.SteamNSteelException;

/**
 * Created by codew on 14/02/2016.
 */
public class CancelJobManagerJob implements IJob
{
    @Override
    public void execute()
    {
        throw new SteamNSteelException("This is a poison job and should never be executed");
    }
}
