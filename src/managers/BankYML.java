package managers;

import java.io.File;
import java.io.IOException;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BankYML implements BankManagement
{
	public static File folder;
	public static File bankFile;
	public static FileConfiguration tempFile;

	private Plugin plugin;
	private static BankYML bankMan;

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

	// XXX Constructor WITH db
	private BankYML(Plugin plugin)
	{
		this.plugin = plugin;

		try
		{
			createFile();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			plugin.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.RED
									+ "Bank file or folder could not be created!");
		}
	}

	// XXX Get instance
	public static BankYML getInstance(Plugin plugin)
	{
		if (bankMan == null)
		{
			bankMan = new BankYML(plugin);
		}
		return bankMan;
	}

	// XXX deposit(Player player, int amount)
	@Override
	public boolean deposit(Player player, int amount)
	{
		if(amount == -1)
		{
			amount = player.getLevel();
		}
		
		defineValues(player, amount);

		if (accountOverflow > 0)
		{
			amount = (amount - accountOverflow);
			player.sendMessage(ChatColor.DARK_AQUA
					+ "The amount to deposit was scaled down to "
					+ ChatColor.YELLOW + amount + ChatColor.DARK_AQUA
					+ " levels to not exceed the account limit");
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

		if (amount > player.getLevel())
		{
			player.sendMessage(ChatColor.RED
					+ "You do not have enough levels for that..");
			return true;
		}

		int lvl = newBalance;

		tempFile.set("Players." + playerName + "." + group, lvl);

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

	// XXX deposit (The maximum amount)
	@Override
	public boolean deposit(Player player)
	{
		int lvl = player.getLevel();

		defineValues(player, lvl);
		return deposit(player, maxDep);
	}

	// XXX withdraw(Player player, int amount)
	@Override
	public boolean withdraw(Player player, int amount)
	{
		if (!checkWithdrawLimits(player, amount))
		{
			return false;
		}
		
		if(amount == -1)
		{
			amount = getBalance(player);
		}
		
		playerName = player.getName();
		world = player.getWorld().getName();
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return false;
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
		
		if(allowedPlayerLevel == -1)
		{
			allowedPlayerLevel = Integer.MAX_VALUE;
		}

		if (player.getLevel() >= allowedPlayerLevel)
		{
			player.sendMessage(ChatColor.YELLOW
					+ "You have the allowed amount of levels. Use some before withdrawing more!");
			return false;
		}
		else if (newPlayerLevel > allowedPlayerLevel)
		{
			int newAmount = (allowedPlayerLevel - (player.getLevel()));
			withdraw(player, newAmount);
			return true;
		}

		int newlvl = newBalance;

		tempFile.set("Players." + playerName + "." + group, newlvl);

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

	// XXX withdraw (The maximum amount)
	@Override
	public boolean withdraw(Player player)
	{
		String stringMaxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");

		maxWit = Integer.parseInt(stringMaxWit);
		
		return withdraw(player, maxWit);
	}

	// XXX getBalance(Player player)
	@Override
	public int getBalance(Player player)
	{
		playerName = player.getName();
		this.world = player.getWorld().getName();
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return (-2);
		}

		int lvlFromFile = 0;

		lvlFromFile = tempFile.getInt("Players." + playerName + "." + group, 0);

		return lvlFromFile;
	}

	// XXX getBalance(Player player, String world)
	@Override
	public int getBalance(Player player, String world)
	{
		playerName = player.getName();
		this.world = world;
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(player);
			return (-2);
		}

		int lvlFromFile = 0;
		lvlFromFile = tempFile.getInt("Players." + playerName + "." + group, 0);

		return lvlFromFile;
	}

	// XXX getBalance(CommandSender sender, String player, String world)
	@Override
	public int getBalance(CommandSender sender, String player, String world)
	{
		playerName = player;
		this.world = world;
		group = getGroup(world);

		if (group.equals("Excluded_Worlds"))
		{
			itIsExcluded(sender);
			return (-2);
		}

		int lvlFromFile = 0;
		lvlFromFile = tempFile.getInt("Players." + playerName + "." + group, 0);

		return lvlFromFile;
	}

	// XXX Define values
	@Override
	public void defineValues(Player player, int amount)
	{
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

	// XXX checkDepositLimits(Player player, int amount)
	// Check if the depositd amount is within the limits
	@Override
	public boolean checkDepositLimits(Player player, int amount)
	{
		if(amount == -1)
		{
			return true;
		}
		else if (amount < 0)
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

	// XXX checkWithdrawLimits(Player player, int amount)
	// Check if the withdrawn amount is within the limits
	@Override
	public boolean checkWithdrawLimits(Player player, int amount)
	{
		String stringMinWit = plugin.getConfig().getString(
				"Account_Limits.Min_Withdraw");
		String stringMaxWit = plugin.getConfig().getString(
				"Account_Limits.Max_Withdraw");

		minWit = Integer.parseInt(stringMinWit);
		maxWit = Integer.parseInt(stringMaxWit);

		if(amount == -1)
		{
			return true;
		}
		else if (amount < 0)
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

	// XXX getGroup(String world)
	// Get the group of the specified world
	@Override
	public String getGroup(String world)
	{
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

	// XXX itIsExcluded(Player player)
	@Override
	public void itIsExcluded(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED
				+ "The world you are requesting is excluded from the LvL Banking system!");
	}

	// XXX transaction did not work
	@Override
	public void didNotWork(CommandSender cs)
	{
		cs.sendMessage(ChatColor.RED
				+ "There seems to be a problem with the connection to the bank servers.");
	}

	// XXX createFile()
	private void createFile() throws IOException, InvalidConfigurationException
	{
		folder = plugin.getDataFolder();
		bankFile = new File(folder, "BankAccounts.yml");
		tempFile = new YamlConfiguration();

		if (!folder.exists())
		{
			folder.mkdir();
		}

		if (!bankFile.exists())
		{
			bankFile.createNewFile();
		}

		tempFile.load(bankFile);
	}

	// XXX save() Saves data to actual file
	@Override
	public void save()
	{
		try
		{
			tempFile.save(bankFile);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
