package Connection;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Transfere
{
	private Plugin plugin;
	private DbConnection dbcon;
	private Set<String> groupList;

	public static File folder;
	public static File bankFile;
	public static FileConfiguration tempFile;
	public static File configFile;
	public static FileConfiguration tempConfigFile;

	public Transfere(Plugin plugin)
	{
		this.plugin = plugin;
		try
		{
			createFile();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		dbcon = DbConnection.getInstance(plugin);
	}

	public boolean sendAllToFile()
	{
		String query = "select * from sql294694.lvl_bank_accounts;";

		groupList = plugin.getConfig().getConfigurationSection("World_Groups")
				.getKeys(false);

		try
		{
			ResultSet rs = dbcon.executeDBStringGet(query);
			String playerName;
			while (rs.next())
			{
				playerName = rs.getString("playerName");

				for (String group : groupList)
				{
					int lvl = rs.getInt(group);
					lvl += tempFile.getInt("Players." + playerName + "."
							+ group);
					tempFile.set("Players." + playerName + "." + group, lvl);
				}
				tempFile.save(bankFile);
				editConfig();
			}
			return true;
		}
		catch (SQLException | IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}

		return false;
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

	// XXX editConfig()
	private void editConfig() throws IOException, InvalidConfigurationException
	{
		folder = plugin.getDataFolder();
		configFile = new File(folder, "config.yml");
		tempConfigFile = new YamlConfiguration();

		if (!folder.exists())
		{
			folder.mkdir();
		}

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		tempConfigFile.load(configFile);
		
		tempConfigFile.set("MySQL.use_SQL", false);
		tempConfigFile.save(configFile);
	}
}
