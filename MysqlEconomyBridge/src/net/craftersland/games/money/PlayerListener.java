package net.craftersland.games.money;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener{
	
	private Money money;
	public static Economy econ = null;
	@SuppressWarnings("unused")
	private ConfigurationHandler coHa;

	public PlayerListener(Money money) {
		this.money = money;
		this.coHa = money.getConfigurationHandler();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e){
		String com = e.getMessage();
		Player player = e.getPlayer();
		String[] splitted = com.split(" ");
		if (splitted[0].equals("money") || splitted[0].equals("balance")) {
			Money.econ.withdrawPlayer(player, Money.econ.getBalance(player));
			Money.econ.depositPlayer(player, money.getMoneyDatabaseInterface().getBalance(player.getUniqueId()));
		}
		else if (splitted[0].equals("economy") || splitted[0].equals("eco")){
			int toDB = (int)Money.econ.getBalance(player);
			int value = Integer.parseInt(splitted[3]);
			switch (splitted[1]){
				case "set":
					toDB = value;
					break;
				case "give":
					toDB += value;
					break;
				case "take":
					toDB -= value;
					break;
				case "reset":
					toDB = 0;
					break;
			}
			money.getMoneyDatabaseInterface().setBalance(player.getUniqueId(), (double)toDB);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLogin(final PlayerJoinEvent event) {

		//Check if player has mysql an account first
		if (!money.getMoneyDatabaseInterface().hasAccount(event.getPlayer().getUniqueId()))
		{
			return;
		}
		//Added a small delay to prevent the onDisconnect handler overlapping onLogin on a BungeeCord configuration when switching servers.
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(money, new Runnable() {

			@Override
			public void run() {
				
				//Set local balance to 0 before depositing the mysql balance
				if (Money.econ.getBalance(event.getPlayer()) > 0) 
				{
					Money.econ.withdrawPlayer(event.getPlayer(), Money.econ.getBalance(event.getPlayer()));
				}
				
				//Set mysql balance to local balance
				Money.econ.depositPlayer(event.getPlayer(), money.getMoneyDatabaseInterface().getBalance(event.getPlayer().getUniqueId()));
				
			}
		}, 20L);

	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		
		//Check if local balance is 0
		if (Money.econ.getBalance(event.getPlayer()) == 0)
		{
			return;
		}
		//Set local balance on mysql balance
		money.getMoneyDatabaseInterface().setBalance(event.getPlayer().getUniqueId(), Money.econ.getBalance(event.getPlayer()));

	}

}
