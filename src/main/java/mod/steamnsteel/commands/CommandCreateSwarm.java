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
package mod.steamnsteel.commands;

import mod.steamnsteel.entity.SteamSpiderEntity;
import mod.steamnsteel.entity.Swarm;
import mod.steamnsteel.entity.SwarmManager;
import mod.steamnsteel.utility.position.ChunkBlockCoord;
import mod.steamnsteel.utility.position.ChunkCoord;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

public class CommandCreateSwarm extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "createswarm";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return String.format("/%s [spidercount]", getCommandName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException
    {
        final int spiderCount = args.length == 1 ? Integer.valueOf(args[0]) : 0;
        final SwarmManager swarmManager = SwarmManager.swarmManagers.get(commandSender.getEntityWorld());
        final EntityPlayer player = getCommandSenderAsPlayer(commandSender);
        final Swarm swarm = new Swarm<SteamSpiderEntity>(
                commandSender.getEntityWorld(),
                ChunkCoord.of(player.chunkCoordX, player.chunkCoordZ),
                ChunkBlockCoord.of(8, MathHelper.floor_double(player.posY), 8),
                SteamSpiderEntity.class
        );

        for (int i = 0; i < spiderCount; i++)
        {
            SteamSpiderEntity spiderEntity = new SteamSpiderEntity(commandSender.getEntityWorld());
            spiderEntity.setSwarm(swarm);
            spiderEntity.setPosition(player.posX, player.posY, player.posZ);
            commandSender.getEntityWorld().spawnEntityInWorld(spiderEntity);
        }

        swarmManager.addSwarm(swarm);
    }
}
