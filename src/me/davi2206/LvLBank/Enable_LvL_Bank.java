package me.davi2206.LvLBank;

import java.sql.Connection;
import java.sql.SQLException;

import managers.BankManagement;
import managers.ConsoleCommands;
import managers.PlayerCommands;
import managers.SignManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import Connection.DbConnection;
import creators.GenerateFiles;

public class Enable_LvL_Bank extends JavaPlugin implements Listener
{
	private static DbConnection dbCon;
	private Connection con;
	private PlayerCommands pCmds;
	private ConsoleCommands cCmds;
	
	private SignManager signManager;
	private BankManagement bm;
	private GenerateFiles genFiles;
	
	private ConsoleCommandSender clog;
	private Plugin plugin;
	
	public void onEnable()
	{
		plugin = this;
		clog = this.getServer().getConsoleSender();
		clog.sendMessage(ChatColor.BLUE + "LvL_Bank enabeling!");
		
		genFiles = new GenerateFiles(this);
		genFiles.generateConfig();
		genFiles.generateChangelog();
		genFiles.generateDonators();
		
		dbCon = DbConnection.getInstance(this);
		
		signManager = SignManager.getInstance(dbCon, this);
		bm = BankManagement.getInstance(dbCon, this);
		pCmds = PlayerCommands.getInstance();
		cCmds = ConsoleCommands.getInstance(this);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(signManager, this);
		
		cCmds.checkMinMaxValues();
		dbCon.createTable();
		dbCon.expandTable();
		
		clog.sendMessage(ChatColor.GREEN + "LvL_Bank enabeled!");
	}

	//Receiving command input
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
	{
		//Sending Command handling to separate class to uncluster the plugin Main class
		if(sender.hasPermission(new Permissions().lvlBankCommands))
		{
			if(sender instanceof Player)
			{
				pCmds.doCommands(this, bm, sender, cmd, commandLabel, args);
			}
			else
			{
				cCmds.doCommands(this, bm, sender, cmd, commandLabel, args);
			}
			
			return true;
		}

		return false;
	}
	
	public void onDisable()
	{
		clog.sendMessage(ChatColor.RED + "<><><><><><><><><><><><><><><> \n");
		clog.sendMessage(ChatColor.RED + "Disabling LvL_Bank \n");
		clog.sendMessage(ChatColor.RED + "<><><><><><><><><><><><><><><>");
	}
}
