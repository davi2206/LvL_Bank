package Connection;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Registration 
{
	private DbConnection dbCon;
	private ResultSet rs;
	private static Registration registration;
	
	// XXX Constructor
	private Registration(DbConnection dbCon)
	{
		this.dbCon = dbCon;
	}
	
	// XXX Get instance
	public static Registration getInstance(DbConnection dbCon)
	{
		if(registration == null)
		{
			registration = new Registration(dbCon);
		}
		return registration;
	}

	// XXX Check if player is registered
	public boolean isRegistered(Player p)
	{
		String queryGetPlayer = ("SELECT * FROM lvl_bank_accounts WHERE playerName = '" 
				+ p.getName() + "';");
		
		rs = dbCon.executeDBStringGet(queryGetPlayer);
		
		try 
		{
			while(rs.next())
			{
				if(rs.getString("playerName").equals(p.getName()))
				{
					return true;
				}
			}
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	// XXX Check if player name is registered
	public boolean isRegistered(String player)
	{
		String queryGetPlayer = ("SELECT * FROM lvl_bank_accounts WHERE playerName = '" + player + "';");
		
		rs = dbCon.executeDBStringGet(queryGetPlayer);
		
		try 
		{
			while(rs.next())
			{
				if(rs.getString("playerName").equals(player))
				{
					return true;
				}
			}
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
		
		return false;
	}

	// XXX Register player
	public void registerPlayer(Player p, String group)
	{
		String queryRegister = ("INSERT INTO lvl_bank_accounts (playerName) VALUES('" + p.getName() + "');");

		boolean worked = dbCon.executeDBStringPut(queryRegister);
		
		if(worked)
		{
			p.sendMessage(ChatColor.GREEN + "New account for " + p.getName() + " created");
		}
		else
		{
			p.sendMessage(ChatColor.RED + "Account for " + p.getName() + " NOT created");
		}
	}
}
