package managers;

import java.sql.Connection;

import me.davi2206.LvLBank.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class SignManager implements Listener
{
	private BankManagement bm;
	private static SignManager signMan;
	private Plugin plugin;
	
	private String unlimitedSign = "";
	private String stringMinDep = "";
	private String stringMaxDep = "";
	private String stringMinWit = "";
	private String stringMaxWit = "";
	
	private SignManager(Connection con, Plugin plugin)
	{
		bm = BankManagement.getInstance(con, plugin);
		this.plugin = plugin;
	}
	
	public static SignManager getInstance(Connection con, Plugin plugin)
	{
		if(signMan == null)
		{
			signMan = new SignManager(con, plugin);
		}
		return signMan;
	}
	
	@EventHandler
	public void signCreate(SignChangeEvent place)
	{
		//XXX Sign create
		if(place.getPlayer().hasPermission(new Permissions().lvlBankSignPlace) && 
			place.getLine(0).equalsIgnoreCase("[LvL Bank]") && 
			!place.getLine(1).equals(""))
		{
			place.setLine(0, ChatColor.GREEN + "" + ChatColor.BOLD + "** LvL Bank **");
			
			defineMinsAndMaxs();
			
			int minDep = 0;
			int maxDep = 0;
			int minWit = 0;
			int maxWit = 0;
			
			if(place.getLine(1).equalsIgnoreCase("deposit"))
			{
				place.setLine(1, ChatColor.BLUE + "Deposit");
				if(!place.getLine(2).equals("") && !place.getLine(2).equalsIgnoreCase("max"))
				{
					int i = 0;
					
					try
					{
						i = Integer.parseInt(place.getLine(2));
						minDep = Integer.parseInt(stringMinDep);
						maxDep = Integer.parseInt(stringMaxDep);
					}
					catch(Exception e)
					{
						place.setCancelled(true);
						place.getPlayer().sendMessage(ChatColor.RED + "The amount must be a number!");
						return;
					}
					
					if(i < minDep || i > maxDep)
					{
						place.setCancelled(true);
						place.getPlayer().sendMessage(ChatColor.RED + "The amount must be within the range of the minimum and maximum values!");
						return;
					}
					place.setLine(2, ChatColor.YELLOW + "Amount: " + ChatColor.BLACK + i);
				}
				else
				{
					place.setLine(2, ChatColor.YELLOW + "Max");
				}
				
				place.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinDep + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxDep);
			}
			
			else if(place.getLine(1).equalsIgnoreCase("Withdraw"))
			{
				place.setLine(1, ChatColor.RED + "Withdraw");
				if(!place.getLine(2).equals("") && !place.getLine(2).equalsIgnoreCase("max"))
				{
					int i = 0;
					try
					{
						i = Integer.parseInt(place.getLine(2));
						minWit = Integer.parseInt(stringMinWit);
						maxWit = Integer.parseInt(stringMaxWit);
					}
					catch(Exception e)
					{
						place.setCancelled(true);
						place.getPlayer().sendMessage(ChatColor.RED + "The amount must be a number!");
					}
					
					if(i < minWit || i > maxWit)
					{
						place.setCancelled(true);
						place.getPlayer().sendMessage(ChatColor.RED + "The amount must be within the range of the minimum and maximum values!");
						return;
					}
					place.setLine(2, ChatColor.YELLOW + "Amount: " + ChatColor.BLACK + i);
				}
				else
				{
					place.setLine(2, ChatColor.YELLOW + "Max");
				}
				
				place.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinWit + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxWit);
			}
			else if(place.getLine(1).equalsIgnoreCase("Balance"))
			{
				place.setLine(1, ChatColor.BLUE + "Balance");
			}
			else
			{
				place.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		//XXX Sign Click
		Player player = event.getPlayer();
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && 
			(event.getClickedBlock().getType() == Material.WALL_SIGN || 
			event.getClickedBlock().getType() == Material.SIGN_POST) &&
			event.getPlayer().hasPermission(new Permissions().lvlBankSignUse))
		{
			Sign sign = (Sign) event.getClickedBlock().getState();
			
			defineMinsAndMaxs();
			
			if(sign.getLine(0).equals(ChatColor.GREEN + "" + ChatColor.BOLD + "** LvL Bank **"))
			{
				if(sign.getLine(1).equals(ChatColor.BLUE + "Deposit"))
				{
					if(sign.getLine(2).equals(ChatColor.YELLOW + "Max"))
					{
						sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinDep + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxDep);
						sign.update();
						
						if(bm.deposit(player))
						{
							sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinDep + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxDep);
							sign.update();
							return;
						}
					}
					else if(sign.getLine(2).substring(0, 9).equals((ChatColor.YELLOW + "Amount:")))
					{
						String amountString = ChatColor.stripColor(sign.getLine(2).substring(9).trim());
						
						sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinDep + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxDep);
						sign.update();
						
						int amount = Integer.parseInt(amountString);
						if(bm.deposit(player, amount))
						{
							sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinDep + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxDep);
							sign.update();
							return;
						}
					}
				}
				else if(sign.getLine(1).equals(ChatColor.RED + "Withdraw"))
				{
					if(sign.getLine(2).equals(ChatColor.YELLOW + "Max"))
					{
						sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinWit + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxWit);
						sign.update();
						
						if(bm.withdraw(player))
						{
							sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinWit + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxWit);
							sign.update();
							return;
						}
					}
					else if(sign.getLine(2).substring(0, 9).equals(ChatColor.YELLOW + "Amount:"))
					{
						String amountString = ChatColor.stripColor(sign.getLine(2).substring(9).trim());
						
						sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinWit + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxWit);
						sign.update();
						
						int amount = Integer.parseInt(amountString);
						if(bm.withdraw(player, amount))
						{
							sign.setLine(3, ChatColor.YELLOW + "Min/Max: " + ChatColor.GREEN + stringMinWit + ChatColor.YELLOW + "/" + ChatColor.GREEN + stringMaxWit);
							sign.update();
							return;
						}
					}
				}
				else if(sign.getLine(1).equals(ChatColor.BLUE + "Balance"))
				{
					int level = bm.getBalance(player);
					
					if(level == (-9001))
					{
						return;
					}
					
					String lvl = ("" + level);
					
					sign.setLine(2, (ChatColor.YELLOW + "Player: " + ChatColor.DARK_BLUE + player.getName()));
					sign.setLine(3, (ChatColor.YELLOW + "Levels: " + ChatColor.GREEN + lvl));
					
					if(level > 1 || level == 0)
					{
						player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN + lvl + ChatColor.BLUE + " levels in the Bank");
					}
					else if(level == 1)
					{
						player.sendMessage(ChatColor.BLUE + "You have " + ChatColor.GREEN + lvl + ChatColor.BLUE + " level in the Bank");
					}
					sign.update();
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void signBreakEvent(BlockBreakEvent bbe)
	{
		//XXX Sign Break
		Player player = bbe.getPlayer();
		
		if(bbe.getBlock().getState() instanceof Sign)
		{
			Sign sign = (Sign) bbe.getBlock().getState();
			
			if(sign.getLine(0).equals(ChatColor.GREEN + "" + ChatColor.BOLD + "** LvL Bank **"))
			{
				if(!player.hasPermission(new Permissions().lvlBankSignBreak))
				{
					bbe.setCancelled(true);
				}
				else
				{
					player.sendMessage(ChatColor.BLUE + "Bank sign broken!");
				}
			}
		}
	}
	
	public void defineMinsAndMaxs()
	{
		unlimitedSign = plugin.getConfig().getString("Account_Limits.Sign_For_Unlimited");
		stringMinDep = plugin.getConfig().getString("Account_Limits.Min_Deposit");
		stringMaxDep = plugin.getConfig().getString("Account_Limits.Max_Deposit");
		stringMinWit = plugin.getConfig().getString("Account_Limits.Min_Withdraw");
		stringMaxWit = plugin.getConfig().getString("Account_Limits.Max_Withdraw");
		
		if(stringMinDep.equals("-1"))
		{
			stringMinDep = unlimitedSign;
		}
		if(stringMaxDep.equals("-1"))
		{
			stringMaxDep = unlimitedSign;
		}
		if(stringMinWit.equals("-1"))
		{
			stringMinWit = unlimitedSign;
		}
		if(stringMaxWit.equals("-1"))
		{
			stringMaxWit = unlimitedSign;
		}
	}
}