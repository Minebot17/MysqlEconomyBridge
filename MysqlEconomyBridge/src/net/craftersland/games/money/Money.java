package net.craftersland.games.money;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import net.craftersland.games.money.database.AccountDatabaseInterface;
import net.craftersland.games.money.database.DatabaseManagerInterface;
import net.craftersland.games.money.database.DatabaseManagerMysql;
import net.craftersland.games.money.database.MoneyMysqlInterface;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Money extends JavaPlugin {
	
	public static Logger log;
	public static Economy econ = null;
	public static ExecutorService execService = null;
	
	private ConfigurationHandler configurationHandler;
	private DatabaseManagerInterface databaseManager;
	private AccountDatabaseInterface<Double> moneyDatabaseInterface;
	
	@Override
    public void onEnable(){
    	log = getLogger();
    	log.info("Loading Money Games/Lobby "+getDescription().getVersion()+"... ");
    	
    	//Create Money folder
    	(new File("plugins"+System.getProperty("file.separator")+"Money")).mkdir();
    	
    	
    	//Setup Vault for economy and permissions
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled! Vault installed? If yes Economy system installed?)", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    	
    	//Load Configuration
        configurationHandler = new ConfigurationHandler(this);
        
      //Initiate Threadpool
        execService = Executors.newFixedThreadPool(Integer.parseInt(configurationHandler.getString("database.maximumThreads")));
        
      //Setup Database
        //if (configurationHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
        	log.info("Using MYSQL as Datasource...");
        	databaseManager = new DatabaseManagerMysql(this);
        	moneyDatabaseInterface = new MoneyMysqlInterface(this);
        //}
          
      //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerListener(this), this);
    	
    	log.info("Money mysql has been successfully loaded!");
	}
	
	@Override
    public void onDisable() {
    	log.info("Money mysql has been disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender.getServer().getPlayer(sender.getName());
        if (player == null)
            return false;
        if (command.getName().equals("economy"))
            getMoneyDatabaseInterface().setBalance(player.getUniqueId(), Money.econ.getBalance(player));
        else if (command.getName().equals("balance") || command.getName().equals("money"))
            Money.econ.depositPlayer(player, getMoneyDatabaseInterface().getBalance(player.getUniqueId()));
        return true;
    }
	
	//Methods for setting up Vault
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
  //Getter for Database Interfaces
    public AccountDatabaseInterface<Double> getMoneyDatabaseInterface() {
    	return moneyDatabaseInterface;
    }
    
    public ConfigurationHandler getConfigurationHandler() {
		return configurationHandler;
	}
    
    public DatabaseManagerInterface getDatabaseManagerInterface() {
		return databaseManager;
	}

}
