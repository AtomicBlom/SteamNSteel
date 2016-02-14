package mod.steamnsteel.plumbing.Impl;

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
