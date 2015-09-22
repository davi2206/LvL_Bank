package managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import Connection.Registration;

public class BankManagement
{
	private Connection con;
	private Registration reg;
	private Plugin plugin;
	private static BankManagement bankMan;

	private Set<String> groupList;
	private String playerName;
	private String world;
	private String group;
	
	private int minDep;
	private int maxDep;
	
	private int minWit;
	private int maxWit;
	
	private int newBalance;
	private int allowedBalance;
	private int accountOverflow;
	
	private int allowedPlayerLevel;
	private int playerLevelOverflow;
	
	private BankManagement(Connection con, Plugin plugin)
	{
		this.con = con;
		reg = Registration.getInstance(con);
		this.plugin = plugin;
	}

	public static BankManagement getInstance(Connection con, Plugin plugin)
	{
		if (bankMan == null)
		{
			bankMan = new BankManagement(con, plugin);
		}
		return bankMan;
	}

	public boolean deposit(Player player, int amount)
	{
		// XXX deposit(Player player, int amount)
		
		if(!validConnection(player))
		{
			return false;
		}
		
		defineValues(player, amount);
		
		if(accountOverflow > 0)
		{
			amount = (amount - accountOverflow);
			player.sendMessage(ChatColor.DARK_AQUA + "The amount to deposit was scaled down to " + 
								ChatColor.YELLOW + amount + 
								ChatColor.DARK_AQUA + " levels to not exceed the account limit");
			defineValues(player, amount);
		}
		
		if (!checkDepositLimits(player, amount))
		{
			return false;
		}

		playerName = player.getName();
		world = player.getWorld().getName();
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return false;
		}
		
		if (!reg.isRegistered(player))
		{
			reg.registerPlayer(player, group);
		}

		if (amount > player.getLevel())
		{
			player.sendMessage(ChatColor.RED
					+ "You do not have enough levels for that..");
			return true;
		}
		
		String lvl = ("" + newBalance);

		String querydepositAmount = "UPDATE lvl_bank_accounts SET " + group
				+ " = " + lvl + " WHERE playerName = '" + playerName + "';";

		try
		{
			PreparedStatement depositAmount = con
					.prepareStatement(querydepositAmount);
			depositAmount.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		if (amount > 1)
		{
			player.sendMessage(ChatColor.GREEN + "" + amount
					+ " levels deposited on account");
		}
		else if (amount == 1)
		{
			player.sendMessage(ChatColor.GREEN + "" + amount
					+ " level deposited on account");
		}

		int newAmount = (int) (player.getLevel() - amount);
		player.setLevel(newAmount);

		return true;
	}

	public boolean deposit(Player player)
	{
		// XXX deposit(Player player)
		if(!validConnection(player))
		{
			return false;
		}
		
		int lvl = player.getLevel();

		defineValues(player, lvl);
		
		if(lvl > maxDep)
		{
			deposit(player, maxDep);
			return true;
		}
		else if(lvl <= maxDep && lvl >= minDep)
		{
			deposit(player, lvl);
			return true;
		}
		else
		{
			player.sendMessage(ChatColor.RED
					+ "The minimum amount to deposit is " + ChatColor.YELLOW
					+ minDep + ChatColor.RED + " levels");
			return false;
		}
	}

	public boolean withdraw(Player player, int amount)
	{
		// XXX withdraw(Player player, int amount)
		if(!validConnection(player))
		{
			return false;
		}
		
		if (!checkWithdrawLimits(player, amount))
		{
			return false;
		}
		
		playerName = player.getName();
		world = player.getWorld().getName();
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return false;
		}

		if (!reg.isRegistered(player))
		{
			reg.registerPlayer(player, group);
		}

		int newBalance = (getBalance(player) - amount);

		if (newBalance < 0)
		{
			player.sendMessage(ChatColor.RED
					+ "You do not have enough levels in the bank for that.. Check your balance");
			return true;
		}
		
		int newPlayerLevel = (player.getLevel() + amount);
		
		String stringMaxPlayerLvl = plugin.getConfig().getString(
				"Player_Limits.Max_Player_Level");
		
