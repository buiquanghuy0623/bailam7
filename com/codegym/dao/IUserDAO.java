package com.codegym.dao;

import com.codegym.model.User;
import java.util.List;

public interface IUserDAO {
    void insertUser(User user) throws Exception;
    User selectUser(int id);
    List<User> selectAllUsers();
    boolean deleteUser(int id) throws Exception;
    boolean updateUser(User user) throws Exception;
  List<User> selectUsersByCountry(String country);
    List<User> sortUsersByName();
    User getUserById(int id);
    void insertUserStore(User user) throws SQLException;
    void addUserTransaction(User user, int[] permissionIds) throws SQLException;
    void insertUpdateUseTransaction() throws SQLException;
    List<User> selectAllUsers() throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
}
