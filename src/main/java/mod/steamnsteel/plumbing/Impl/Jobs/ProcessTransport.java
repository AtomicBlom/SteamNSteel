package mod.steamnsteel.plumbing.Impl.Jobs;

import com.google.common.collect.Lists;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.api.plumbing.ISteamTransport;
import mod.steamnsteel.plumbing.Impl.*;
import mod.steamnsteel.plumbing.Impl.SteamTransportTransientData.PreviousTransportState;
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
    public final SteamTransport _transport;
    private final INotifyTransportJobComplete _notificationRecipient;
    private final List<SteamTransportTransientData> _eligibleTransportData = Lists.newArrayList();
    private final SteamNSteelConfiguration _config;
    private SteamTransportTransientData[] _horizontalAdjacentTransports;
    private SteamTransportTransientData[] _allAdjacentTransports;
    private SteamTransportTransientData _transportData;
    private SteamTransportTransientData _transportAbove;
    private SteamTransportTransientData _transportBelow;

    public ProcessTransportJob(SteamTransport transport, INotifyTransportJobComplete notificationRecipient, SteamNSteelConfiguration config)
    {
        _transport = transport;
        _notificationRecipient = notificationRecipient;
        _config = config;
    }

    public void execute()
    {
        try
        {
            if (_transportData == null || _transport.StructureChanged)
            {
                updateLocalData();

                _transport.StructureChanged = false;
            }

            _transportData.verifyTick();

            transferSteam();
            calculateUnitHeat();
            transferWater();
            condenseSteam();
        } catch (Exception e)
        {
            //Console.WriteLine(e);
            Logger.warning("error executing update job", e);
        }

        _notificationRecipient.jobComplete();
    }

    private void updateLocalData()
    {
        final SteamTransportStateMachine stateMachine = TheMod.SteamTransportStateMachine;
        final List<SteamTransportTransientData> adjacentTransports = Lists.newArrayList();

        SteamTransport adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.NORTH);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.EAST);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.SOUTH);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.WEST);
        if (adjacentTransport != null)
        {
            adjacentTransports.add(stateMachine.getJobDataForTransport(adjacentTransport));
        }
        _horizontalAdjacentTransports = adjacentTransports.ToArray();

        adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.UP);
        _transportAbove = adjacentTransport == null ? null : stateMachine.getJobDataForTransport(adjacentTransport);
        if (_transportAbove != null)
        {
            adjacentTransports.add(_transportAbove);
        }
        adjacentTransport = (SteamTransport) _transport.getAdjacentTransport(EnumFacing.DOWN);
        _transportBelow = adjacentTransport == null ? null : stateMachine.getJobDataForTransport(adjacentTransport);
        if (_transportBelow != null)
        {
            adjacentTransports.add(_transportBelow);
        }

        _allAdjacentTransports = adjacentTransports.ToArray();
        _transportData = stateMachine.getJobDataForTransport(_transport);
    }

    private void condenseSteam()
    {
        final double usableSteam = _transportData.getPreviousState().getSteamStored();

        final double newCondensation = usableSteam * _config.CondensationRatePerTick * ((100 - _transportData.getPreviousState().getTemperature()) / 100);
        final double takenCondensation = _transportData.takeSteam(newCondensation);
        final double waterGained = takenCondensation * _config.SteamToWaterRatio;
        _transportData.addCondensate(waterGained);
    }

    private void calculateUnitHeat()
    {
        final double unitTemperature = _transportData.getPreviousState().getTemperature();
        final double tempDifference = _transportData.getPreviousState().getSteamDensity() - unitTemperature;

        final double temperature = unitTemperature + (_transport.getHeatConductivity() * (tempDifference / 100));
        _transportData.setTemperature(temperature);
    }

    private void transferSteam()
    {
        final double usableSteam = _transportData.getPreviousState().getSteamStored();

        if (usableSteam <= 0) return;

        transferSteam(usableSteam);
    }

    private void transferSteam(double usableSteam)
    {
        _eligibleTransportData.clear();
        double steamSpaceAvailable = 0;

        for (final SteamTransportTransientData neighbourUnit : _allAdjacentTransports)
        {
            //Steam providers can always push?
            final double neighbourSteamStored = neighbourUnit.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = neighbourUnit.getPreviousState().getActualMaximumSteam();
            if (neighbourSteamStored < neighbourMaximumSteam && neighbourSteamStored < usableSteam)
            {
                _eligibleTransportData.add(neighbourUnit);
                steamSpaceAvailable += (neighbourMaximumSteam - neighbourSteamStored);
            }
        }

        final double calculatedSteamDensity = _transportData.getPreviousState().getSteamDensity();
        if (_transportBelow != null && calculatedSteamDensity >= _config.EQUILIBRIUM && _transportBelow.getPreviousState().getSteamStored() < _transportData.getPreviousState().getSteamStored())
        {
            final double neighbourSteamStored = _transportBelow.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = _transportBelow.getPreviousState().getActualMaximumSteam();
            if (neighbourSteamStored < neighbourMaximumSteam && neighbourSteamStored < usableSteam)
            {
                _eligibleTransportData.add(_transportBelow);
                steamSpaceAvailable += (neighbourMaximumSteam - neighbourSteamStored);
            }
        }

        double originalSteamStored = usableSteam;
        for (SteamTransportTransientData neighbourTransport : _eligibleTransportData)
        {
            final double neighbourSteamStored = neighbourTransport.getPreviousState().getSteamStored();
            final double neighbourMaximumSteam = neighbourTransport.getPreviousState().getActualMaximumSteam();

            final double ratio = (neighbourMaximumSteam - neighbourSteamStored) / steamSpaceAvailable;

            double amountTransferred = originalSteamStored * ratio;

            if (neighbourSteamStored + amountTransferred > neighbourMaximumSteam)
            {
                amountTransferred = neighbourMaximumSteam - neighbourSteamStored;
            }

            amountTransferred = amountTransferred * _config.TransferRatio;

            amountTransferred = _transportData.takeSteam(amountTransferred);

            neighbourTransport.verifyTick();
            neighbourTransport.addSteam(amountTransferred);
        }
    }

    private void transferWater()
    {
        final double usableWater = _transportData.getPreviousState().getCondensationStored();

        if (usableWater <= 0)
        {
            transferWaterFromHigherPoint();
            return;
        }
        //First, work on any units above
        if (_transportBelow != null)
        {
            transferWaterBelow(usableWater);
        }

        if (usableWater > 0 && _horizontalAdjacentTransports.Any())
        {
            transferWaterAcross(usableWater);
        }

        transferWaterFromHigherPoint();
    }

    private void transferWaterFromHigherPoint()
    {
        /*if (_transportData.getDebug())
		{
		Console.WriteLine($"HERE! {_transport.getTransportLocation()}");
		}*/

        if (_transportBelow == null || !(_transportData.getUsableSteam() < _transportData.getPreviousState().getActualMaximumSteam()))
        {
            return;
        }
        final PreviousTransportState previousTransportState = _transportBelow.getPreviousState();
        if (!(Math.abs(previousTransportState.getCondensationStored() - previousTransportState.getMaximumCondensation()) < 100))
        {
            return;
        }

        final Stack<SearchData> elementsToSearch = new Stack<>();
        final Set<SteamTransportLocation> visitedLocations = new HashSet<>();
        visitedLocations.add(_transport.getTransportLocation());
        elementsToSearch.push(new SearchData(_transportBelow.getTransport(), 1));
        SearchData candidate = null;
        Boolean validScenario = true;
        while (validScenario && elementsToSearch.Any())
        {
            final SearchData searchData = elementsToSearch.pop();

            final SteamTransport transport = searchData.Transport;
            final int depth = searchData.Depth;
            final SteamTransportLocation steamTransportLocation = transport.getTransportLocation();
            //Console.WriteLine($"Checking transport @ {steamTransportLocation} - {depth} - {_transport.getShouldDebug()}");
            visitedLocations.add(steamTransportLocation);

            if (depth <= 0 && (candidate == null || depth < candidate.Depth))
            {
                if (searchData.Depth < 0 || (searchData.Depth == 0 && searchData.Transport.getWaterStored() >= (_transport.getWaterStored() + 5)))
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
            _transportData.addCondensate(actualCondensate);
        }
    }

    private void transferWaterAcross(double waterUsedAtStart)
    {
        _eligibleTransportData.clear();

        if (_horizontalAdjacentTransports.length == 0)
        {
            return;
        }

        final int elementIndex = _transportData.getTickLastUpdated() % _horizontalAdjacentTransports.length;
        final SteamTransportTransientData nextTransport = _horizontalAdjacentTransports[elementIndex];

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

        final double waterStored = _transportData.getPreviousState().getCondensationStored();
        if (neighbourWaterStored >= waterStored)
        {
            return;
        }

        final double desiredTransfer = (waterStored - neighbourWaterStored) / (_horizontalAdjacentTransports.length + 1);
        for (SteamTransportTransientData steamTransportTransientData : _horizontalAdjacentTransports)
        {
            final double takeCondensate = _transportData.takeCondensate(desiredTransfer);
            steamTransportTransientData.addCondensate(takeCondensate);
        }
    }

    private void transferWaterBelow(double usableWater)
    {
        final double neighbourWaterStored = _transportBelow.getPreviousState().getCondensationStored();
        final double neighbourMaximumWater = _transportBelow.getPreviousState().getMaximumCondensation();

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

        amountTransferred = _transportData.takeCondensate(amountTransferred);

        _transportBelow.verifyTick();
        _transportBelow.addCondensate(amountTransferred);
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
