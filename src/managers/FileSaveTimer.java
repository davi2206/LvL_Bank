package managers;

import org.bukkit.scheduler.BukkitRunnable;

public class FileSaveTimer extends BukkitRunnable
{
	private BankManagement bank;
	
	public FileSaveTimer(BankManagement bm)
	{
		bank = bm;
	}
	
	public void run()
	{
		bank.save();
	}
}
