package dao;

import model.User;

import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
//    public void insert(User user) throws SQLException;

//    public User selectById(int id);

    public List<User> selectByCountry(String country);
    public List<User> selectAll();
    public List<User> sortByName();
    String getName(int id);

    public boolean delete(int id) throws SQLException;

    public boolean update(User user) throws SQLException;
    User getUserById(int id);

    void insertUserStore(User user) throws SQLException;
}
