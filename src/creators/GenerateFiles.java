package creators;

import java.io.File;
import java.io.PrintWriter;

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
	public void generateConfig()
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
			ccs.sendMessage(ChatColor.GREEN + "Config file already exists");
		}
	}
	
	//Generate changelog.txt
	//XXX Changelog file
	public void generateChangelog()
	{
		try
		{
			PrintWriter writer = new PrintWriter((plugin.getDataFolder() + File.separator + "changelog.txt"), "UTF-8");
			writer.println("** CHANGELOG **");
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		plugin.saveResource("changelog.txt", true);
	}
	
	//Generate Donators.txt
	//XXX Donators file
	public void generateDonators()
	{
		try
		{
			PrintWriter writer = new PrintWriter((plugin.getDataFolder() + File.separator + "Donators.txt"), "UTF-8");
			writer.println("Donators");
			writer.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		plugin.saveResource("Donators.txt", true);
	}
}
