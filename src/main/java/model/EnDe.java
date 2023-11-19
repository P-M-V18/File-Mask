package model;

import javax.crypto.SecretKey;

public class EnDe {
    private int id;
    private String fileName;
    private String path;
    private String email;
    private SecretKey encryptKey;

    public EnDe(int id, String fileName, String path, String email, SecretKey encryptKey) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
        this.email = email;
        this.encryptKey = encryptKey;
    }

    public EnDe(int id, String fileName, String path) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SecretKey getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(SecretKey encryptKey) {
        this.encryptKey = encryptKey;
    }
}
