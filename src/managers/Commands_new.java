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
	private int args;
	
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
			String[] arguments)
	{
		boolean isPlayer = false;

		// Check if Command Sender is a Player
		if (sender instanceof Player)
		{
			player = (Player) sender;
			isPlayer = true;
		}
		
		args = arguments.length;
		
		//If lvl bank command
		if(cmd.getName().equalsIgnoreCase("lvlBank"))
		{
			//XXX One argument
			if(args == 1)
			{
				//XXX Reload
				if(arguments[0].equalsIgnoreCase("reload"))
				{
					if(sender.hasPermission(new Permissions().lvlBankReload))
					{
						plugin.reloadConfig();
						sender.sendMessage(ChatColor.GREEN + "LvL_Bank reloaded!");
						checkMinMaxValues(plugin, sender);
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "You do not have permission to reload this plugin!");
					}
				}
				//XXX Deposit all (max)
				else if(arguments[0].equalsIgnoreCase("deposit"))
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					bm.deposit(player);
				}
				//XXX Withdraw all (max)
				else if(arguments[0].equalsIgnoreCase("withdraw"))
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					bm.withdraw(player);
				}
				else if(arguments[0].equalsIgnoreCase("balance"))
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					
					int balance = 0;
					balance = bm.getBalance(player);
					
					if (balance > 1 || balance == 0)
					{
						player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN + balance
								+ ChatColor.BLUE + " levels in the Bank");
					}
					else if (balance == 1)
					{
						player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN + balance
								+ ChatColor.BLUE + " level in the Bank");
					}
					else if(balance == (-9001))
					{
						return true;
					}
				}
			}
			//XXX Two arguments
			else if(args == 2)
			{
				//XXX Deposit amount
				if(arguments[0].equalsIgnoreCase("deposit"))
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					
					int amount = 0;
					try
					{
						amount = Integer.parseInt(arguments[1]);
					}
					catch (Exception e)
					{
						player.sendMessage("The amount has to be a number, lower than or equal to the amount of levels you have");
						return true;
					}
					
					bm.deposit(player, amount);
				}
				//XXX withdraw amount
				else if(arguments[0].equalsIgnoreCase("withdraw"))
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					
					int amount = 0;
					try
					{
						amount = Integer.parseInt(arguments[1]);
					}
					catch (Exception e)
					{
						player.sendMessage(ChatColor.RED
								+ "The argument has to be a number, lower than or equal to the balance of your account!");
						return true;
					}

					bm.withdraw(player, amount);
				}
				//XXX Balance in World
				else if(arguments[0].equalsIgnoreCase("balance"))
				{
					if(player.hasPermission(new Permissions().lvlBankOther))
					{
						try
						{
							bm.getBalance(player, arguments[1]);
						}
						catch(Exception e)
						{
							player.sendMessage(ChatColor.RED + "There was an unexpected error. Ask an admin to check the console.");
							e.printStackTrace();
						}
					}
				}
			}
			//XXX Three arguments
			else if(args == 3)
			{
//				balance world player
			}
			
			
			
			
			
			
		}
		
		return true;
	}
	
	//XXX checkMinMaxValues(Plugin plugin, ConsoleCommandSender clog)
	public boolean checkMinMaxValues(Plugin plugin, CommandSender sender)
	{
		int minDep = 0;
		int maxDep = 0;
		int minWit = 0;
		int maxWit = 0;
		
		try
		{
			minDep = Integer.parseInt(plugin.getConfig().getString("Account_Limits.Min_Deposit"));
			maxDep = Integer.parseInt(plugin.getConfig().getString("Account_Limits.Max_Deposit"));
			minWit = Integer.parseInt(plugin.getConfig().getString("Account_Limits.Min_Withdraw"));
			maxWit = Integer.parseInt(plugin.getConfig().getString("Account_Limits.Max_Withdraw"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(minDep > maxDep && 
				(minDep != -1) &&
				(maxDep != -1))
		{
			sender.sendMessage(ChatColor.RED + "The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			sender.sendMessage(ChatColor.RED + "Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		else if(minWit > maxWit && 
				(minWit != -1) &&
				(maxWit != -1))
		{
			sender.sendMessage(ChatColor.RED + "The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			sender.sendMessage(ChatColor.RED + "Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		return true;
	}
}
