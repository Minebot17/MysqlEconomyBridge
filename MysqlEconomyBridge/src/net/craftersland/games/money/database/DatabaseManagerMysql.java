package net.craftersland.games.money.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.craftersland.games.money.Money;

public class DatabaseManagerMysql implements DatabaseManagerInterface{
	
	private Connection conn = null;
	  
	  // Hostname
	  private String dbHost;
	 
	  // Port -- Standard: 3306
	  private String dbPort;
	 
	  // Databankname
	  private String database;
	 
	  // Databank username
	  private String dbUser;
	 
	  // Databank password
	  private String dbPassword;

	private Money money;
	
	public DatabaseManagerMysql(Money money) {
		this.money = money;
		
		setupDatabase();
	}
	
	@Override
	public boolean setupDatabase() {
		try {
       	 	//Load Drivers
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            dbHost = money.getConfigurationHandler().getString("database.mssql.host");
            dbPort = money.getConfigurationHandler().getString("database.mssql.port");
            database = money.getConfigurationHandler().getString("database.mssql.databaseName");
            dbUser = money.getConfigurationHandler().getString("database.mssql.user");
            dbPassword = money.getConfigurationHandler().getString("database.mssql.password");
            
            //Connect to database
			conn = DriverManager.getConnection("jdbc:sqlserver://"+dbHost+":"+dbPort+";databaseName="+database+";user="+dbUser+";password="+dbPassword+";");
           
          } catch (ClassNotFoundException e) {
            //System.out.println("Could not locate drivers!");
            Money.log.severe("Could not locate drivers for mysql!");
            return false;
          } catch (SQLException e) {
            //System.out.println("Could not connect");
            Money.log.severe("Could not connect to mysql database!");
            e.printStackTrace();
            return false;
          }
		
		//Create tables if needed
	      /*Statement query;
	      try {
	        query = conn.createStatement();
	        
	        String accounts = "CREATE TABLE IF NOT EXISTS `bc_accounts` (id int(10) AUTO_INCREMENT, player_name varchar(50) NOT NULL UNIQUE, balance DOUBLE(30,2) NOT NULL, PRIMARY KEY(id));";
	        query.executeUpdate(accounts);
	      } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	      }*/
      Money.log.info("Mysql has been set up!");
		return true;
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	@Override
	public boolean closeDatabase() {
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
