package com.codegym.dao;



import com.codegym.model.User;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;



public class UserDAO implements IUserDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC";

    private final String jdbcUsername = "root";

    private final String jdbcPassword = "password"; // Đổi thành mật khẩu CSDL của bạn



    private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";

    private static final String SELECT_USER_BY_ID = "SELECT id, name, email, country FROM users WHERE id = ?;";

    private static final String SELECT_ALL_USERS = "SELECT * FROM users;";

    private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ?;";

    private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?;";

private static final String SELECT_USERS_BY_COUNTRY = "SELECT id, name, email, country FROM users WHERE country LIKE ?;";

    private static final String SORT_USERS_BY_NAME = "SELECT id, name, email, country FROM users ORDER BY name ASC;";

private static final String SQL_INSERT = "INSERT INTO Employee (name, salary, created_Date) VALUES (?,?,?)";

    private static final String SQL_UPDATE = "UPDATE Employee SET salary=? WHERE name=?";

    private static final String SQL_TABLE_CREATE = "CREATE TABLE Employee"

            + "("

            + " id INT(11) AUTO_INCREMENT,"

            + " name VARCHAR(100) NOT NULL,"

            + " salary DECIMAL(15, 2) NOT NULL,"

            + " created_Date TIMESTAMP,"

            + " PRIMARY KEY (id)"

            + ")";

    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS Employee";

@Override

    public List<User> selectAllUsers() {

        List<User> users = new ArrayList<>();

        // Sử dụng {CALL get_all_users()} với CallableStatement

        try (Connection connection = getConnection();

             CallableStatement statement = connection.prepareCall("{CALL get_all_users()}");

             ResultSet rs = statement.executeQuery()) {

            

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



    // 2. Sửa thông tin user sử dụng Stored Procedure

    @Override

    public boolean updateUser(User user) throws SQLException {

        boolean rowUpdated;

        // Sử dụng {CALL update_user(?,?,?,?)} với CallableStatement

        try (Connection connection = getConnection();

             CallableStatement statement = connection.prepareCall("{CALL update_user(?,?,?,?)}")) {

            

            statement.setInt(1, user.getId());

            statement.setString(2, user.getName());

            statement.setString(3, user.getEmail());

            statement.setString(4, user.getCountry());

            

            rowUpdated = statement.executeUpdate() > 0;

        }

        return rowUpdated;

    }



    // 3. Xoá user sử dụng Stored Procedure

    @Override

    public boolean deleteUser(int id) throws SQLException {

        boolean rowDeleted;

        // Sử dụng {CALL delete_user(?)} với CallableStatement

        try (Connection connection = getConnection();

             CallableStatement statement = connection.prepareCall("{CALL delete_user(?)}")) {

            

            statement.setInt(1, id);

            rowDeleted = statement.executeUpdate() > 0;

        }

        return rowDeleted;

    }

    @Override

    public void insertUpdateUseTransaction() {

        try (Connection conn = getConnection();

             Statement statement = conn.createStatement();

             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);

             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {



            statement.execute(SQL_TABLE_DROP);

            statement.execute(SQL_TABLE_CREATE);



            // 1. Tắt chế độ auto-commit để bắt đầu Transaction

            conn.setAutoCommit(false); 



            // 2. Chạy 2 lệnh Insert

            psInsert.setString(1, "Quynh");

            psInsert.setBigDecimal(2, new BigDecimal(10));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            psInsert.setString(1, "Ngan");

            psInsert.setBigDecimal(2, new BigDecimal(20));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            // 3. Lệnh Update (Cố tình gán sai tham số để tạo lỗi ở lần chạy 1)

            psUpdate.setBigDecimal(2, new BigDecimal(999.99)); // Sai index

            psUpdate.setString(2, "Quynh");

            psUpdate.execute();



            // 4. Xác nhận commit nếu không có lỗi xảy ra

            conn.commit();

            

            conn.setAutoCommit(true);



        } catch (Exception e) {

            System.out.println("Lỗi xảy ra, Transaction sẽ tự động huỷ bỏ (rollback) khi đóng kết nối!");

            System.out.println(e.getMessage());

            e.printStackTrace();

        }

    }

    @Override

    public void insertUpdateWithoutTransaction() {

        try (Connection conn = getConnection();

             Statement statement = conn.createStatement();

             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);

             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) { 

             

            // 1. Xoá bảng cũ và tạo lại bảng mới

            statement.execute(SQL_TABLE_DROP);

            statement.execute(SQL_TABLE_CREATE);

             

            // 2. Chèn 2 nhân viên (Quynh và Ngan)

            psInsert.setString(1, "Quynh");

            psInsert.setBigDecimal(2, new BigDecimal(10));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute(); 

            

            psInsert.setString(1, "Ngan");

            psInsert.setBigDecimal(2, new BigDecimal(20));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            // 3. Cố tình tạo lỗi ở lệnh Update (chưa gán giá trị tham số 1)

            psUpdate.setBigDecimal(2, new BigDecimal(999.99));

            psUpdate.setString(2, "Quynh");

            

            // Lệnh này sẽ ném ra Exception

            psUpdate.execute();

            

        } catch (Exception e) {

            System.out.println("Đã bắt được lỗi trong quá trình thực thi SQL:");

            e.printStackTrace();

        }

    }

    public List<User> selectUsersByCountry(String countryKeyword) {

        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS_BY_COUNTRY)) {

            preparedStatement.setString(1, "%" + countryKeyword + "%");

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

    public List<User> sortUsersByName() {

        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SORT_USERS_BY_NAME)) {

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

    public UserDAO() {}



    protected Connection getConnection() {

        Connection connection = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

        }

        return connection;

    }



    @Override

    public void insertUser(User user) throws Exception {

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {

            preparedStatement.setString(1, user.getName());

            preparedStatement.setString(2, user.getEmail());

            preparedStatement.setString(3, user.getCountry());

            preparedStatement.executeUpdate();

        }

    }



    @Override

    public User selectUser(int id) {

        User user = null;

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();



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



    @Override

    public List<User> selectAllUsers() {

        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {

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

    public boolean deleteUser(int id) throws Exception {

        boolean rowDeleted;

        try (Connection connection = getConnection();

             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL)) {

            statement.setInt(1, id);

            rowDeleted = statement.executeUpdate() > 0;

        }

        return rowDeleted;

    }



    @Override

    public boolean updateUser(User user) throws Exception {

        boolean rowUpdated;

        try (Connection connection = getConnection();

             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL)) {

            statement.setString(1, user.getName());

            statement.setString(2, user.getEmail());

            statement.setString(3, user.getCountry());

            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;

        }

        return rowUpdated;

    }

}

