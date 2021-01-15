import io.javalin.http.UploadedFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileModel {
    private static final int BUFFER_SIZE = 4096; // 4KB

    public void storeFile(File file, Path path) throws IOException {
        FileOutputStream fos=new FileOutputStream(path.toString());
        DataOutputStream dos=new DataOutputStream(fos);
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        while (fileInputStream.read(buffer) != -1) {
            dos.write(buffer);
            dos.flush();
        }
        fileInputStream.close();
        dos.close();
        dos.flush();
    }
    public InputStream getFile(Path path) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(path.toString());
        DataInputStream dataInputStream=new DataInputStream(fileInputStream);
        return dataInputStream;
    }
}
