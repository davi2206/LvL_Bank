package managers;

import me.davi2206.LvLBank.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import Connection.Transfere;

public class ConsoleCommands
{
	private Plugin plugin;
	private BankManagement bm;
	private CommandSender sender;
	private static ConsoleCommands cmds;
	private int args;

	private ConsoleCommands(Plugin pl)
	{
		this.plugin = pl;
	}

	public static ConsoleCommands getInstance(Plugin plug)
	{
		if (cmds == null)
		{
			cmds = new ConsoleCommands(plug);
		}
		return cmds;
	}

	// Handeling all commands
	public boolean doCommands(Plugin plugin, BankManagement bm,
			CommandSender sender, Command cmd, String commandLabel,
			String[] arguments)
	{
		this.plugin = plugin;
		this.bm = bm;
		this.sender = sender;

		args = arguments.length;
		
		// XXX If transfere to YML command
		if(cmd.getName().equalsIgnoreCase("Transferetoyml"))
		{
			String sql = plugin.getConfig().getString("MySQL.use_SQL");
			boolean useSql = Boolean.parseBoolean(sql);
			
			if(useSql)
			{
				Transfere tf = new Transfere(plugin);
				if(tf.sendAllToFile())
				{
					sender.sendMessage(ChatColor.GREEN + "[LvL_Bank] Data sent to YML file");
					sender.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Reloading plugin!");
					cmdReload(bm);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "[LvL_Bank] Unable to save data to file!");
				}
				tf = null;
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "[LvL_Bank] The 'Use SQL' value in the config is FALSE!");
				sender.sendMessage(ChatColor.RED + "[LvL_Bank] Set it to true, and make sure the connection options are correct, and try again!");
			}
		}
		
		// XXX If lvl bank command
		if (cmd.getName().equalsIgnoreCase("lvlBank"))
		{
			// XXX No arguments
			if (args == 0)
			{
				cmdHelp();
			}
			// XXX One argument
			else if (args == 1)
			{
				// XXX Reload
				if (arguments[0].equalsIgnoreCase("reload"))
				{
					cmdReload(bm);
					return true;
				}
				else if (arguments[0].equalsIgnoreCase("Limits"))
				{
					cmdLimits();
				}
				else if (arguments[0].equalsIgnoreCase("help"))
				{
					cmdHelp();
				}
				else if (arguments[0].equalsIgnoreCase(""))
				{
					cmdHelp();
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "[LvL_Bank] Unknown command!");
					cmdHelp();
				}
			}
			// XXX Three arguments
			else if (args == 3)
			{
				// XXX Balance in world for player
				if (arguments[0].equalsIgnoreCase("balance"))
				{
					cmdBalancePlayerWorld(arguments);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "[LvL_Bank] Unknown command!");
					cmdHelp();
				}
			}
			else if (args > 3)
			{
				sender.sendMessage(ChatColor.RED + "[LvL_Bank] Too many arguments");
				cmdHelp();
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "[LvL_Bank] Unknown command");
				cmdHelp();
			}
		}
		return true;
	}

	// XXX cmdHelp
	public void cmdHelp()
	{
		String allowedCmds = (ChatColor.BLUE + "[LvL_Bank] The commands you can use are: \n");

		if (sender.hasPermission(new Permissions().lvlBankReload))
		{
			allowedCmds += (ChatColor.YELLOW + "- /lvlBank reload \n");
		}

		if (sender.hasPermission(new Permissions().lvlBankOther))
		{
			allowedCmds += (ChatColor.YELLOW + "- /lvlBank balance <player> <world> \n");
		}

		allowedCmds += (ChatColor.YELLOW + "- /lvlBank limits");
		sender.sendMessage(allowedCmds);
	}

	// XXX cmdReload()
	public void cmdReload(BankManagement bm)
	{
		bm.save();
		plugin.reloadConfig();
		checkMinMaxValues();
		sender.sendMessage(ChatColor.GREEN + "[LvL_Bank] Reloaded!");
	}

	// XXX cmdLimits()
	public void cmdLimits()
	{
		String unLim = "Unlimited";
		
		String minDep = plugin.getConfig().getString(
				"Account_Limits.Min_Deposit");
		if(minDep.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.BLUE + "Min deposit: " + ChatColor.GREEN
					+ unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.BLUE + "Min deposit: " + ChatColor.GREEN
				+ minDep);
		}
				
		String maxDep = plugin.getConfig().getString(
				"Account_Limits.Max_Deposit");
		if(maxDep.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.BLUE + "Max deposit: " + ChatColor.GREEN
					+ unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.BLUE + "Max deposit: " + ChatColor.GREEN
					+ maxDep);
		}

		String minWit = plugin.getConfig().getString(
				"Account_Limits.Min_Withdraw");
		if(minWit.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.YELLOW + "Min withdraw: "
					+ ChatColor.GREEN + unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "Min withdraw: "
					+ ChatColor.GREEN + minWit);
		}

		String maxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");
		if(maxWit.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.YELLOW + "Max withdraw: "
					+ ChatColor.GREEN + unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "Max withdraw: "
					+ ChatColor.GREEN + maxWit);
		}

		String maxBal = plugin.getConfig().getString(
				"Account_Limits.Max_Account_Balance");
		if(maxBal.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.DARK_GREEN + "Max balance: "
					+ ChatColor.GREEN + unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.DARK_GREEN + "Max balance: "
					+ ChatColor.GREEN + maxBal);
		}

		String maxPlLvl = plugin.getConfig().getString(
				"Player_Limits.Max_Player_Level");
		if(maxPlLvl.equalsIgnoreCase("-1"))
		{
			sender.sendMessage(ChatColor.DARK_GREEN + "Max player level: "
					+ ChatColor.GREEN + unLim);
		}
		else
		{
			sender.sendMessage(ChatColor.DARK_GREEN + "Max player level: "
					+ ChatColor.GREEN + maxPlLvl);
		}
	}

	// XXX cmdBalancePlayerWorld(arguments)
	public boolean cmdBalancePlayerWorld(String[] arguments)
	{
		int balance = -9001;
		String player = arguments[1];
		String world = arguments[2];

		try
		{
			balance = bm.getBalance(sender, player, world);
		}
		catch (Exception e)
		{
			sender.sendMessage(ChatColor.RED
					+ "[LvL_Bank] There was an unexpected error. Ask an admin to check the console.");
			e.printStackTrace();
			return true;
		}

		tellTheResult(sender, balance, player, world);
		return true;
	}

	// XXX checkMinMaxValues()
	public boolean checkMinMaxValues()
	{
		int minDep = 0;
		int maxDep = 0;
		int minWit = 0;
		int maxWit = 0;

		try
		{
			String s = (plugin.getConfig().getString("Account_Limits.Min_Deposit"));
			minDep = Integer.parseInt(s);
			maxDep = Integer.parseInt(plugin.getConfig().getString(
					"Account_Limits.Max_Deposit"));
			minWit = Integer.parseInt(plugin.getConfig().getString(
					"Account_Limits.Min_Withdraw"));
			maxWit = Integer.parseInt(plugin.getConfig().getString(
					"Account_Limits.Max_Withdraw"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (minDep > maxDep && (minDep != -1) && (maxDep != -1))
		{
			sender.sendMessage(ChatColor.RED
					+ "[LvL_Bank] The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			sender.sendMessage(ChatColor.RED + "[LvL_Bank] Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		else if (minWit > maxWit && (minWit != -1) && (maxWit != -1))
		{
			sender.sendMessage(ChatColor.RED
					+ "[LvL_Bank] The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			sender.sendMessage(ChatColor.RED + "[LvL_Bank] Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		return true;
	}

	// XXX tellTheResult(CommandSender sender, int balance, String player, String world)
	public boolean tellTheResult(CommandSender sender, int balance,
			String player, String world)
	// Tell the command sender the result
	{
		if (balance > 1 || balance == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + player + ChatColor.BLUE
					+ " has " + ChatColor.GREEN + balance + ChatColor.BLUE
					+ " levels in the Bank in world: " + ChatColor.YELLOW
					+ world);
		}
		else if (balance == 1)
		{
			sender.sendMessage(ChatColor.YELLOW + player + ChatColor.BLUE
					+ " has " + ChatColor.GREEN + balance + ChatColor.BLUE
					+ " level in the Bank in world: " + ChatColor.YELLOW
					+ world);
		}
		else if (balance == (-1))
		{
			sender.sendMessage(ChatColor.YELLOW + player + ChatColor.RED
					+ " is not registered in the Bank!");
		}
		else if (balance == (-2))
		{
			return false;
		}
		else if (balance == (-9001))
		{
			return false;
		}
		return true;
	}
}
