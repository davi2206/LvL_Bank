package managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface BankManagement
{
	// XXX deposit(Player player, int amount)
	public boolean deposit(Player player, int amount);
	public boolean deposit(Player player);
	
	// XXX withdraw(Player player, int amount)
	public boolean withdraw(Player player, int amount);

	// XXX withdraw (The maximum amount)
	public boolean withdraw(Player player);

	// XXX getBalance(Player player)
	public int getBalance(Player player);

	// XXX getBalance(Player player, String world)
	public int getBalance(Player player, String world);

	// XXX getBalance(Player sender, String player, String world)
	public int getBalance(CommandSender sender, String player, String world);

	//XXX Define values
	void defineValues(Player player, int amount);
	
	//XXX checkDepositLimits(Player player, int amount)
	// Check if the depositd amount is within the limits
	boolean checkDepositLimits(Player player, int amount);

	// Check if the withdrawn amount is within the limits
	boolean checkWithdrawLimits(Player player, int amount);

	// XXX getGroup(String world)
	// Get the group of the specified world
	String getGroup(String world);

	// XXX itIsExcluded(Player player)
	public void itIsExcluded(CommandSender sender);
	
	// XXX transaction did not work
	void didNotWork(CommandSender p);
	
	// XXX Save on disable
	void save();
}