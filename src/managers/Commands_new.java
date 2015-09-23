package managers;

import me.davi2206.LvLBank.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands_new
{
	private Player player;
	private static Commands_new cmds;
	
	private Commands_new()
	{
		
	}
	
	public static Commands_new getInstance()
	{
		if (cmds == null)
		{
			cmds = new Commands_new();
		}
		return cmds;
	}

	// Handeling all commands
	public boolean doCommands(Plugin plugin, BankManagement bm,
			CommandSender sender, Command cmd, String commandLabel,
			String[] args)
	{
		
		
		return true;
	}
}
