package mod.steamnsteel.commands;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CommandDestroyEntities implements ICommand
{
    @Override
    public String getCommandName()
    {
        return "KILL_ALL";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return getCommandName();
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Lists.newArrayList(getCommandName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            for (Entity e : sender.getEntityWorld().getLoadedEntityList())
            {
                e.fall(100, 1000);
            }
        });
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    @Override
    public int compareTo(ICommand iCommand)
    {
        return 0;
    }
}
