import io.javalin.Javalin;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Javalin app=Javalin.create().start(8080);
        FileController fileController=new FileController();

        app.get("/files/:query",fileController::getFile);
        app.post("/files",fileController::storeFile);
    }
}
