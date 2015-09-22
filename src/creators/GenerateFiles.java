package creators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class GenerateFiles
{
	private Plugin plugin;
	private ConsoleCommandSender clog;
	
	private Reader reader;
	
	public GenerateFiles(Plugin plugin)
	{
		this.plugin = plugin;
		clog = plugin.getServer().getConsoleSender();
	}

	//Generate the Configuration file, if it is not there
	public void generateConfig()
	{
		File configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		
		if(!configFile.exists())
		{
			clog.sendMessage(ChatColor.YELLOW + "Generating config file...");
			
			plugin.saveDefaultConfig();
			
			clog.sendMessage(ChatColor.YELLOW + "Config file generated");
			clog.sendMessage(ChatColor.YELLOW + "LvL_Bank: Default config must be edited!");
			
			plugin.getPluginLoader().disablePlugin(plugin);
			return;
		}
		else
		{
			clog.sendMessage(ChatColor.GREEN + "Config file already exists");
			return;
		}
	}
	
	//Generate changelog.txt
	//XXX Changelog file
	public void generateChangelog()
	{
		//File changelogFile = new File(plugin.getDataFolder() + File.separator + "changelog.txt");
		
		try
		{
			PrintWriter writer = new PrintWriter((plugin.getDataFolder() + File.separator + "changelog.txt"), "UTF-8");
			writer.println("** CHANGELOG **");
			writer.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		plugin.saveResource("changelog.txt", true);
	}
	
	//Generate Donators.txt
	//XXX Donators file
	public void generateDonators()
	{
		//File donatorsFile = new File(plugin.getDataFolder() + File.separator + "Donators.txt");
		
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
