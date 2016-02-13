package mod.steamnsteel.plumbing.Impl;

public enum SteamMaths
{
	INSTANCE;


	public static double calculateMaximumSteam(double waterStored, double maximumWater, double maximumSteam)
	{
		return (1 - (waterStored / maximumWater)) * maximumSteam;
	}

	public static double calculateSteamDensity(double steamStored, double maximumSteam)
	{
		if (maximumSteam <= 0)
		{
			return steamStored > 0 ? 100 : 0;
		}

		double x = steamStored;
		double c = maximumSteam;
		double a = c / 100;
		double b = 100 / c;
		double y = Math.log10((x + a) * b) * 50;
		if (y > 100)
		{
			return 100;
		}
		if (y < 0)
		{
			return 0;
		}
		return y;
	}
}