@Override

    public User getUserById(int id) {

        User user = null;

        String query = "{CALL get_user_by_id(?)}";



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



    @Override

    public void insertUserStore(User user) throws SQLException {

        String query = "{CALL insert_user(?, ?, ?)}";



        try (Connection connection = getConnection();

             CallableStatement callableStatement = connection.prepareCall(query)) {

            

            callableStatement.setString(1, user.getName());

            callableStatement.setString(2, user.getEmail());

            callableStatement.setString(3, user.getCountry());

            callableStatement.executeUpdate();

        }

    }

@Override

    public void addUserTransaction(User user, int[] permissionIds) throws SQLException {

        Connection connection = null;

        PreparedStatement pstmtUser = null;

        PreparedStatement pstmtAssignment = null;

        ResultSet rs = null;

        

        try {

            connection = getConnection();

            

            // 1. Tắt auto-commit để bắt đầu Transaction

            connection.setAutoCommit(false);

            

            // 2. Chèn dữ liệu vào bảng users và lấy lại ID tự sinh

            String insertUserSql = "INSERT INTO users (name, email, country) VALUES (?, ?, ?)";

            pstmtUser = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);

            pstmtUser.setString(1, user.getName());

            pstmtUser.setString(2, user.getEmail());

            pstmtUser.setString(3, user.getCountry());

            pstmtUser.executeUpdate();

            

            // 3. Lấy ID của user vừa chèn

            rs = pstmtUser.getGeneratedKeys();

            int userId = 0;

            if (rs.next()) {

                userId = rs.getInt(1);

            }

            

            // 4. Chèn dữ liệu vào bảng user_permission

            if (permissionIds != null && permissionIds.length > 0) {

                String insertPermissionSql = "INSERT INTO user_permission (user_id, permission_id) VALUES (?, ?)";

                pstmtAssignment = connection.prepareStatement(insertPermissionSql);

                

                for (int permissionId : permissionIds) {

                    pstmtAssignment.setInt(1, userId);

                    pstmtAssignment.setInt(2, permissionId);

                    pstmtAssignment.executeUpdate();

                }

            }

            

            // 5. Commit nếu mọi thứ thành công

            connection.commit();

            System.out.println("Transaction đã được commit thành công!");

            

        } catch (SQLException e) {

            // 6. Rollback nếu có lỗi bất kỳ

            try {

                if (connection != null) {

                    connection.rollback();

                    System.out.println("Có lỗi xảy ra! Transaction đã bị rollback.");

                }

            } catch (SQLException ex) {

                ex.printStackTrace();

            }

            e.printStackTrace();

        } finally {

            // 7. Dọn dẹp tài nguyên và bật lại auto-commit

            if (rs != null) rs.close();

            if (pstmtUser != null) pstmtUser.close();

            if (pstmtAssignment != null) pstmtAssignment.close();

            if (connection != null) {

                connection.setAutoCommit(true);

                connection.close();

            }

        }

    }
