package managers;

import me.davi2206.LvLBank.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class PlayerCommands
{
	private Plugin plugin;
	private Player player;
	private static PlayerCommands cmds;
	private int args;

	private PlayerCommands()
	{}

	public static PlayerCommands getInstance()
	{
		if (cmds == null)
		{
			cmds = new PlayerCommands();
		}
		return cmds;
	}

	// Handeling all commands
	public boolean doCommands(Plugin plugin, BankManagement bm,
			CommandSender sender, Command cmd, String commandLabel,
			String[] arguments)
	{
		this.plugin = plugin;
		player = (Player) sender;

		args = arguments.length;

		// If lvl bank command
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
				if (arguments[0].equalsIgnoreCase("reload"))
				{
					cmdReload();
				}
				else if (arguments[0].equalsIgnoreCase("deposit"))
				{
					bm.deposit(player);
				}
				else if (arguments[0].equalsIgnoreCase("withdraw"))
				{
					bm.withdraw(player);
				}
				else if (arguments[0].equalsIgnoreCase("balance"))
				{
					cmdBalance(bm);
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
					player.sendMessage(ChatColor.RED + "Unknown command!");
					cmdHelp();
				}
			}
			// XXX Two arguments
			else if (args == 2)
			{
				if (arguments[0].equalsIgnoreCase("deposit"))
				{
					cmdDeposit(arguments, bm);
				}
				else if (arguments[0].equalsIgnoreCase("withdraw"))
				{
					cmdWithdraw(arguments, bm);
				}
				else if (arguments[0].equalsIgnoreCase("balance"))
				{
					cmdBalanceWorld(bm, arguments);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Unknown command!");
					cmdHelp();
				}
			}
			// XXX Three arguments
			else if (args == 3)
			{
				if (arguments[0].equalsIgnoreCase("balance"))
				{
					cmdBalancePlayerWorld(bm, arguments);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Unknown command!");
					cmdHelp();
				}
			}
		}

		return true;
	}

	// XXX cmdHelp
	public void cmdHelp()
	{
		String allowedCmds = (ChatColor.BLUE + "The commands you can use are: \n");

		if (player.hasPermission(new Permissions().lvlBankReload))
		{
			allowedCmds += (ChatColor.YELLOW 
					+ "- /lvlBank reload \n");
		}
		
		allowedCmds += (ChatColor.YELLOW 
					+ "- /lvlBank deposit [amount] \n"
					+ "- /lvlBank withdraw [amount] \n" 
					+ "- /lvlBank balance [world] \n");
		

		if (player.hasPermission(new Permissions().lvlBankOther))
		{
			allowedCmds += (ChatColor.YELLOW 
					+ "- /lvlBank balance <world> <player> \n");
		}

		allowedCmds += (ChatColor.YELLOW + "- /lvlBank limits");
		player.sendMessage(allowedCmds);
	}

	// XXX cmdReload
	public boolean cmdReload()
	{
		if (player.hasPermission(new Permissions().lvlBankReload))
		{
			plugin.reloadConfig();
			player.sendMessage(ChatColor.GREEN + "LvL_Bank reloaded!");
			checkMinMaxValues();
			return true;
		}
		else
		{
			noPermission();
			return false;
		}
	}

	// XXX cmdBalance(bm)
	public boolean cmdBalance(BankManagement bm)
	{
		int balance = 0;
		balance = bm.getBalance(player);

		if (balance > 1 || balance == 0)
		{
			player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN
					+ balance + ChatColor.BLUE + " levels in the Bank");
		}
		else if (balance == 1)
		{
			player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN
					+ balance + ChatColor.BLUE + " level in the Bank");
		}
		else if (balance == (-9001))
		{
			return false;
		}
		return true;
	}

	// XXX cmdBalanceWorld(bm, args)
	public boolean cmdBalanceWorld(BankManagement bm, String[] args)
	{
		String world = args[1];
		if (player.hasPermission(new Permissions().lvlBankWorld))
		{
			int balance = 0;

			try
			{
				balance = bm.getBalance(player, world);
			}
			catch (Exception e)
			{
				player.sendMessage(ChatColor.RED
						+ "There was an unexpected error. Ask an admin to check the console.");
				e.printStackTrace();
				return false;
			}
			tellTheResult(balance, world);
			return true;
		}
		else
		{
			noPermission();
			return false;
		}
	}

	// XXX cmdBalancePlayerWorld(bm, args)
	public boolean cmdBalancePlayerWorld(BankManagement bm, String[] args)
	{
		int balance = -9001;
		String world = args[1];
		String sPlayer = args[2];
		if (player.hasPermission(new Permissions().lvlBankOther))
		{
			try
			{
				balance = bm.getBalance(player, sPlayer, world);
			}
			catch (Exception e)
			{
				player.sendMessage(ChatColor.RED
						+ "There was an unexpected error. Ask an admin to check the console.");
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			noPermission();
			return false;
		}
		tellTheResult(balance, world);
		return true;
	}
	
	// XXX cmdLimits()
	public void cmdLimits()
	{
		String minDep = plugin.getConfig().getString(
				"Account_Limits.Min_Deposit");
		player.sendMessage(ChatColor.BLUE + "Min deposit: " + ChatColor.GREEN
				+ minDep);

		String maxDep = plugin.getConfig().getString(
				"Account_Limits.Max_Deposit");
		player.sendMessage(ChatColor.BLUE + "Max deposit: " + ChatColor.GREEN
				+ maxDep);

		String minWit = plugin.getConfig().getString(
				"Account_Limits.Min_Withdraw");
		player.sendMessage(ChatColor.YELLOW + "Min withdraw: "
				+ ChatColor.GREEN + minWit);

		String maxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");
		player.sendMessage(ChatColor.YELLOW + "Max withdraw: "
				+ ChatColor.GREEN + maxWit);

		String maxBal = plugin.getConfig().getString(
				"Account_Limits.Max_Account_Balance");
		player.sendMessage(ChatColor.DARK_GREEN + "Max balance: "
				+ ChatColor.GREEN + maxBal);

		String maxPlLvl = plugin.getConfig().getString(
				"Player_Limits.Max_Player_Level");
		player.sendMessage(ChatColor.DARK_GREEN + "Max player level: "
				+ ChatColor.GREEN + maxPlLvl);
	}

	// XXX cmdDeposit(args, bm)
	public void cmdDeposit(String[] args, BankManagement bm)
	{
		int amount = 0;
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch (Exception e)
		{
			player.sendMessage("The amount has to be a number, lower than or equal to the amount of levels you have");
			cmdHelp();
			return;
		}

		bm.deposit(player, amount);
	}

	// XXX cmdWithdraw(args, bm)
	public void cmdWithdraw(String[] args, BankManagement bm)
	{
		int amount = 0;
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch (Exception e)
		{
			player.sendMessage(ChatColor.RED
					+ "The argument has to be a number, lower than or equal to the balance of your account!");
			cmdHelp();
			return;
		}

		bm.withdraw(player, amount);
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
			minDep = Integer.parseInt(plugin.getConfig().getString(
					"Account_Limits.Min_Deposit"));
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
			player.sendMessage(ChatColor.RED
					+ "The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			player.sendMessage(ChatColor.RED + "Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		else if (minWit > maxWit && (minWit != -1) && (maxWit != -1))
		{
			player.sendMessage(ChatColor.RED
					+ "The minimum deposit and withdraw amounts cannot be higher than the maximum amounts!");
			player.sendMessage(ChatColor.RED + "Plugin will be disabled");
			plugin.getPluginLoader().disablePlugin(plugin);
			return false;
		}
		return true;
	}

	// XXX tellTheResult(player, balance, world)
	public boolean tellTheResult(int balance, String world)
	{
		if (balance > 1 || balance == 0)
		{
			player.sendMessage(ChatColor.BLUE
					+ "You have " + ChatColor.GREEN + balance + ChatColor.BLUE
					+ " levels in the Bank in world: " + ChatColor.YELLOW
					+ world);
		}
		else if (balance == 1)
		{
			player.sendMessage(ChatColor.BLUE
					+ "You have " + ChatColor.GREEN + balance + ChatColor.BLUE
					+ " level in the Bank in world: " + ChatColor.YELLOW
					+ world);
		}
		else if (balance == (-1))
		{
			player.sendMessage(ChatColor.RED
					+ "You are not registered in the Bank!");
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

	// XXX noPermission
	public void noPermission()
	{
		player.sendMessage(ChatColor.RED
				+ "You do not have permission to use that command!");
		cmdHelp();
	}
}
