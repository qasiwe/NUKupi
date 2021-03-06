package DB;

import DB.QueryCreators.UserQueryCreator;
import Models.User;
import Models.UserCollection;
import Utils.Hasher;
import Utils.UniqueStringGenerator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabaseClient extends DatabaseClient {

    public UserDatabaseClient() {
        UserQueryCreator.getInstance().setConnection(conn);
    }

    public boolean runQueryEmail(String email) {
        try {
            ResultSet rs = UserQueryCreator.getInstance().getUserByEmailQuery(email).executeQuery();
            if (rs.next() == true) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String runQueryUserIdByEmail(String email) {
        String userId = "";
        try {
            ResultSet rs = UserQueryCreator.getInstance().getUserByEmailQuery(email).executeQuery();
            if (rs.next())
                userId = rs.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    public String runQueryUserInfoById(String userId) {
        String info = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = UserQueryCreator.getInstance().userInfoByIdQuery(userId).executeQuery();
            if (rs.next())
                info = rs.getString("email") + "," + rs.getString("phone_number");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    public boolean runQueryUpdateUserPassword(String userId, String newPassword) {
        try {
            int count = UserQueryCreator.getInstance().updatePasswordQuery(userId, newPassword).executeUpdate();
            if (count > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String runQueryDefaultUserPasswordByEmail(String email) {
        try {
            PreparedStatement stmt = conn.prepareStatement("update user set password=? where email=?");
            String password = UniqueStringGenerator.generatePasswordWithDefaultLength();
            String hashedPassword = Hasher.encodeSHA256(password);
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            if (stmt.executeUpdate() == 0) {
                System.out.println(stmt.toString());
                throw new Exception("Oopsie");
            }
            return password;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean runQueryLogIn(String email, String password) {
        try {
            ResultSet rs = UserQueryCreator.getInstance().loginQuery(email, password).executeQuery();
            if (rs.next() == true) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean runQueryCheckPassword(String userId, String password) {
        try {
            ResultSet rs = UserQueryCreator.getInstance().checkPasswordQuery(userId, password).executeQuery();
            if (rs.next() == true) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean runQueryInsertUser(String user_id, String email, String password, String name, String type, String phone_number) {
        try {
            PreparedStatement stmt = conn.prepareStatement("insert into user values (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, user_id);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, name);
            stmt.setString(5, type);
            stmt.setString(6, phone_number);
            int count = stmt.executeUpdate();
            if (count > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserCollection runQueryUsersAll() {
        UserCollection users = new UserCollection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * from user"
            );
            fillUsers(users, rs);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public UserCollection runQueryUserById(String userId) {
        UserCollection users = new UserCollection();
        try {
            ResultSet rs = UserQueryCreator.getInstance().userByIDQuery(userId).executeQuery();
            fillUsers(users, rs);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public String runQueryIfModerator(String userId) {
        String type = "";
        try {
            ResultSet rs = UserQueryCreator.getInstance().userIfModeratorQuery(userId).executeQuery();
            if (rs.next())
                type = rs.getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    private void fillUsers(UserCollection users, ResultSet rs) throws SQLException {
        while (rs.next()) {
            User u = new User(rs.getString("user_id"), rs.getString("email"),
                    rs.getString("password"), rs.getString("name"),
                    rs.getString("type"), rs.getString("phone_number"));
            users.add(u);
        }
    }

//    private String updatePassword(String email) {
//        try {
//            PreparedStatement stmt = conn.prepareStatement("UPDATE users set password=? where email=(?)");
//            String tempPassword = UniqueStringGenerator.generateIDWithDefaultLength();
//            stmt.setString(1, tempPassword);
//            stmt.setString(2, email);
//            stmt.execute();
//            return tempPassword;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

}
