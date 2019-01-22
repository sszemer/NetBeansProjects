/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.sql.*;

/**
 *
 * @author sebastianszemer
 */
public class JDBConnect {
    
private String host = "jdbc:derby://localhost:1527/JTwitchBotDB";
private String userName = "sa";
private String password = "asdqwe123";
private Connection connect = null;
private Statement statement = null;
private ResultSet resultSet = null;

//list of querry results returned by getQuerry method
    
    public static void main (String[] args) throws SQLException, ClassNotFoundException {
        
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        String insertQuery = "insert into users (username, coins, xp, nocoinsforyou) values ('dockingbot', 13, 21, false)";
        String topQuerry = "Select * from users order by coins asc FETCH FIRST 1 ROWS ONLY";
        JDBConnect db = new JDBConnect();
        db.getQuerry();
        
    }
    
    public JDBConnect() throws SQLException {
        
        connect = DriverManager.getConnection(host, userName, password);
        
    }
    
    private String getTopCoins() throws SQLException{
        String topCoins;
        topCoins = "";
        String querry = "Select * from users order by coins desc FETCH FIRST 10 ROWS ONLY";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                topCoins = topCoins + resultSet.getString("username");
                topCoins = topCoins + " ";
                topCoins = topCoins + resultSet.getString("coins");
                topCoins = topCoins + ", ";
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return topCoins;
    } 
    
    private String getTopXp() throws SQLException{
        String topXp;
        topXp = "";
        String querry = "Select * from users order by xp desc FETCH FIRST 10 ROWS ONLY";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                topXp = topXp + resultSet.getString("username");
                topXp = topXp + " ";
                topXp = topXp + resultSet.getString("xp");
                topXp = topXp + ", ";
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return topXp;
    }
    
    private void addCoins(String user, int coins) throws SQLException{
        if(getNoCoinsForYou(user)==false){
            if(getUser(user.toLowerCase()).equals("null")){
                try {

                    String insertQuery = "insert into users (username, coins, xp, nocoinsforyou) values ('"+user+"', "+coins+", 0, false)";
                    PreparedStatement statement = connect.prepareStatement(insertQuery);
                    statement.executeUpdate();

                    } catch (SQLException e) {
                    throw e;
                    } finally {
                    close();
                }
            }
            else{
                try{
                    String updateQuerry = "update users set coins = (select coins from users where username = '"+user+"') + "+coins+" where username = '"+user+"'";
                    PreparedStatement statement = connect.prepareStatement(updateQuerry);
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    throw e;
                }
                finally {
                    close();
                }
            }  
        }
        else{
            System.out.println("you don't deserve to get coins");
        }
    }
    
    private void addXP(String user, int xp) throws SQLException{
        if(getNoCoinsForYou(user)==false){
            if(getUser(user.toLowerCase()).equals("null")){
                try {

                    String insertQuery = "insert into users (username, coins, xp, nocoinsforyou) values ('"+user+"', 0, "+xp+", false)";
                    PreparedStatement statement = connect.prepareStatement(insertQuery);
                    statement.executeUpdate();

                    } catch (SQLException e) {
                    throw e;
                    } finally {
                    close();
                }
            }
            else{
                try{
                    String updateQuerry = "update users set xp = (select xp from users where username = '"+user+"') + "+xp+" where username = '"+user+"'";
                    PreparedStatement statement = connect.prepareStatement(updateQuerry);
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    throw e;
                }
                finally {
                    close();
                }
            }
        }
    }
        
    private int getCoins(String user) throws SQLException{
        int coins;
        coins = 0;
        String querry = "Select coins from users where username='"+user+"'";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                coins = Integer.parseInt(resultSet.getString("COINS"));
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return coins;
    } 
    
    private String getUser(String user) throws SQLException{
        String userNameFromDB;
        userNameFromDB = null;
        String querry = "Select username from users where username='"+user+"'";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userNameFromDB = resultSet.getString("username");
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return userNameFromDB;
    } 
    
    private int getXP(String user) throws SQLException{
        int xp;
        xp = 0;
        String querry = "Select xp from users where username='"+user+"'";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                xp = Integer.parseInt(resultSet.getString("xp"));
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return xp;
    }
    
    private boolean getNoCoinsForYou(String user) throws SQLException{
        boolean noCoinsForYou;
        noCoinsForYou = false;
        String querry = "Select nocoinsforyou from users where username='"+user+"'";
        System.out.println(querry);
        try {

            PreparedStatement statement = connect.prepareStatement(querry);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("nocoinsforyou"));
                if(resultSet.getString("nocoinsforyou").equals("true")){
                    noCoinsForYou = true;
                }
                else{
                    noCoinsForYou = false;
                }
            }
            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
        return noCoinsForYou;
    }
    
    private void getQuerry() throws SQLException {
      
        System.out.println(getNoCoinsForYou("docngbot"));
            
    }
    
    private void updateDB(String querry) throws SQLException {
        try {

            PreparedStatement statement = connect.prepareStatement(querry);
            statement.executeUpdate();

            } catch (SQLException e) {
            throw e;
            } finally {
            close();
        }
    }
    
    private String[][] getTable(String querry) throws SQLException {
        String[][] tabela;
        tabela = new String[0][0];
        return tabela;
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connect != null) {
                //connect.close();
            }
        } catch (Exception e) {

        }
    }
        
}
    

