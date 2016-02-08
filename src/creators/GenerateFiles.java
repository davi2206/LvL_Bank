package creators;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class GenerateFiles
{
	private Plugin plugin;
	private ConsoleCommandSender ccs;
	
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
			ccs.sendMessage(ChatColor.YELLOW + "Generating config file...");
			
			plugin.saveDefaultConfig();
			
			ccs.sendMessage(ChatColor.YELLOW + "Config file generated");
			ccs.sendMessage(ChatColor.YELLOW + "LvL_Bank: Default config should be edited!");
		}
		else
		{
			String version = plugin.getConfig().getString("Version");
			
			if(version.equalsIgnoreCase(curVersion))
			{
				ccs.sendMessage(ChatColor.GREEN + "Config file already exists");
				ccs.sendMessage(ChatColor.GREEN + "Config version is " + version);
			}
			else
			{
				ccs.sendMessage(ChatColor.YELLOW + "Config file already exists, but is outdated!");
				ccs.sendMessage(ChatColor.YELLOW + "Current config version is " + version);
				ccs.sendMessage(ChatColor.YELLOW + "New config version is " + curVersion);
				
				ccs.sendMessage(ChatColor.GREEN + "New config will be generated!");
				
				File oldFile = new File(plugin.getDataFolder() + File.separator + "OLD config.yml");
				configFile.renameTo(oldFile);
				
				generateConfig(curVersion);
			}
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
