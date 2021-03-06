package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.bukkit.plugin.Plugin;

public class DbConnection 
{
	private static String sqlURL = "";
	private static String sqlPORT = "";
	private static String sqlDBNAME = "";
	private static String sqlUSER = "";
	private static String sqlPASS = "";
	private Set<String> groupList;
	private static Connection con;
	private Plugin plugin;
	
	private static DbConnection dbcon;
	
	private DbConnection(Plugin plugin)
	{
		this.plugin = plugin;
		con = openConnection();
	}
	
	public static DbConnection getInstance(Plugin plugin)
	{
		if(dbcon == null)
		{
			dbcon = new DbConnection(plugin);
		}
		return dbcon;
	}
	
	public synchronized Connection openConnection()
	{
		//SQL parameters
		sqlURL = plugin.getConfig().getString("MySQL.SQL_IP");
		sqlPORT = plugin.getConfig().getString("MySQL.SQL_Port");
		sqlDBNAME = plugin.getConfig().getString("MySQL.SQL_Database_Name");
		sqlUSER = plugin.getConfig().getString("MySQL.SQL_User_Name");
		sqlPASS = plugin.getConfig().getString("MySQL.SQL_Password");
		
		//Open connection
		try
		{
			con = DriverManager.getConnection("jdbc:mysql://" + sqlURL + ":" + sqlPORT + "/" + sqlDBNAME, sqlUSER, sqlPASS);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return con;
	}
	
	//Create table
	public void createTable()
	{
		try 
		{
			con = validateCon();
			PreparedStatement createTable = con.prepareStatement("CREATE TABLE lvl_bank_accounts(playerName varchar(255), PRIMARY KEY (playerName));");
			createTable.executeUpdate();
		} 
		catch (SQLException e) 
		{
			plugin.getLogger().info("[LvL_Bank] Table already exists!");
		}
	}
	
	//Expand table (If new groups was added to Config)
	public void expandTable()
	{
		groupList = plugin.getConfig().getConfigurationSection("World_Groups").getKeys(false);
		
		for(String group : groupList)
		{
			try
			{
				con = validateCon();
				PreparedStatement addGroup = con.prepareStatement("ALTER TABLE lvl_bank_accounts ADD " + group + " int default 0;");
				addGroup.executeUpdate();
			}
			catch (SQLException e)
			{
				plugin.getLogger().info("Column '" + group + "' was not added! Does it already exist?");
			}
		}
	}
	
	//Execute DB call to put data in the DB
	public boolean executeDBStringPut(String query)
	{
		con = validateCon();
		
		try
		{
			PreparedStatement depositAmount = con.prepareStatement(query);
			depositAmount.executeUpdate();
			return true;
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		
		return false;
	}
	
	//Execute DB call to get data from the DB
	public ResultSet executeDBStringGet(String query)
	{
		ResultSet rs = null;
		con = validateCon();
		
		try
		{
			PreparedStatement prep = con.prepareStatement(query);
			rs = prep.executeQuery();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		return rs;
	}
	
	//Validate the connection
	public Connection validateCon()
	{
		try
		{
			if(!con.isValid(1))
			{
				con = openConnection();
			}
		}
		catch(SQLException sqlE)
		{
			sqlE.printStackTrace();
		}
		return con;
	}
}
