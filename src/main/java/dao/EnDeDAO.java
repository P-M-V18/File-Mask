package dao;

import db.MyConnection;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import model.Data;
import model.EnDe;
public class EnDeDAO {
    //Algorithm used for encryption.
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;


    public static SecretKey generateKey() throws Exception {
        //Creates a new KeyGenerator instance for the specified algorithm ("AES").
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        //Initializes the key generator with the desired key size.
        keyGenerator.init(KEY_SIZE);
        //Generates a new secret key using the initialized KeyGenerator instance.
        return keyGenerator.generateKey();
    }

    public static void encryptFile(String inputFile, Key secretK, EnDe file) throws Exception {
        Connection connection = MyConnection.getConnection();

        String encodedKey = Base64.getEncoder().encodeToString(secretK.getEncoded());
        PreparedStatement ps2 = connection.prepareStatement("insert into fileData(name,path,email,secret_key) values(?,?,?,?)");
        ps2.setString(1,file.getFileName());
        System.out.println(file.getFileName());
        ps2.setString(2, file.getPath());
        ps2.setString(3, file.getEmail());
        ps2.setString(4, encodedKey);
        int ans = ps2.executeUpdate();

        //Creates a Cipher instance for the specified algorithm ("AES").
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //Initializes the cipher for encryption mode using the specified secret key.
        cipher.init(Cipher.ENCRYPT_MODE, secretK);

        // Create a temporary file for encrypted content
        String tempFile = inputFile + ".enc";
        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new FileOutputStream(tempFile);
             //The CipherOutputStream is used to wrap the FileOutputStream.
             // This allows the encryption to be applied to the data as it is written to the output stream.
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

            byte[] buffer = new byte[1024];
            //A buffer is used to read data from the input file
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                //The encrypted data is written to the CipherOutputStream, which, in turn, writes to the FileOutputStream.
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }

        // Replace the input file with the encrypted content
        Files.move(Paths.get(tempFile), Paths.get(inputFile), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void decryptFile(int id) throws Exception {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select path,secret_key from filedata where id=?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String path = rs.getString("path");
        String key = rs.getString("secret_key");
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        try {
            // Decode the Base64 string
            byte[] decodedKey = Base64.getDecoder().decode(key);

            // Create a SecretKey object from the decoded key bytes
            SecretKey secretKeyl = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeyl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a temporary file for decrypted content
        String tempFile = path + ".dec";
        try (InputStream inputStream = new FileInputStream(path);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Replace the input file with the decrypted content
        Files.move(Paths.get(tempFile), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);

        ps = connection.prepareStatement("delete from filedata where id=?");
        ps.setInt(1,id);
        ps.executeUpdate();
        System.out.println("Successfully Decrypted!");

    }
    public static List<EnDe> getAllFiles(String email) throws SQLException {

        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from filedata where email=?");
        ps.setString(1,email);
        ResultSet rs = ps.executeQuery();
        List<EnDe> files = new ArrayList<>();
        while(rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new EnDe(id,name,path));


        }
        return files;
    }
}