		allowedPlayerLevel = Integer.parseInt(stringMaxPlayerLvl);
		
		if(player.getLevel() >= allowedPlayerLevel)
		{
			player.sendMessage(ChatColor.YELLOW + "You have the allowed amount of levels. Use some before withdrawing more!");
			return false;
		}
		else if(newPlayerLevel > allowedPlayerLevel)
		{
			int newAmount = (allowedPlayerLevel - (player.getLevel()));
			withdraw(player, newAmount);
			return true;
		}
		
		String stringLVL = ("" + newBalance);

		String queryWithdrawAmount = "UPDATE lvl_bank_accounts SET " + group
				+ " = " + stringLVL + " WHERE playerName = '" + playerName
				+ "';";
		try
		{
			PreparedStatement withdrawAmount = con
					.prepareStatement(queryWithdrawAmount);
			withdrawAmount.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
		if (amount > 1 || amount == 0)
		{
			player.sendMessage(ChatColor.YELLOW + "" + amount
					+ " levels withdrawn from account");
		}
		else if (amount == 1)
		{
			player.sendMessage(ChatColor.YELLOW + "" + amount
					+ " level withdrawn from account");
		}
				
		player.setLevel(newPlayerLevel);

		return true;
	}

	public boolean withdraw(Player player)
	{
		// XXX withdraw(Player player)
		if(!validConnection(player))
		{
			return false;
		}
		
		int balance = (int) getBalance(player);

		String stringMaxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");
		String stringMinWit = plugin.getConfig().getString(
				"Account_Limits.Min_Withdraw");
		
		maxWit = Integer.parseInt(stringMaxWit);
		minWit = Integer.parseInt(stringMinWit);
		
		if(balance > maxWit)
		{
			withdraw(player, maxWit);
			return true;
		}
		else if(balance <= maxWit && balance >= minWit)
		{
			withdraw(player, balance);
			return true;
		}
		else
		{
			player.sendMessage(ChatColor.RED
					+ "The minimum amount to withdraw is " + ChatColor.YELLOW
					+ minWit + ChatColor.RED + " levels");
			return false;
		}
	}

	public int getBalance(Player player)
	{
		// XXX getBalance(Player player)

		if(!validConnection(player))
		{
			return (-9001);
		}
		
		playerName = player.getName();
		this.world = player.getWorld().getName();
		group = getGroup(world);

		if (!reg.isRegistered(player))
		{
			reg.registerPlayer(player, group);
		}

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return (-2);
		}

		String queryGetBalance = "SELECT " + group
				+ " FROM lvl_bank_accounts WHERE playerName = '" + playerName
				+ "';";
		int lvlFromDb = 0;

