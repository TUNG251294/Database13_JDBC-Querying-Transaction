package dao;

import model.User;

import java.sql.*;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;


public class UserDAO implements IUserDAO{
        private String jdbcURL = "jdbc:mysql://localhost:3306/user_demo?useSSL=false";
        private String jdbcUsername = "root";
        private String jdbcPassword = "votinh9$";

        private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";
        private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
        private static final String SELECT_ALL_USERS = "select * from users";
        private static final String SELECT_BY_NAME = "select * from users order by name";
        private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
        private static final String UPDATE_USERS_SQL = "update users set name = ?,email= ?, country =? where id = ?;";
        private static final String SELECT_BY_COUNTRY = "select*from users where country = ?;";

        public UserDAO() {
        }

        protected Connection getConnection(){
            Connection connection = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(jdbcURL,jdbcUsername,jdbcPassword);
            } catch (SQLException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return connection;
        }

//    @Override
//    public void insert(User user) throws SQLException {
//        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
//            preparedStatement.setString(1, user.getName());
//            preparedStatement.setString(2, user.getEmail());
//            preparedStatement.setString(3, user.getCountry());
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    Ham thay the cho selectById(), dung procedure thay vi viet ham trong DAO
@Override
public User getUserById(int id) {
    User user = null;
    String query = "{CALL getUserById(?)}";
    try (Connection connection = getConnection();
         CallableStatement callableStatement = connection.prepareCall(query)) {

        callableStatement.setInt(1, id);

        ResultSet rs = callableStatement.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String email = rs.getString("email");
            String country = rs.getString("country");
            user = new User(id, name, email, country);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return user;
}
//    @Override
//    public User selectById(int id) {
//        User user = null;
//        try (Connection connection = getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
//            preparedStatement.setInt(1, id);
//            // Step 3: Execute the query or update query
//            ResultSet rs = preparedStatement.executeQuery();
//
//            while (rs.next()) {
//                String name = rs.getString("name");
//                String email = rs.getString("email");
//                String country = rs.getString("country");
//                user = new User(id, name, email, country);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return user;
//    }

    //    Ham thay the cho insert(), dung procedure thay vi viet ham trong DAO
    @Override
    public void insertUserStore(User user) throws SQLException {
        String query = "{CALL insertUser(?,?,?)}";
        // try-with-resource statement will auto close the connection.
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query);) {
            callableStatement.setString(1, user.getName());
            callableStatement.setString(2, user.getEmail());
            callableStatement.setString(3, user.getCountry());
//            System.out.println(callableStatement);
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        @Override
        public List<User> selectByCountry(String country) {
            List<User> users = new ArrayList<>();
            try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_COUNTRY);) {
                preparedStatement.setString(1,country);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    User user = new User(id, name, email, country);
                    users.add(user);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return users;
        }

        @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();
        // Step 1: Establishing a Connection
        try (Connection connection = getConnection();

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                users.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

        @Override
        public List<User> sortByName() {
            List<User> users = new ArrayList<>();
            try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NAME);){
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String country = rs.getString("country");
                    users.add(new User(id,name,email,country));
                    }
                }catch(SQLException e){
                e.printStackTrace();
            }
            return users;
        }

        @Override
    public boolean delete(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
    @Override
    public boolean update(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

        @Override
        public String getName(int id) {
            String name ="";
            try (Connection connection = getConnection();
                 CallableStatement callableStatement = connection.prepareCall("{call getUserName(?,?)}");){
                callableStatement.setInt(1,id);
                callableStatement.registerOutParameter(2, Types.NVARCHAR);

//                ResultSet rs = callableStatement.executeQuery();
                callableStatement.execute();
                name = callableStatement.getString(2);
            } catch (SQLException e){
                e.printStackTrace();
            }
            return name;
        }



}
