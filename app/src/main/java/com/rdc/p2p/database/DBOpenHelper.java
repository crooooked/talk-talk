package com.rdc.p2p.database;

import android.media.Image;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBOpenHelper {
    private static String uid=null;
    public static String diver = "com.mysql.jdbc.Driver";
    //加入utf-8是为了后面往表中输入中文，表中不会出现乱码的情况
    public static String url = "jdbc:mysql://cdb-2kv77mqo.cd.tencentcdb.com:10053/chat?characterEncoding=utf-8&serverTimezone=UTC";
    public static String user = "root";//用户名
    public static String password = "713zzy892730004";//密码
    /*
     * 连接数据库
     * */
    public static Connection getConn(){
        Connection conn = null;
        try{
            Class.forName(diver);
            System.out.println("Connecting database...");
            conn = DriverManager.getConnection(url, user, password);//获取连接
            System.out.println("Database connectivity success");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Database connection error");
        }
        return conn;
    }

    public static int login(Connection conn,String username, String password) {
        int flag=0;
        try {
            Statement sql=conn.createStatement();
            ResultSet rs=sql.executeQuery("select * from user_info");
            while(rs.next()){
                if(username.equals(rs.getString(1)) && password.equals(rs.getString(2))){
                    flag=1;
                    uid=rs.getString(1);
                    break;
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL statement error");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return flag;
    }
    public static int regist(Connection conn,String username, String password) {
        int flag=0;

        String sql="insert into user_info values (?,?,?)";
        try {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setObject(1,username );
            ps.setObject(2,password );
            ps.setObject(3,0 );
            ps.execute();
            conn.close();
            flag=1;
            System.out.println("Successful write registration data");
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return flag;
    }
    public static int update(Connection conn,String username,int imageID){
        int flag=0;
        String sql="update user_info set imageID=? where uid=? ";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setInt(1,imageID );
            ps.setObject(2,username);
            ps.executeUpdate();
            conn.close();
            flag=1;
            System.out.println("Update avatar successfully");
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fail to update avatar");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return flag;
    }
    public static int add_friend(Connection conn,String username,String friend){
        int flag=0;
        String sql="insert into friend values (?,?)";
        try {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setObject(1,uid );
            ps.setObject(2,friend );
            ps.execute();
            conn.close();
            flag=1;
            System.out.println("Write contact data successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return flag;
    }
     public static List<String> query_friend(Connection conn){
         List<String> friend = new ArrayList<String>();
         try {
             Statement sql=conn.createStatement();
             ResultSet rs=sql.executeQuery("select * from friend where uid='"+uid+"'");
             while(rs.next()) {
                 friend.add(rs.getString(2));
             }
             System.out.println("Query contact success");
         } catch (SQLException e) {
             e.printStackTrace();
             System.out.println("SQL statement error");
         }catch (Exception e){
             e.printStackTrace();
             System.out.println("Connection error");
         }
         return friend;
     }
    public static int query_image(Connection conn,String friend){
        int imageID=0;
        try {
            Statement sql=conn.createStatement();
            ResultSet rs=sql.executeQuery("select * from user_info where uid='"+friend+"'");
            while(rs.next()) {
                imageID=rs.getInt(3);
            }
            System.out.println("Query contact avatar success");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL statement error");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return imageID;
    }
    public static int modify_pwd(Connection conn,String username,String pwd){
        int flag=0;
        String sql="update user_info set password=? where uid=?";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setObject(1,pwd);
            ps.setObject(2,username);
            ps.executeUpdate();
            conn.close();
            flag=1;
            System.out.println("Update password successfully");
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fail to update avatar");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return flag;
    }
}
