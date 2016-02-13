package mod.steamnsteel.plumbing.Impl;

import com.google.common.base.Objects;
import net.minecraft.util.EnumFacing;

public class SteamTransportLocation
{
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _worldId;

    private SteamTransportLocation(int x, int y, int z, int worldId)
    {
        _x = x;
        _y = y;
        _z = z;
        _worldId = worldId;
    }

    public static SteamTransportLocation create(int x, int y)
    {
        return new SteamTransportLocation(x, y, 0, 0);
    }

    public static SteamTransportLocation create(int x, int y, int z, int worldId)
    {
        return new SteamTransportLocation(x, y, z, worldId);
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getZ()
    {
        return _z;
    }

    public int getWorldId()
    {
        return _worldId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SteamTransportLocation that = (SteamTransportLocation) o;

        if (_x != that._x) return false;
        if (_y != that._y) return false;
        if (_z != that._z) return false;
        return _worldId == that._worldId;

    }

    @Override
    public int hashCode()
    {
        int result = _x;
        result = 31 * result + _y;
        result = 31 * result + _z;
        result = 31 * result + _worldId;
        return result;
    }

    public SteamTransportLocation offset(EnumFacing direction)
    {
        //Fixme: Use a pool?
        return new SteamTransportLocation(_x + direction.getDirectionVec().getX(), _y + direction.getDirectionVec().getY(), _z + direction.getDirectionVec().getZ(), _worldId);
    }

    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("x", _x)
                .add("y", _y)
                .add("z", _z)
            .toString();
    }
}
