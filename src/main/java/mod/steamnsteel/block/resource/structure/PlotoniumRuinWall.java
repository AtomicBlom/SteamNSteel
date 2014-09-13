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

package mod.steamnsteel.block.resource.structure;

import mod.steamnsteel.block.SteamNSteelBlock;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PlotoniumRuinWall extends SteamNSteelBlock
{
    public static final String NAME = "ruinWallPlotonium";

	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Logger.info("%d, %d, %d, %d", x, y, z, side);
		return super.getIcon(blockAccess, x, y, z, side);
	}

	public PlotoniumRuinWall()
    {
        super(Material.rock);
        setBlockName(NAME);


    }
}
