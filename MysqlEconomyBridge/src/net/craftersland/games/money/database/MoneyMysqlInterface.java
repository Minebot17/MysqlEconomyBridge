package net.craftersland.games.money.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.craftersland.games.money.Money;

public class MoneyMysqlInterface implements AccountDatabaseInterface <Double>{
	
	@SuppressWarnings("unused")
	private Money money;
	private Connection conn;
	
	public MoneyMysqlInterface(Money money) {
		this.money = money;
		this.conn = ((DatabaseManagerMysql)money.getDatabaseManagerInterface()).getConnection();
	}
	
	@Override
	public boolean hasAccount(UUID player) {
		      try {
		 
		        String sql = "select UUID from Users where UUID=?";
		        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.toString());
		        
		        
		        ResultSet result = preparedUpdateStatement.executeQuery();
		 
		        while (result.next()) {
		        	return true;
		        }
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		      return false;
	}
	
	@Override
	public Double getBalance(UUID player) {
		if (!hasAccount(player)) {
			return null;
		}
		
	      try {
	 
	        String sql = "select balance from Users where UUID=?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return Double.parseDouble(result.getString("balance"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}
	
	@Override
	public boolean setBalance(UUID player, Double amount) {
		if (!hasAccount(player)) {
			return false;
		}
		
        try {
			String updateSql = "update Users set balance=? where UUID=?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
			preparedUpdateStatement.setInt(1, (int)(Math.floor(amount)));
			preparedUpdateStatement.setString(2, player.toString());
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	@Override
	public boolean addToAccount(UUID player, Double amount) {
		if (!hasAccount(player)) {
			return false;
		}
		
		if (amount < 0) {
			return removeFromAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance <= Double.MAX_VALUE-amount) {
			setBalance(player, currentBalance+amount);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeFromAccount(UUID player, Double amount) {
		if (!hasAccount(player)) {
			return false;
		}
		
		if (amount < 0) {
			return addToAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance >= -Double.MAX_VALUE+amount) {
			setBalance(player, currentBalance-amount);
			return true;
		}
		return false;
	}
	
	@Override
	public UUID[] getAccounts() {
		
	      Statement query;
	      try {
	        query = conn.createStatement();
	 
	        String sql = "select UUID from Users";
	        ResultSet result = query.executeQuery(sql);
	 
	        List <UUID> loadingList= new ArrayList <UUID>();
	        while (result.next()) {
	        	loadingList.add(UUID.fromString(result.getString("UUID")));
	        }
	        return loadingList.toArray(new UUID [0]);
	        
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}

}
