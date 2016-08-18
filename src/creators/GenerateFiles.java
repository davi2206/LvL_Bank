package creators;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class GenerateFiles
{
	private Plugin plugin;
	private ConsoleCommandSender ccs;
	private File oldFile;
	
	//Values for updated config:
	String useSql = "MySQL.use_SQL";
	String sqlURL = "MySQL.SQL_IP";
	String sqlPORT = "MySQL.SQL_Port";
	String sqlDBNAME = "MySQL.SQL_Database_Name";
	String sqlUSER = "MySQL.SQL_User_Name";
	String sqlPASS = "MySQL.SQL_Password";
	HashMap<String, Set<String>> worldMap = new HashMap<>();
	Set<String> worldsInGroup;
	String minDep = "Account_Limits.Min_Deposit";
	String maxDep = "Account_Limits.Max_Deposit";
	String minWit ="Account_Limits.Min_Withdraw";
	String maxWit = "Account_Limits.Max_Withdraw";
	String unlim = "Account_Limits.Sign_For_Unlimited";
	String maxBal = "Account_Limits.Max_Account_Balance";
	String maxPlLvl = "Player_Limits.Max_Player_Level";
	
	public GenerateFiles(Plugin plugin)
	{
		this.plugin = plugin;
		ccs = plugin.getServer().getConsoleSender();
	}

	//Generate the Configuration file, if it is not there
	// XXX Config file
	public void generateConfig(String curVersion)
	{
		File configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		
		if(!configFile.exists())
		{
			ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Generating config file...");
			
			plugin.saveDefaultConfig();
			
			ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Config file generated");
			ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Default config should be edited!");
		}
		else
		{
			String configVersion = plugin.getConfig().getString("Version");
			
			if(configVersion.equalsIgnoreCase(curVersion))
			{
				ccs.sendMessage(ChatColor.GREEN + "[LvL_Bank] Config file already exists");
				ccs.sendMessage(ChatColor.GREEN + "[LvL_Bank] Config version is " + configVersion);
			}
			else
			{
				ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Config file already exists, but is outdated!");
				ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] Current config version is " + configVersion);
				ccs.sendMessage(ChatColor.YELLOW + "[LvL_Bank] New config version is " + curVersion);
				
				ccs.sendMessage(ChatColor.GREEN + "[LvL_Bank] New config will be generated!");
				
				saveData();
				
				oldFile = new File(plugin.getDataFolder() + File.separator + "OLD config.yml");
				configFile.renameTo(oldFile);
				
				generateConfig(curVersion);
				ccs.sendMessage(ChatColor.GREEN + "[LvL_Bank] Moving config values to new config");
				moveDataToNewConfig();
			}
		}
	}
	
	private void saveData()
	{
		Set<String> groupList = plugin.getConfig()
				.getConfigurationSection("World_Groups")
				.getKeys(false);
		
		for (String group : groupList)
		{
			worldsInGroup = plugin.getConfig()
					.getConfigurationSection("World_Groups." + group)
					.getKeys(true);
			worldMap.put(group, worldsInGroup);
		}
		
		ArrayList<String> list = new ArrayList<>();
		list.add(useSql);
		list.add(sqlURL);
		list.add(sqlPORT);
		list.add(sqlDBNAME);
		list.add(sqlUSER);
		list.add(sqlPASS);
		list.add(minDep);
		list.add(maxDep);
		list.add(minWit);
		list.add(maxWit);
		list.add(unlim);
		list.add(maxBal);
		
		for(String entry : list)
		{
			try
			{
				entry = plugin.getConfig().getString(entry);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	//Move the data from the old config to the new config
	//XXX Move data
	private void moveDataToNewConfig()
	{
		try
		{
			this.plugin.getConfig().set("MySQL.use_SQL", useSql);
			this.plugin.getConfig().set("MySQL.SQL_IP", sqlURL);
			this.plugin.getConfig().set("MySQL.SQL_Port", sqlPORT);
			this.plugin.getConfig().set("MySQL.SQL_Database_Name", sqlDBNAME);
			this.plugin.getConfig().set("MySQL.SQL_User_Name", sqlUSER);
			this.plugin.getConfig().set("MySQL.SQL_Password", sqlPASS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			//PSEUDO::
			for(Entry<String, Set<String>> ent : worldMap.entrySet())
			{
				String group = ent.getKey();
				for(String world : worldMap.get(group))
				{
					int nr = 1;
					
					this.plugin.getConfig().set("World_Groups." + group + ".'" + nr + "'", world);
					
					nr++;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			this.plugin.getConfig().set("Account_Limits.Min_Deposit", minDep);
			this.plugin.getConfig().set("Account_Limits.Max_Deposit", maxDep);
			this.plugin.getConfig().set("Account_Limits.Min_Withdraw", minWit);
			this.plugin.getConfig().set("Account_Limits.Max_Withdraw", maxWit);
			this.plugin.getConfig().set("Account_Limits.Sign_For_Unlimited", unlim);
			this.plugin.getConfig().set("Account_Limits.Max_Account_Balance", maxBal);
			this.plugin.getConfig().set("Player_Limits.Max_Player_Level", maxPlLvl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Generate changelog.txt
	//XXX Changelog file
	public void generateChangelog()
	{
		plugin.saveResource("Associated files/changelog.txt", true);
	}
	
	//Generate Donators.txt
	//XXX Donators file
	public void generateDonators()
	{
		plugin.saveResource("Associated files/Donators.txt", true);
	}
}
