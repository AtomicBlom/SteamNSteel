/*
 * Copyright (c) 2014 Rosie Alexander and Scott Killen.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */

package mod.steamnsteel.block.machine.structure;

import com.foudroyantfactotum.tool.structure.coordinates.BlockPosUtil;
import com.foudroyantfactotum.tool.structure.tileentity.StructureTE;
import com.foudroyantfactotum.tool.structure.utility.StructureDefinitionBuilder;
import com.google.common.collect.ImmutableMap;
import mod.steamnsteel.block.SteamNSteelStructureBlock;
import mod.steamnsteel.tileentity.SpiderFactoryTE;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderFactoryStructure extends SteamNSteelStructureBlock
{
    public static final String NAME = "ssSpiderFactory";

    public SpiderFactoryStructure()
    {
        super(false, true);
        setUnlocalizedName(NAME);
        setHardness(Float.MAX_VALUE);
    }

    @Override
    public boolean onStructureBlockActivated(World world, BlockPos pos, EntityPlayer player, BlockPos callPos, EnumFacing side, BlockPos local, float sx, float sy, float sz)
    {
        return super.onStructureBlockActivated(world, pos, player, callPos, side, local, sx, sy, sz);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new SpiderFactoryTE(getPattern(), state.getValue(BlockDirectional.FACING), false);
    }

    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState blockState, int eventId, int eventParameter)
    {
        super.onBlockEventReceived(world, pos, blockState, eventId, eventParameter);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventId, eventParameter);
    }

    @Override
    public void spawnBreakParticle(World world, StructureTE te, BlockPos local, float sx, float sy, float sz)
    {

    }

    @Override
    public StructureDefinitionBuilder getStructureBuild()
    {
        final StructureDefinitionBuilder builder = new StructureDefinitionBuilder();

        builder.assignConstructionDef(ImmutableMap.of());

        builder.assignConstructionBlocks(
                new String[]{
                        " ",
                        " "
                },
                new String[]{
                        " ",
                        " "
                }
        );

        builder.assignToolFormPosition(BlockPosUtil.of(0,0,0));

        builder.setConfiguration(BlockPosUtil.of(0,0,0),
                new String[]{
                        "M",
                        "-"
                },
                new String[]{
                        "-",
                        "-"
                }
        );

        builder.setCollisionBoxes(
                new float[]{0.0f,0.0f,0.3f, 0.35f,1.1f,2.0f},//left side
                new float[]{0.35f,0.0f,0.3f, 0.65f,1.33f,2.0f},//middle
                new float[]{0.65f,0.0f,0.3f, 1.0f,1.1f,2.0f},//right side
                new float[]{0.35f,0.0f,0.0f, 0.65f,1.63f,0.3f}//back exhaust

        );

        return builder;
    }
}