		try
		{
			PreparedStatement getBalance = con
					.prepareStatement(queryGetBalance);
			ResultSet rs = getBalance.executeQuery();
			while (rs.next())
			{
				lvlFromDb = rs.getInt(group);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return lvlFromDb;
	}

	public int getBalance(Player sender, String player, String world)
	{
		// XXX getBalance(Player sender, String player, String world)
		playerName = player;
		this.world = world;
		group = getGroup(world);

		if(!validConnection(sender))
		{
			return (-9001);
		}
		
		if (!reg.isRegistered(playerName))
		{
			return (-1);
		}

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(sender);
			return (-2);
		}

		String queryGetBalance = "SELECT " + group
				+ " FROM lvl_bank_accounts WHERE playerName = '" + playerName
				+ "';";
		int lvlFromDb = 0;

		try
		{
			PreparedStatement getBalance = con
					.prepareStatement(queryGetBalance);
			ResultSet rs = getBalance.executeQuery();
			while (rs.next())
			{
				lvlFromDb = rs.getInt(group);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return lvlFromDb;
	}

	private void defineValues(Player player, int amount)
	{
		//XXX Define values
		String stringMinDep = plugin.getConfig().getString(
				"Account_Limits.Min_Deposit");
		String stringMaxDep = plugin.getConfig().getString(
				"Account_Limits.Max_Deposit");
		String stringAllowedBalance = plugin.getConfig().getString(
				"Account_Limits.Max_Account_Balance");
		
		minDep = Integer.parseInt(stringMinDep);
		maxDep = Integer.parseInt(stringMaxDep);
		newBalance = (getBalance(player) + amount);
		allowedBalance = Integer.parseInt(stringAllowedBalance);
		accountOverflow = (newBalance - allowedBalance);
	}
	
	// Check if the depositd amount is within the limits
	private boolean checkDepositLimits(Player player, int amount)
	{
		//XXX checkDepositLimits(Player player, int amount)
		if (amount < 0)
		{
			player.sendMessage(ChatColor.RED
					+ "You cannot deposit a negative amount!");
			return false;
		}
		else if (minDep == -1 && maxDep == -1)
		{
			return true;
		}
		else if (amount < minDep && minDep != -1)
		{
			player.sendMessage(ChatColor.RED
					+ "The minimum amount to deposit is " + ChatColor.YELLOW
					+ minDep + ChatColor.RED + " levels");
			return false;
		}
		else if (amount > maxDep && maxDep > -1)
		{
			player.sendMessage(ChatColor.RED
					+ "The maximum amount to deposit is " + ChatColor.YELLOW
					+ maxDep + ChatColor.RED + " levels");
			return false;
		}
		else if (newBalance > allowedBalance)
		{
			player.sendMessage(ChatColor.RED
					+ "Your balance would exceed the maximum allowed balance, which is: " + 
					ChatColor.YELLOW + allowedBalance + ChatColor.RED + " levels");
			return false;
		}
		else
		{
			return true;
		}
	}

	// Check if the withdrawn amount is within the limits
	private boolean checkWithdrawLimits(Player player, int amount)
	{
		String stringMinWit = plugin.getConfig().getString(
				"Account_Limits.Min_Withdraw");
		String stringMaxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");

		minWit = Integer.parseInt(stringMinWit);
		maxWit = Integer.parseInt(stringMaxWit);
		
		if (amount < 0)
		{
			player.sendMessage(ChatColor.RED
					+ "You cannot withdraw a negative amount!");
			return false;
		}
		else if (minWit == -1 && maxWit == -1)
		{
			return true;
		}
		else if (amount < minWit && minWit != -1)
		{
			player.sendMessage(ChatColor.RED
					+ "The minimum amount to withdraw is " + ChatColor.YELLOW
					+ minWit + ChatColor.RED + " levels");
			return false;
		}
		else if (amount > maxWit && maxWit > -1)
		{
			player.sendMessage(ChatColor.RED
					+ "The maximum amount to withdraw is " + ChatColor.YELLOW
					+ maxWit + ChatColor.RED + " levels");
			return false;
		}
		else
		{
			return true;
		}
	}

	// Get the group of the specified world
	private String getGroup(String world)
	{
		// XXX getGroup(String world)
		groupList = plugin.getConfig().getConfigurationSection("World_Groups")
				.getKeys(false);

		for (String group : groupList)
		{
			Set<String> worldsInGroup = plugin.getConfig()
					.getConfigurationSection("World_Groups." + group)
					.getKeys(true);

			for (String thisWorld : worldsInGroup)
			{
				String worldName = plugin.getConfig().getString(
						"World_Groups." + group + "." + thisWorld);
				if (world.equals(worldName))
				{
					return group;
				}
			}
		}

		return "Excluded_Worlds";
	}

	public void itIsExcluded(Player player)
	{
		// XXX itIsExcluded(Player player)
		player.sendMessage(ChatColor.RED
				+ "The world you are requesting is excluded from the LvL Banking system!");
	}
	
	public boolean validConnection(Player player)
	{
		try
		{
			if(!con.isValid(3))
			{
				player.sendMessage(ChatColor.RED + "There is curently no connection to the bank! Contact an Admin for help");
				return false;
			}
			else
			{
				return true;
			}
		}
		catch (SQLException sqlExc)
		{
			sqlExc.printStackTrace();
			return false;
		}
	}
}