package br.com.bbc.dao;

import br.com.bbc.factory.ConnectionFactory;
import br.com.bbc.model.UserDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void create(UserDB user){
        String sql = "INSERT INTO user(userId, saldo) VALUES(?, ?)";

        Connection con = null;
        PreparedStatement pstm = null;

        try{
            con = ConnectionFactory.connectionToDB();

            pstm = con.prepareStatement(sql);
            pstm.setString(1, user.getUserId());
            pstm.setDouble(2, user.getSaldo());

            pstm.execute();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if (pstm != null){
                    pstm.close();
                }

                if (con != null){
                    con.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public List<UserDB> getUser(int id){
        String sql = "SELECT * FROM user WHERE userId = ?";

        List<UserDB> users = new ArrayList<UserDB>();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet result = null;

        try{
            con = ConnectionFactory.connectionToDB();
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, id);

            result = pstm.executeQuery();
            while (result.next()){
                UserDB user = new UserDB();
                user.setId(result.getInt("id"));
                user.setUserId(result.getString("userId"));
                user.setSaldo(result.getDouble("saldo"));

                users.add(user);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {

            try{
                if (con != null){
                    con.close();
                }
                if (pstm != null){
                    pstm.close();
                }
                if (result != null){
                    result.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return users;
        }
    }

}
