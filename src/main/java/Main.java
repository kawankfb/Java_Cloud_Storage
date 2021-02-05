import io.javalin.Javalin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final String baseURL="https://www.digikala.com/mag/";

    public static UserController getUserController() {
        return userController;
    }

    private static UserController userController;

    public static Connection getConnection() {
        return connection;
    }

    private static Connection connection=null;

    public static void main(String[] args) {
        userController = new UserController();

        Javalin app=Javalin.create().start(8080);
        FileController fileController=new FileController();

        app.get("/files/:query",fileController::getFile);
        app.post("/files",fileController::storeFile);
        app.post("/users/register",userController::createUser);
        app.post("/users/login",userController::loginUser);

        String url = "jdbc:mysql://localhost:3306/java_cloud_storage";
        String username = "root";
        String password = "kawan1378";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            setConnection(connection);

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    private static void setConnection(Connection con) {
        connection=con;
    }
}
