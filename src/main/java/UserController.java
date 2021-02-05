import io.javalin.http.Context;

import java.util.Random;

public class UserController {
UserModel userModel;

    public UserController() {
        userModel=new UserModel();
    }

    public void createUser(Context ctx){
        String name=ctx.formParam("name");
        String email=ctx.formParam("email");
        String password=ctx.formParam("password");
        boolean wrongInput= !isAcceptable(name+password+email);
        if (wrongInput) {
            ctx.status(400);
            ctx.result("{\"error\" : \"wrong input\"}");
        }
        else {
            User user = null;
            try {
                user = userModel.createUser(name,email,password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (user==null){

                ctx.status(400);
                ctx.result("{\"error\" : \"user Already exists\"}");
            }
            else {

                ctx.status(201);
                ctx.result("{\"message\" : \"creadted\"}");
            }
        }
    }

    public void loginUser(Context ctx){
        String email=ctx.formParam("email");
        String password=ctx.formParam("password");
        boolean rightInput= isAcceptable(password+email);
        if (!rightInput) {
            ctx.status(400);
            ctx.result("{\"error\" : \"wrong input\"}");
        }
        else {
            User user = null;
            try {
                user = userModel.getUser(email,password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (user==null){

                ctx.status(400);
                ctx.result("{\"error\" : \"user doesn't exist\"}");
            }
            else {
                String token=getToken(user.getEmail(),user.getPassword());
                user.setToken(token);
                ctx.status(200);
                ctx.contentType("application/json");
                ctx.result("{" +
                        "\"name\" : \""+user.getName()+"\" , " +
                        "\"token\" : \""+user.getToken()+"\"" +
                        "}");
            }
        }
    }

    public boolean isAuthenticated(String email,String password) throws Exception{
        if (!isAcceptable(email) || !isAcceptable(password))
            throw new Exception();
        User user= userModel.getUser(email, password);
        if (user.getPassword().equals(password))
            return true;
        return false;
    }
    public boolean isAuthenticated(String token) throws Exception{
        if (token.startsWith("Bearer "))
            token=token.replace("Bearer ","");
        if (token.length()!=UserModel.getTokenLength())
            throw new Exception();
        if (!isAcceptable(token))
            throw new Exception();
        User user= userModel.getUserFromToken(token);
        if (user.getToken().equals(token))
            return true;
        return false;
    }
    public String generateToken(){
        Random random=new Random();
        int token_length=60;
        char[] characters=new char[token_length];
        for (int i = 0; i <token_length ; i++) {
            char temp = (char) (random.nextInt(65535)%62);
            if (temp<10)
                temp= (char) (temp+'0');
            else if (temp<36)
                temp= (char) ((char) (temp-10)+'a');
            else if (temp<62)
                temp= (char) ((char) (temp-36)+'A');
            characters[i]=temp;
        }
        //save to database
        return new String(characters);
    }

    public String getToken(String email,String password){
        String result="";
        try {
            boolean isAuthenticated=isAuthenticated(email, password);
            User user=userModel.getUser(email,password);
            if (isAuthenticated){
                result=generateToken();
                userModel.storeTokenForUser(user,result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isAcceptable(String string){
        boolean wrongInput=false;
        for (int i = 0; i <string.length() ; i++) {
            if (!Character.isLetterOrDigit(string.charAt(i))) {
                if (string.charAt(i)=='@' ||string.charAt(i)=='_' ||string.charAt(i)=='-' ||string.charAt(i)=='.')
                    continue;
                wrongInput = true;
            break;
            }
        }
        return !wrongInput;
    }
}
