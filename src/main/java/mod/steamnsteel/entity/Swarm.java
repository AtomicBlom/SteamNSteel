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
package mod.steamnsteel.entity;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import mod.steamnsteel.utility.position.ChunkBlockCoord;
import mod.steamnsteel.utility.position.ChunkCoord;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class Swarm<T extends EntityLiving & ISwarmer>
{
    public static final String HOME_CHUNK_X = "HomeChunkX";
    public static final String HOME_CHUNK_Z = "HomeChunkZ";
    public static final String HOME_BLOCK = "HomeBlock";

    public static final String THREAT = "Threat";
    public static final String THREAT_NAME = "Name";
    public static final String THREAT_COUNT = "Count";

    private static final int NBT_TAG_TYPE_COMPOUND = 10;

    transient Class<T> entityClass; //We need this for comparisons
    transient private World world;
    //private WeakReference<Object> spiderFactory; //TODO implement
    private final Multiset<String> threatCount = HashMultiset.create();
    transient private final Set<T> swarmerEntities = Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
    private ChunkCoord homeChunkCoord;
    private ChunkBlockCoord homeBlockCoord; //This could be replaced with the spiderFactory in the future
    private ChunkCoord currPosition;

    public Swarm(World world, Class<T> entityClass)
    {
        this.world = world;
        this.entityClass = entityClass;
    }

    public Swarm(World world, ChunkCoord homeChunkCoord, ChunkBlockCoord homeBlockCoord, Class<T> entityClass)
    {
        this(world, entityClass);
        this.homeChunkCoord = homeChunkCoord;
        this.homeBlockCoord = homeBlockCoord;
        this.currPosition = homeChunkCoord;
    }

    public void update(int tickCounter)
    {
        @SuppressWarnings("unchecked")
        List<EntityPlayer> playerList = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(
                homeChunkCoord.getX(), homeBlockCoord.getY() - 16, homeChunkCoord.getZ(), homeChunkCoord.getX() + 16,
                homeBlockCoord.getY() + 16, homeChunkCoord.getZ() + 16));

        if (playerList != null && playerList.size() > 0)
        {
            HashMultiset<String> playersToAdd = HashMultiset.create();
            //Increase threat count for players in home radius
            for (EntityPlayer player : playerList)
            {
                playersToAdd.add(player.getName());
                changeThreatCount(player.getName(), 1);
            }

            //Decrease threat count for players no longer in home area
            for (String playerName : threatCount.elementSet())
            {
                if (!playersToAdd.contains(playerName))
                {
                    threatCount.remove(playerName);
                }
            }
        }
    }

    public void addEntity(T entity)
    {
        swarmerEntities.add(entity);
    }

    public void removeEntity(T entity)
    {
        swarmerEntities.remove(entity);
    }

    public void setWorld(World world)
    {
        this.world = world;
        swarmerEntities.clear();
    }

    public void setAttackTarget(EntityLivingBase entityLiving)
    {
        for (T entity : swarmerEntities)
        {
            entity.setAttackTarget(entityLiving);
        }
    }

    //TODO implement
    private EntityPlayer findNearestTarget(boolean ignoreThreat)
    {
        if (currPosition == null)
        {
            determineCurrentPosition();
        }
        //world.getEntitiesWithinAABBExcludingEntity(EntityPlayer.class, AxisAlignedBB.getBoundingBox())
        return null;
    }

    /**
     * This loops through the entities that are a part of this swarm and tried to determine a {@link mod.steamnsteel.utility.position.ChunkCoord}
     * for them based on where the majority of the swarm is.
     */
    private void determineCurrentPosition()
    {
        Multiset<ChunkCoord> chunkCoords = HashMultiset.create();
        for (T entity : swarmerEntities)
        {
            chunkCoords.add(ChunkCoord.of(MathHelper.floor_double(entity.posX) >> 4, MathHelper.floor_double(entity.posY) >> 4));
        }

        currPosition = Multisets.copyHighestCountFirst(chunkCoords).iterator().next(); //Little trick to get the highest count
    }

    public ChunkCoord getHomeChunkCoord()
    {
        return homeChunkCoord;
    }

    public ChunkBlockCoord getHomeBlockCoord()
    {
        return homeBlockCoord;
    }

    /**
     * Changes the threat count for the players name. Min threat is 0, max is 100. Value is clamped between 0 and 100 so if a
     * larger value is passed in, it is still capped.
     * Threat may be used by {@link mod.steamnsteel.entity.ISwarmer}'s when finding or determining if a certain player
     * should be attacked over another one. By default, threat is decreased by 1 every tick as long as said isn't in the
     * Swarm's home (this is done in {@link #update(int)}). Similarly, threat is increased by 1 as long as the player is in
     * the home region
     * @param name The players name
     * @param valueChange The value change
     */
    public void changeThreatCount(String name, int valueChange)
    {
        int newValue = MathHelper.clamp_int(getThreatValue(name) + valueChange, 0, 100);
        threatCount.setCount(name, newValue);
    }

    public int getThreatValue(String name)
    {
        if (threatCount.contains(name))
        {
            return threatCount.count(name);
        }

        return 0;
    }

    /**
     * Checks if this current swarm is valid and if not, return null. By default checks if {@link #swarmerEntities} is not
     * empty.
     * @return If swarm is valid
     */
    public boolean isValid()
    {
        return swarmerEntities.size() > 0;
    }

    /**
     * Whether this swarm should be saved on world save.
     * @return
     */
    public boolean shouldPersist()
    {
        return true;
    }

    public SwarmManager.SwarmType getSwarmType()
    {
        return SwarmManager.SwarmType.STEAMSPIDERSWARM;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        final int[] coord = nbtTagCompound.getIntArray(HOME_BLOCK);

        homeBlockCoord = ChunkBlockCoord.of(coord[0], coord[1], coord[2]);
        homeChunkCoord = ChunkCoord.of(nbtTagCompound.getInteger(HOME_CHUNK_X), nbtTagCompound.getInteger(HOME_CHUNK_Z));

        //Load threat list
        final NBTTagList threatTagList = nbtTagCompound.getTagList(THREAT, NBT_TAG_TYPE_COMPOUND);

        for (int j = 0; j < threatTagList.tagCount(); ++j)
        {
            final NBTTagCompound tagListEntry = threatTagList.getCompoundTagAt(j);
            threatCount.setCount(tagListEntry.getString(THREAT_NAME), tagListEntry.getInteger(THREAT_COUNT));
        }
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setInteger(HOME_CHUNK_X, homeChunkCoord.getX());
        nbtTagCompound.setInteger(HOME_CHUNK_Z, homeChunkCoord.getZ());
        nbtTagCompound.setIntArray(HOME_BLOCK, new int[]{homeBlockCoord.getX(), homeBlockCoord.getY(), homeBlockCoord.getZ()});

        //Save threat list
        final NBTTagList threatTagList = new NBTTagList();

        for (Multiset.Entry<String> entry : threatCount.entrySet())
        {
            final NBTTagCompound threatEntry = new NBTTagCompound();

            threatEntry.setString(THREAT_NAME, entry.getElement());
            threatEntry.setInteger(THREAT_COUNT, entry.getCount());
        }

        nbtTagCompound.setTag(THREAT, threatTagList);
    }
}
