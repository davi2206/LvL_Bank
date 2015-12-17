package Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import java.sql.Connection;

public class Registration 
{
	private Connection con;
	private PreparedStatement prep;
	private ResultSet rs;
	private static Registration registration;
	
	private Registration(Connection con)
	{
		this.con = con;
	}
	
	public static Registration getInstance(Connection con)
	{
		if(registration == null)
		{
			registration = new Registration(con);
		}
		return registration;
	}

	public boolean isRegistered(Player p)
	{
		try 
		{
			prep = con.prepareStatement("SELECT * FROM lvl_bank_accounts WHERE playerName = '" + p.getName() + "';");
			rs = prep.executeQuery();
			
			while(rs.next())
			{
				if(rs.getString("playerName").equals(p.getName()))
				{
					return true;
				}
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean isRegistered(String player)
	{
		try 
		{
			prep = con.prepareStatement("SELECT * FROM lvl_bank_accounts WHERE playerName = '" + player + "';");
			rs = prep.executeQuery();
			
			while(rs.next())
			{
				if(rs.getString("playerName").equals(player))
				{
					return true;
				}
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}

	public void registerPlayer(Player p, String group)
	{
		String sql = null;
		try
		{
			sql = "INSERT INTO lvl_bank_accounts (playerName) VALUES('" + p.getName() + "');";
			prep = con.prepareStatement(sql);
			prep.executeUpdate();
		}
		catch(SQLException sqlE)
		{
			sqlE.printStackTrace();
		}
		p.sendMessage("New account for " + p.getName() + " created");
	}
}
