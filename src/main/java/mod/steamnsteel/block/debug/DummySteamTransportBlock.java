package mod.steamnsteel.block.debug;

import mod.steamnsteel.block.SteamNSteelBlock;
import mod.steamnsteel.client.model.opengex.OpenGEXAnimationFrameProperty;
import mod.steamnsteel.tileentity.debug.DummySteamTransportTE;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;

public class DummySteamTransportBlock extends SteamNSteelBlock implements ITileEntityProvider
{
    public static final String NAME = "dummySteamTransport";

    public static final PropertyFloat SteamDensityProperty = new PropertyFloat("SteamDensity");
    public static final PropertyFloat CondensationProperty = new PropertyFloat("Condensation");


    public DummySteamTransportBlock()
    {
        super(Material.iron, true);
        setUnlocalizedName(NAME);
    }

    @Override
    protected BlockState createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {
                SteamDensityProperty,
                CondensationProperty,
                OpenGEXAnimationFrameProperty.instance
        });
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            final TileEntity genericTileEntity = world.getTileEntity(pos);
            if (genericTileEntity instanceof DummySteamTransportTE)
            {
                final DummySteamTransportTE tileEntity = (DummySteamTransportTE) genericTileEntity;
                if (player.isSneaking())
                {
                    final double condensation = tileEntity.getCondensation();
                    tileEntity.setCondensation(condensation + 100.0);
                } else {
                    final double condensation = tileEntity.getSteamDensity();
                    tileEntity.addSteam(condensation + 0.1);
                }
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
        }
        return false;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        final IExtendedBlockState actualState = (IExtendedBlockState)getActualState(state, world, pos);
        final TileEntity genericTileEntity = world.getTileEntity(pos);
        if (genericTileEntity instanceof DummySteamTransportTE) {
            final DummySteamTransportTE tileEntity = (DummySteamTransportTE)genericTileEntity;

            return actualState
                    .withProperty(SteamDensityProperty, (float) tileEntity.getSteamDensity())
                    .withProperty(CondensationProperty, (float) tileEntity.getCondensation())
                    ;
        }
        return actualState;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.SOLID;
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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {

        final DummySteamTransportTE dummySteamTransportTE = new DummySteamTransportTE();
        return dummySteamTransportTE;
    }

    @Override
    public int getRenderType()
    {
        return 2;
    }
}
