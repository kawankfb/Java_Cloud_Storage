import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserModel {
    public static int getTokenLength() {
        return tokenLength;
    }

    private static int tokenLength=60;

    public User createUser(String name,String email,String password) throws Exception{
        if (name.length()>200)
            throw new Exception();
        if (email.length()>200)
            throw new Exception();
        if (password.length()>200)
            throw new Exception();
        
        //add user to database
        Connection connection=Main.getConnection();
        try{
            Statement statement=connection.createStatement();
            StringBuilder queryBuilder=new StringBuilder("INSERT INTO users (name, email, password) VALUES ('");
            queryBuilder.append(name);
            queryBuilder.append("','");
            queryBuilder.append(email);
            queryBuilder.append("','");
            queryBuilder.append(password);
            queryBuilder.append("');");
            statement.execute(queryBuilder.toString());


        }catch (Exception e){

        }

        User user=new User(email, password);
        user.setName(name);
        return user;


    }
    public User getUser(String email,String password)throws Exception {
        Statement statement=Main.getConnection().createStatement();
        ResultSet myResults = statement.executeQuery("SELECT * FROM users WHERE email=\""+email+"\" AND password=\""+password+"\";");
        String name="";
        int user_id=-1;
        while (myResults.next()) {
            name=myResults.getString(2);
            user_id=myResults.getInt(1);
        }

            User user=new User(email,password);
            user.setName(name);
            user.setUser_id(user_id);
            return user;
    }

    public User getUserFromToken(String token) throws Exception{
        String email="";
        String name="";
        String password="";
        int user_id =-1;
        //get results for token from database and set it to user
        Statement statement=Main.getConnection().createStatement();
        ResultSet myResults = statement.executeQuery("SELECT (user_id) FROM users_tokens WHERE token=\""+token+"\";");
        while (myResults.next()) {
            user_id=myResults.getInt(1);
        }
        if (user_id>=0) {
            statement = Main.getConnection().createStatement();
            myResults = statement.executeQuery("SELECT * FROM users WHERE id=\"" + user_id + "\";");
            while (myResults.next()) {
                name = myResults.getString(2);
                email = myResults.getString(3);
                password = myResults.getString(4);
            }
        }



        User user=new User();
        user.setEmail(email);
        user.setToken(token);
        user.setName(name);
        user.setUser_id(user_id);
        user.setPassword(password);
        return user;
    }

    public boolean storeTokenForUser(User user,String token){
        boolean result=false;
        Connection connection=Main.getConnection();
        try{
            Statement statement=connection.createStatement();
            StringBuilder queryBuilder=new StringBuilder("INSERT INTO users_tokens (user_id, token) VALUES (");
            queryBuilder.append(user.getUser_id());
            queryBuilder.append(",'");
            queryBuilder.append(token);
            queryBuilder.append("');");
            statement.execute(queryBuilder.toString());

            result=true;
        }catch (Exception e){
    e.printStackTrace();
        }


        return result;
    }
}
