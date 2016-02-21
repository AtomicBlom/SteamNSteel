package mod.steamnsteel.plumbing.Impl;

import com.google.common.base.Objects;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SteamTransportLocation
{
    private final int x;
    private final int y;
    private final int z;
    private final int dimensionId;

    private SteamTransportLocation(int x, int y, int z, int dimensionId)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensionId = dimensionId;
    }

    public static SteamTransportLocation create(int x, int y)
    {
        return new SteamTransportLocation(x, y, 0, 0);
    }

    public static SteamTransportLocation create(BlockPos pos, int dimensionId) {
        return new SteamTransportLocation(pos.getX(), pos.getY(), pos.getZ(), dimensionId);
    }

    public static SteamTransportLocation create(int x, int y, int z, int dimensionId)
    {
        return new SteamTransportLocation(x, y, z, dimensionId);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getDimensionId()
    {
        return dimensionId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SteamTransportLocation that = (SteamTransportLocation) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return dimensionId == that.dimensionId;

    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + dimensionId;
        return result;
    }

    public SteamTransportLocation offset(EnumFacing direction)
    {
        //Fixme: Use a pool?
        return new SteamTransportLocation(x + direction.getDirectionVec().getX(), y + direction.getDirectionVec().getY(), z + direction.getDirectionVec().getZ(), dimensionId);
    }

    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .add("z", z)
            .toString();
    }
}
