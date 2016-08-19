package me.davi2206.LvLBank;

import managers.BankManagement;
import managers.BankSQL;
import managers.BankYML;
import managers.ConsoleCommands;
import managers.FileSaveTimer;
import managers.PlayerCommands;
import managers.SignManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import Connection.DbConnection;
import creators.GenerateFiles;

public class Enable_LvL_Bank extends JavaPlugin implements Listener
{
	private static DbConnection dbCon;
	private PlayerCommands pCmds;
	private ConsoleCommands cCmds;
	
	private SignManager signManager;
	private BankManagement bm;
	private GenerateFiles genFiles;
	private FileSaveTimer fst;
	
	private ConsoleCommandSender clog;
	
	private String configVersion = "0.5";
	
	public void onEnable()
	{
		clog = this.getServer().getConsoleSender();
		clog.sendMessage(ChatColor.BLUE + "[LvL_Bank] Enabeling!");
		
		genFiles = new GenerateFiles(this);
		genFiles.generateConfig(configVersion);
		genFiles.generateChangelog();
		genFiles.generateDonators();
		
		bm = sqlTrueFalse();
		
		signManager = SignManager.getInstance(bm, this);
		
		pCmds = PlayerCommands.getInstance();
		cCmds = ConsoleCommands.getInstance(this);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(signManager, this);
		
		cCmds.checkMinMaxValues();
		
		clog.sendMessage(ChatColor.GREEN + "[LvL_Bank] Enabeled!");
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
		else
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission to use LvL_Bank commands!");
		}
		
		return false;
	}
	
	public BankManagement sqlTrueFalse()
	{
		BankManagement bank;
		boolean useSql = false;
		
		useSql = this.getConfig().getBoolean("MySQL.use_SQL", false);
		
		if(useSql)
		{
			dbCon = DbConnection.getInstance(this);
			dbCon.validateCon();
			dbCon.createTable();
			dbCon.expandTable();
			bank = BankSQL.getInstance(dbCon, this);
		}
		else
		{
			bank = BankYML.getInstance(this);
			fst = new FileSaveTimer(bank);
			fst.runTaskTimer(this, 0, (5 * (60 * 20)));
		}
		
		return bank;
	}
	
	public void onDisable()
	{
		bm.save();
		
		clog.sendMessage(ChatColor.RED + "<><><><><><><><><><><><><><><> \n");
		clog.sendMessage(ChatColor.RED + "[LvL_Bank] Disabling \n");
		clog.sendMessage(ChatColor.RED + "<><><><><><><><><><><><><><><>");
		
		dbCon = null;
		pCmds = null;
		cCmds = null;
		signManager = null;
		bm = null;
		genFiles = null;
		fst = null;
		clog = null;
	}
}
