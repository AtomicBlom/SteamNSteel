package mod.steamnsteel.plumbing.Impl.Jobs;

import com.google.common.collect.Lists;
import jline.internal.Log;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.plumbing.Impl.*;
import mod.steamnsteel.plumbing.Impl.PreviousTransportState;
import mod.steamnsteel.plumbing.Jobs.IJob;
import mod.steamnsteel.plumbing.SteamNSteelConfiguration;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.util.EnumFacing;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ProcessTransportJob implements IJob
{
    private final SteamTransport transport;
    private final INotifyTransportJobComplete notificationRecipient;
    private final List<SteamTransportTransientData> eligibleTransportData = Lists.newArrayList();
    private final SteamNSteelConfiguration config;
    private SteamTransportTransientData[] horizontalAdjacentTransports = null;
    private SteamTransportTransientData[] allAdjacentTransports = null;
    private SteamTransportTransientData transportData = null;
    private SteamTransportTransientData transportAbove = null;
    private SteamTransportTransientData transportBelow = null;

    public ProcessTransportJob(SteamTransport transport, INotifyTransportJobComplete notificationRecipient, SteamNSteelConfiguration config)
    {
        this.transport = transport;
        this.notificationRecipient = notificationRecipient;
        this.config = config;
    }

    @Override
    public void execute()
    {
        try
        {
            Log.info("Processing steamTransport at location " + transport.getTransportLocation());

            if (transportData == null || transport.StructureChanged)
            {
                updateLocalData();

                transport.StructureChanged = false;
            }

            transportData.verifyTick();

            transferSteam();
            calculateUnitHeat();
            transferWater();
            condenseSteam();
        } catch (Exception e)
        {
            //Console.WriteLine(e);
            Logger.warning("error executing update job", e);
        }

        notificationRecipient.jobComplete();
    }

    private void updateLocalData()
    {
        final SteamTransportStateMachine stateMachine = TheMod.SteamTransportStateMachine;
        final List<SteamTransportTransientData> adjacentTransports = Lists.newArrayList();

        SteamTransport adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.NORTH);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.EAST);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.SOUTH);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.WEST);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }

        horizontalAdjacentTransports = adjacentTransports.toArray(new SteamTransportTransientData[adjacentTransports.size()]);

        adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.UP);
        transportAbove = adjacentTransport == null ? null : stateMachine.getJobDataForTransport(adjacentTransport);
        if (transportAbove != null)
        {
            adjacentTransports.add(transportAbove);
        }
        adjacentTransport = (SteamTransport) transport.getAdjacentTransport(EnumFacing.DOWN);
        transportBelow = adjacentTransport == null ? null : stateMachine.getJobDataForTransport(adjacentTransport);
        if (transportBelow != null)
        {
            adjacentTransports.add(transportBelow);
        }

        allAdjacentTransports = adjacentTransports.toArray(new SteamTransportTransientData[adjacentTransports.size()]);
        transportData = stateMachine.getJobDataForTransport(transport);
    }

    private void condenseSteam()
    {
        final double usableSteam = transportData.getPreviousState().getSteamStored();

        final double newCondensation = usableSteam * config.CondensationRatePerTick * ((100 - transportData.getPreviousState().getTemperature()) / 100);
        final double takenCondensation = transportData.takeSteam(newCondensation);
        final double waterGained = takenCondensation * config.SteamToWaterRatio;
        transportData.addCondensate(waterGained);
    }

    private void calculateUnitHeat()
    {
        final double unitTemperature = transportData.getPreviousState().getTemperature();
        final double tempDifference = transportData.getPreviousState().getSteamDensity() - unitTemperature;

        final double temperature = unitTemperature + (transport.getHeatConductivity() * (tempDifference / 100));
        transportData.setTemperature(temperature);
    }

    private void transferSteam()
    {
        final double usableSteam = transportData.getPreviousState().getSteamStored();

        if (usableSteam <= 0) return;

        transferSteam(usableSteam);
    }

    private void transferSteam(double usableSteam)
    {
        eligibleTransportData.clear();
        double steamSpaceAvailable = 0;

        for (final SteamTransportTransientData neighbourUnit : allAdjacentTransports)
        {
            //Steam providers can always push?
            final double neighbourSteamStored = neighbourUnit.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = neighbourUnit.getPreviousState().getActualMaximumSteam();
            if (neighbourSteamStored < neighbourMaximumSteam && neighbourSteamStored < usableSteam)
            {
                eligibleTransportData.add(neighbourUnit);
                steamSpaceAvailable += (neighbourMaximumSteam - neighbourSteamStored);
            }
        }

        final double calculatedSteamDensity = transportData.getPreviousState().getSteamDensity();
        if (transportBelow != null && calculatedSteamDensity >= config.EQUILIBRIUM && transportBelow.getPreviousState().getSteamStored() < transportData.getPreviousState().getSteamStored())
        {
            final double neighbourSteamStored = transportBelow.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = transportBelow.getPreviousState().getActualMaximumSteam();
            if (neighbourSteamStored < neighbourMaximumSteam && neighbourSteamStored < usableSteam)
            {
                eligibleTransportData.add(transportBelow);
                steamSpaceAvailable += (neighbourMaximumSteam - neighbourSteamStored);
            }
        }

        double originalSteamStored = usableSteam;
        for (SteamTransportTransientData neighbourTransport : eligibleTransportData)
        {
            final double neighbourSteamStored = neighbourTransport.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = neighbourTransport.getPreviousState().getActualMaximumSteam();

            final double ratio = (neighbourMaximumSteam - neighbourSteamStored) / steamSpaceAvailable;

            double amountTransferred = originalSteamStored * ratio;

            if (neighbourSteamStored + amountTransferred > neighbourMaximumSteam)
            {
                amountTransferred = neighbourMaximumSteam - neighbourSteamStored;
            }

            amountTransferred = amountTransferred * config.TransferRatio;

            amountTransferred = transportData.takeSteam(amountTransferred);

            neighbourTransport.verifyTick();
            neighbourTransport.addSteam(amountTransferred);
        }
    }

    private void transferWater()
    {
        final double usableWater = transportData.getPreviousState().getCondensationStored();

        if (usableWater <= 0)
        {
            transferWaterFromHigherPoint();
            return;
        }
        //First, work on any units above
        if (transportBelow != null)
        {
            transferWaterBelow(usableWater);
        }

        if (usableWater > 0 && horizontalAdjacentTransports.length != 0)
        {
            transferWaterAcross(usableWater);
        }

        transferWaterFromHigherPoint();
    }

    private void transferWaterFromHigherPoint()
    {
        /*if (transportData.getDebug())
		{
		Console.WriteLine($"HERE! {transport.getTransportLocation()}");
		}*/

        if (transportBelow == null || !(transportData.getUsableSteam() < transportData.getPreviousState().getActualMaximumSteam()))
        {
            return;
        }
        final PreviousTransportState previousTransportState = transportBelow.getPreviousState();
        if (!(Math.abs(previousTransportState.getCondensationStored() - previousTransportState.getMaximumCondensation()) < 100))
        {
            return;
        }

        final Stack<SearchData> elementsToSearch = new Stack<>();
        final Set<SteamTransportLocation> visitedLocations = new HashSet<>();
        visitedLocations.add(transport.getTransportLocation());
        elementsToSearch.push(new SearchData(transportBelow.getTransport(), 1));
        SearchData candidate = null;
        Boolean validScenario = true;
        while (validScenario && !elementsToSearch.isEmpty())
        {
            final SearchData searchData = elementsToSearch.pop();

            final SteamTransport transport = searchData.Transport;
            final int depth = searchData.Depth;
            final SteamTransportLocation steamTransportLocation = transport.getTransportLocation();
            //Console.WriteLine($"Checking transport @ {steamTransportLocation} - {depth} - {transport.getShouldDebug()}");
            visitedLocations.add(steamTransportLocation);

            if (depth <= 0 && (candidate == null || depth < candidate.Depth))
            {
                if (searchData.Depth < 0 || (searchData.Depth == 0 && searchData.Transport.getWaterStored() >= (this.transport.getWaterStored() + 5)))
                {
                    candidate = searchData;
                }
            }

            for (EnumFacing direction : EnumFacing.VALUES)
            {
                final SteamTransport adjacentTransport = (SteamTransport) transport.getAdjacentTransport(direction);
                if (adjacentTransport != null && !visitedLocations.contains(adjacentTransport.getTransportLocation()))
                {
                    final SteamTransportTransientData steamTransportTransientData = TheMod.SteamTransportStateMachine.getJobDataForTransport(adjacentTransport);
                    final PreviousTransportState nextPreviousData = steamTransportTransientData.getPreviousState();

                    if ((direction == EnumFacing.EAST || direction == EnumFacing.WEST || direction == EnumFacing.NORTH ||
                            direction == EnumFacing.SOUTH) &&
                            nextPreviousData.getCondensationStored() < nextPreviousData.getMaximumCondensation() - 10)
                    {
                        validScenario = false;
                        break;
                    }

                    if (nextPreviousData.getCondensationStored() > 10)
                    {
                        final int newDepth = depth + direction.getDirectionVec().getY();
                        elementsToSearch.push(new SearchData(adjacentTransport, newDepth));
                    }
                }
            }
        }

        if (candidate != null)
        {
            //Console.WriteLine($"Updating from candidate {candidate.Transport.getTransportLocation()} - {candidate.Depth}");
            double condensate;
            if (candidate.Depth == 0)
            {
                condensate = candidate.Transport.getWaterStored() / 2;
                if (condensate > 100)
                {
                    condensate = 100;
                }
            } else
            {
                condensate = 100;
            }

            final double actualCondensate = candidate.Transport.takeCondensate(condensate);
            transportData.addCondensate(actualCondensate);
        }
    }

    private void transferWaterAcross(double waterUsedAtStart)
    {
        eligibleTransportData.clear();

        if (horizontalAdjacentTransports.length == 0)
        {
            return;
        }

        final int elementIndex = transportData.getTickLastUpdated() % horizontalAdjacentTransports.length;
        final SteamTransportTransientData nextTransport = horizontalAdjacentTransports[elementIndex];

        if (nextTransport == null)
        {
            return;
        }
        nextTransport.verifyTick();

        final double neighbourWaterStored = nextTransport.getPreviousState().getCondensationStored();
        final double neighbourMaximumWater = nextTransport.getPreviousState().getMaximumCondensation();
        if (neighbourWaterStored >= neighbourMaximumWater || !(neighbourWaterStored < waterUsedAtStart))
        {
            return;
        }

        final double waterStored = transportData.getPreviousState().getCondensationStored();
        if (neighbourWaterStored >= waterStored)
        {
            return;
        }

        final double desiredTransfer = (waterStored - neighbourWaterStored) / (horizontalAdjacentTransports.length + 1);
        for (SteamTransportTransientData steamTransportTransientData : horizontalAdjacentTransports)
        {
            final double takeCondensate = transportData.takeCondensate(desiredTransfer);
            steamTransportTransientData.addCondensate(takeCondensate);
        }
    }

    private void transferWaterBelow(double usableWater)
    {
        final double neighbourWaterStored = transportBelow.getPreviousState().getCondensationStored();
        final double neighbourMaximumWater = transportBelow.getPreviousState().getMaximumCondensation();

        if (!(neighbourWaterStored < neighbourMaximumWater)) return;

        double amountTransferred = usableWater;

        if (neighbourWaterStored + amountTransferred > neighbourMaximumWater)
        {
            amountTransferred = neighbourMaximumWater - neighbourWaterStored;
        }

        if (usableWater - amountTransferred < 0)
        {
            amountTransferred = usableWater;
        }

        amountTransferred = transportData.takeCondensate(amountTransferred);

        transportBelow.verifyTick();
        transportBelow.addCondensate(amountTransferred);
    }

    public SteamTransport getTransport()
    {
        return transport;
    }

    private class SearchData
    {
        public final SteamTransport Transport;
        public final int Depth;

        public SearchData(ISteamTransport transport, int depth)
        {
            Transport = (SteamTransport) transport;
            Depth = depth;
        }
    }
}
