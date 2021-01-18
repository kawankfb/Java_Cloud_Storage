import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileController {
    FileModel fileModel;

    public FileController() {
        this.fileModel =new FileModel();
    }
    public void getFile(Context ctx) throws IOException {
        boolean authorized=false;
        String token= ctx.header("Authorization");
        try {
            if (Main.getUserController().isAuthenticated(token))
                authorized=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authorized){
            String query=ctx.pathParam("query");
            Path path= Path.of(query);
            if (Files.exists(path)){
                InputStream file =fileModel.getFile(path);
                ctx.header("Content-Length", String.valueOf(Files.size(path)));
                ctx.contentType("application/octet-stream");
                ctx.result(file);
            }
            else {
                ctx.status(404);
                ctx.contentType("application/json");
                ctx.result("{\"error\" : \"such file doesn't exist\" }");
            }

        }
        else {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result("{\"error\" : \"Unauthorized\" }");
        }

    }
    public void storeFile(Context ctx) throws IOException {
        boolean authorized=false;
        String token= ctx.header("Authorization");
        try {
            if (Main.getUserController().isAuthenticated(token))
                authorized=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authorized){
            UploadedFile file =ctx.uploadedFile("file");
            FileOutputStream fileOutputStream=new FileOutputStream(file.getFilename());
            DataOutputStream dataOutputStream=new DataOutputStream(fileOutputStream);
            dataOutputStream.write(file.getContent().readAllBytes());
            ctx.status(201);
            ctx.result("{\"message\" : \"creadted\"}");
        }
        else {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result("{\"error\" : \"Unauthorized\" }");
        }

    }
}
