package views;

import dao.DataDAO;
import dao.EnDeDAO;
import model.Data;
import model.EnDe;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static dao.EnDeDAO.*;

public class UserView {
    private final String email;

    UserView(String email) {
        this.email = email;
    }
    public void home() {
        do {
            System.out.println("Welcome " + this.email);
            System.out.println("Press 1 to hide a new file");
            System.out.println("Press 2 to unhide the file");
            System.out.println("Press 3 to encrypt a file");
            System.out.println("Press 4 to Decrypt the file");
            System.out.println("Press 0 to exit!");
            Scanner sc = new Scanner(System.in);
            int choice = Integer.parseInt(sc.nextLine());

//-----------------------------------------------------CONTROLLER--------------------------------------------------
            switch(choice) {
                case 0 -> System.exit(0);
                case 1 -> {
                    System.out.println("Enter the File Path! ");
                    String path = sc.nextLine();
                    File f = new File(path);
                    Data file = new Data(0,f.getName(),path,this.email);
                    try {
                        DataDAO.hideFile(file);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 2 -> {
                    try {
                        List<Data> files = DataDAO.getAllFiles(this.email);
                        System.out.println("ID - File Name");
                        for (Data file : files) {
                            System.out.println(file.getId() + " - " + file.getFileName());
                        }
                        System.out.println("Enter the ID of file to unhide!");
                        int id = Integer.parseInt(sc.nextLine());
                        boolean isValidID = false;
                        for(Data file : files) {
                            if(file.getId() == id) {
                                isValidID = true;
                                break;
                            }
                        }
                        if(isValidID) {
                            DataDAO.unhide(id);
                        }
                        else {
                            System.out.println("Wrong ID");
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                case 3 -> {
                    System.out.println("Enter the File Path! ");
                    String path = sc.nextLine();
                    File f = new File(path);

                    try {
                        // Generate a secret key
                        SecretKey secretKey = generateKey();
                        EnDe file = new EnDe(0,f.getName(),path,this.email,secretKey);

                        // Encrypt the file
                        encryptFile(path, secretKey,file);


                        System.out.println("File encryption completed successfully.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case 4 -> {
                    try {
                        List<EnDe> files = EnDeDAO.getAllFiles(this.email);
                        System.out.println("ID - File Name");
                        for (EnDe file : files) {
                            System.out.println(file.getId() + " - " + file.getFileName());
                        }
                        System.out.println("Enter the ID of file to Decrypt!");
                        int id = Integer.parseInt(sc.nextLine());
                        boolean isValidID = false;
                        for(EnDe file : files) {
                            if(file.getId() == id) {
                                isValidID = true;
                                break;
                            }
                        }
                        if(isValidID) {
                            EnDeDAO.decryptFile(id);
                        }
                        else {
                            System.out.println("Wrong ID");
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }while(true);
    }
}
