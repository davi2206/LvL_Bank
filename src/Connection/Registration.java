package Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import java.sql.Connection;

public class Registration 
{
	private Connection con;
	private DbConnection dbCon;
	private PreparedStatement prep;
	private ResultSet rs;
	private static Registration registration;
	
	private Registration(DbConnection dbCon)
	{
		this.dbCon = dbCon;
	}
	
	public static Registration getInstance(DbConnection dbCon)
	{
		if(registration == null)
		{
			registration = new Registration(dbCon);
		}
		return registration;
	}

	public boolean isRegistered(Player p)
	{
		try 
		{
			con = dbCon.validateCon();
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
			con = dbCon.validateCon();
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
			con = dbCon.validateCon();
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
