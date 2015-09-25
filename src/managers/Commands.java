package managers;

import me.davi2206.LvLBank.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands
{
	private Player player;
	private static Commands cmds;

	private Commands()
	{
		//All commands handled in this class
	}

	public static Commands getInstance()
	{
		if (cmds == null)
		{
			cmds = new Commands();
		}
		return cmds;
	}

	// Handeling all commands
	public boolean doCommands(Plugin plugin, BankManagement bm,
			CommandSender sender, Command cmd, String commandLabel,
			String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("lvlBalance"))
		{
			int balance = 0;
			
			if (sender.hasPermission(new Permissions().lvlBankCommands))
			{
				
				else if(sender.hasPermission(new Permissions().lvlBankOther))
				{
					String world = "";
					//Check other players balance in current world
					if(args.length == 1)
					{
						if (!isPlayer)
						{
							sender.sendMessage(ChatColor.RED
									+ "Only players can use this command");
							return true;
						}
						
						world = player.getWorld().getName();
						String playerName = args[0];
						
						balance = bm.getBalance(player, playerName, world);
					}
					
					//Check other players balance in specified world
					if (args.length >= 2)
					{
						balance = bm.getBalance(player, args[0], args[1]);
						world = args[1];
					}
					
					//Tell the command sender the result
					if (balance > 1 || balance == 0)
					{
						sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.BLUE + " has " + ChatColor.GREEN + balance
								+ ChatColor.BLUE + " levels in the Bank in world: " + ChatColor.YELLOW + world);
					}
					else if (balance == 1)
					{
						sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.BLUE + " has " + ChatColor.GREEN + balance
								+ ChatColor.BLUE + " level in the Bank in world: " + ChatColor.YELLOW + world);
					}
					else if (balance == (-1))
					{
						sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not registered in the Bank!");
					}
					else if (balance == (-2))
					{
						return true;
					}
					else if(balance == (-9001))
					{
						return true;
					}
				}
				else if (!player.hasPermission(new Permissions().lvlBankCommands))
				{
					player.sendMessage(ChatColor.RED + "You do not have permission to use LvL Bank commands");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "For some reason that did not work.. Try again");
				}
			}
		}
		return true;
	}
	}
}
