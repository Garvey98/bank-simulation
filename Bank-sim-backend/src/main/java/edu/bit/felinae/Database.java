package edu.bit.felinae;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private Lock lock;
    private static Database instance = new Database();
    private Connection conn;
    public static Database getInstance() {
        return instance;
    }
    private void initDatabase() {
        try {
            DatabaseMetaData md = conn.getMetaData();
            ArrayList<String> table_name = new ArrayList<>();
            ResultSet set = md.getTables(null, null, "%", null);
            while(set.next()){
                table_name.add(set.getString(3));
            }
            if(!table_name.contains("user")){
                lock.lock();
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE user (" +
                        "id integer PRIMARY KEY autoincrement ," +
                        "username varchar(225) NOT NULL," +
                        "password varchar(225) NOT NULL," +
                        "balance double DEFAULT 0" +
                        ");";
                stmt.execute(sql);
                lock.unlock();
                System.out.println("insert ok");
            }

        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }
    private Database() {
        lock = new ReentrantLock();
        String url = "jdbc:sqlite:db.sqlite";
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            initDatabase();
        }catch (SQLException s) {
            System.err.println(s.toString());
        }catch (ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
    }

    public boolean checkCreditial(String username, String password) {
        String sql = "SELECT COUNT(*) FROM user WHERE username=? AND password=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;

        }catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean register(String username, String password) {
        String sql_query = "SELECT COUNT(*) FROM user WHERE username=?";
        String sql = "INSERT INTO user (username, password) VALUES (?,?)";
        try{
            PreparedStatement qstmt = conn.prepareStatement(sql_query);
            qstmt.setString(1, username);
            ResultSet rs = qstmt.executeQuery();
            rs.next();
            if(rs.getInt(1) >= 1){
                return false;
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        }catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }
    public boolean delete(String username) {
        String sql = "DELETE FROM user WHERE username=?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            return true;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public double checkBalance(String username) {
        String sql = "SELECT balance FROM user WHERE username=?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt("balance");
        }catch (Exception e){
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public boolean withdrawal(String username, double amount) {
        lock.lock();
        String query_sql = "SELECT balance FROM user WHERE username=?";
        String update_sql = "UPDATE user SET balance=balance-? WHERE username=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query_sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            double balance = rs.getDouble("balance");
            pstmt.close();
            if(balance<amount) return false;
            pstmt = conn.prepareStatement(update_sql);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() == 1;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }finally {
            lock.unlock();
        }
    }

    public boolean deposit(String username, double amount) {
        lock.lock();
        String sql = "UPDATE user SET balance=balance+? WHERE username=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() == 1;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }finally {
            lock.unlock();
        }
    }

    public void cleanDB() {
        String sql = "DELETE FROM user";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
