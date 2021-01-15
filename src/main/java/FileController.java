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
        String query=ctx.pathParam("query");
        Path path= Path.of(query);
        InputStream file =fileModel.getFile(path);
        ctx.header("Content-Length", String.valueOf(Files.size(path)));
        ctx.contentType("image/jpg");
        ctx.result(file);
    }
    public void storeFile(Context ctx) throws IOException {
        UploadedFile file =ctx.uploadedFile("file");
        FileOutputStream fileOutputStream=new FileOutputStream(file.getFilename());
        DataOutputStream dataOutputStream=new DataOutputStream(fileOutputStream);
        dataOutputStream.write(file.getContent().readAllBytes());
        ctx.status(201);
        ctx.result("{\"message\" : \"creadted\"}");

    }
}
