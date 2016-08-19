package creators;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class GenerateFiles
{
	private Plugin plugin;
	private ConsoleCommandSender ccs;
	private File configFile;
	private File oldFile;
	
	public GenerateFiles(Plugin plugin)
	{
		this.plugin = plugin;
		ccs = plugin.getServer().getConsoleSender();
	}

	//Generate the Configuration file, if it is not there
	// XXX Config file
	public void generateConfig(String curVersion)
	{
		configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		
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
				
				oldFile = new File(plugin.getDataFolder() + File.separator + "OLD config.yml");
				if(oldFile.exists())
				{
					oldFile.delete();
				}
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
