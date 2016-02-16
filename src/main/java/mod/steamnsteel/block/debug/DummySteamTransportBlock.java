package mod.steamnsteel.block.debug;

import mod.steamnsteel.block.SteamNSteelBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;

/**
 * Created by codew on 15/02/2016.
 */
public class DummySteamTransportBlock extends SteamNSteelBlock
{
    public static final String NAME = "dummySteamTransport";

    public DummySteamTransportBlock()
    {
        super(Material.iron, true);
        setUnlocalizedName(NAME);
    }

    @Override
    protected BlockState createBlockState()
    {
        return super.createBlockState();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return super.getExtendedState(state, world, pos);
    }

    @Override
    public boolean isFullBlock()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isTranslucent()
    {
        return true;
    }

    @Override
    public boolean isVisuallyOpaque()
    {
        return false;
    }
}