@Override
public void addUserTransaction(User user, int[] permissionIds) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmtUser = null;
    PreparedStatement pstmtPermission = null;
    ResultSet rs = null;

    try {
        conn = getConnection();
        
        // 1. Tắt chế độ auto-commit để bắt đầu Transaction
        conn.setAutoCommit(false);

        // 2. Chèn thông tin User mới
        String sqlUser = "INSERT INTO users (name, email, country) VALUES (?, ?, ?)";
        pstmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_VALUES);
        pstmtUser.setString(1, user.getName());
        pstmtUser.setString(2, user.getEmail());
        pstmtUser.setString(3, user.getCountry());
        pstmtUser.executeUpdate();

        // Lấy ID của user vừa được chèn tự động tăng
        rs = pstmtUser.getGeneratedKeys();
        int userId = 0;
        if (rs.next()) {
            userId = rs.getInt(1);
        }

        // 3. Chèn các quyền của user vào bảng trung gian
        // 💡 Mẹo test lỗi: Bạn có thể cố tình viết sai tên bảng (ví dụ: user_permision thay vì user_permissions) 
        // để ép chương trình quăng lỗi SQLException và quan sát cơ chế Rollback hoạt động.
        String sqlPermission = "INSERT INTO user_permissions(user_id, permission_id) VALUES(?, ?)";
        pstmtPermission = conn.prepareStatement(sqlPermission);

        if (permissionIds != null) {
            for (int permissionId : permissionIds) {
                pstmtPermission.setInt(1, userId);
                pstmtPermission.setInt(2, permissionId);
                pstmtPermission.executeUpdate();
            }
        }

        // 4. Nếu mọi câu lệnh chạy thành công, tiến hành Commit
        conn.commit();
        System.out.println("Transaction thành công! Đã commit dữ liệu.");

    } catch (SQLException e) {
        // 5. Nếu xảy ra lỗi bất kỳ, tiến hành Rollback để hoàn tác dữ liệu
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("Đã xảy ra lỗi! Transaction đã được Rollback (hủy bỏ toàn bộ).");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        // Ném tiếp ngoại lệ để controller/servlet hoặc tầng trên bắt được nếu cần
        throw e;
    } finally {
        // Đóng các tài nguyên
        if (rs != null) rs.close();
        if (pstmtUser != null) pstmtUser.close();
        if (pstmtPermission != null) pstmtPermission.close();
        if (conn != null) {
            conn.setAutoCommit(true); // Bật lại trạng thái mặc định
            conn.close();
        }
    }
}
