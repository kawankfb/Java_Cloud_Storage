import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class FileController {
    FileModel fileModel;

    public FileController() {
        this.fileModel =new FileModel();
    }
    public void getFile(Context ctx) throws IOException {
        boolean authorized=false;
        String token= ctx.header("Authorization");
        String range= ctx.header("Range");
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
                if (range!=null){
                    int overallSize=0;
                    byte[] fileContents = file.readAllBytes();
                    String[] ranges= range.split(",");
                    ArrayList<byte[]> bytesArray=new ArrayList<>();
                    for (String s : ranges) {
                        String[] temp=s.split("-");
                        int start=Integer.parseInt(temp[0]);
                        int finish=Integer.parseInt(temp[1]);
                        byte[] tempBytes=new byte[finish-start];
                        overallSize=overallSize+(finish-start);
                        for (int i = start; i < fileContents.length && i< finish; i++) {
                            tempBytes[i-start]=fileContents[i];
                        }
                        bytesArray.add(tempBytes);
                    }
                    byte[] finalResult=new byte[overallSize];
                    int index=0;
                    for (byte[] bytes : bytesArray) {
                        for (byte aByte : bytes) {
                            finalResult[index++]=aByte;
                        }
                    }
                    InputStream inputStream=new ByteArrayInputStream(finalResult);

                    ctx.contentType("application/octet-stream");
                    ctx.result(inputStream);
                }
                else {
                    ctx.header("Content-Length", String.valueOf(Files.size(path)));
                    ctx.contentType("application/octet-stream");
                    ctx.result(file);
                }
            }
            else {
                ctx.status(404);
                ctx.contentType("application/json");
                ctx.result("{\"error\" : \"such file doesn't exist\" }");
            }

        }
        else {
            ctx.status(401);
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
