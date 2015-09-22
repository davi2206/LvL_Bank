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
		//XXX Command Reload
		if (cmd.getName().equalsIgnoreCase("lvlReload")
				&& sender.hasPermission(new Permissions().lvlBankReload))
		{
			plugin.reloadConfig();
			sender.sendMessage(ChatColor.GREEN + "LvL_Bank reloaded!");
			checkMinMaxValues(plugin, sender);
		}
		else if (cmd.getName().equalsIgnoreCase("lvlReload")
				&& !sender.hasPermission(new Permissions().lvlBankReload))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
		}

		boolean isPlayer = false;

		// Check if Command Sender is a Player
		if (sender instanceof Player)
		{
			player = (Player) sender;

			isPlayer = true;
		}

		//XXX Command deposit
		if (cmd.getName().equalsIgnoreCase("lvldeposit"))
		{
			if (!isPlayer)
			{
				sender.sendMessage(ChatColor.RED
						+ "Only players can use this command");
				return true;
			}

			// deposit all
			if (args.length == 0
					&& (player.hasPermission(new Permissions().lvlBankCommands)))
			{
				bm.deposit(player);
			}

			// deposit amount
			else if (args.length > 0
					&& (player.hasPermission(new Permissions().lvlBankCommands)))
			{
				int amount = 0;
				try
				{
					amount = Integer.parseInt(args[0]);
				}
				catch (Exception e)
				{
					player.sendMessage("The amount has to be a number, lower than or equal to the amount of levels you have");
					return true;
				}

				bm.deposit(player, amount);
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

		//XXX Command Withdraw
		if (cmd.getName().equalsIgnoreCase("lvlWithdraw"))
		{
			if (!isPlayer)
			{
				sender.sendMessage(ChatColor.RED
						+ "Only players can use this command");
				return true;
			}

			// Withdraw all
			if (args.length == 0
					&& (player.hasPermission(new Permissions().lvlBankCommands)))
			{
				bm.withdraw(player);
			}

			// Withdraw amount
			else if (args.length > 0
					&& (player.hasPermission(new Permissions().lvlBankCommands)))
			{
				int amount = 0;
				try
				{
					amount = Integer.parseInt(args[0]);
				}
				catch (Exception e)
				{
					player.sendMessage(ChatColor.RED
							+ "The argument has to be a number, lower than or equal to the balance of your account!");
					return true;
				}

				bm.withdraw(player, amount);
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

		//XXX Command balance
		if (cmd.getName().equalsIgnoreCase("lvlBalance"))
		{
			int balance = 0;
			
			if (sender.hasPermission(new Permissions().lvlBankCommands))
			{
				// Check own balance
				if (args.length == 0)
				{
					if (!isPlayer)
					{
						sender.sendMessage(ChatColor.RED
								+ "Only players can use this command");
						return true;
					}
					
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
