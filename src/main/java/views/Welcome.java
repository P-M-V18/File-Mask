package views;

import dao.UserDAO;
import model.User;
import service.GenerateOTP;
import service.SendOTPService;
import service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

public class Welcome {
    public void welcomeScreen() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to the app");
        System.out.println("Press 1 to Login");
        System.out.println("Press 2 to Signup");
        System.out.println("Press 0 to Exit");
        int choice = 0;
        try {
            choice = Integer.parseInt(br.readLine());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        switch(choice) {
            case 1 -> login();
            case 2 -> signUp();
            case 0 -> System.exit(0);
        }
    }
    private void login() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter registered email");
        String email = sc.nextLine();
        try {
            if(UserDAO.isExists(email)) {
                String genOTP = GenerateOTP.getOTP();
                SendOTPService.sendOTP(email,genOTP);
                System.out.println("Enter OTP");
                String otp = sc.nextLine();
                if(otp.equals(genOTP)) {
                    new UserView(email).home();
                }
                else {
                    System.out.println("Wrong OTP!");
                }
            }
            else {
                System.out.println("User Not Found!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void signUp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Name");
        String name = sc.nextLine();
        System.out.println("Enter email");
        String email = sc.nextLine();


        try{
            if(UserDAO.isExists(email)) {
                System.out.println("You are already registered!");
            }
            else {
                String genOTP = GenerateOTP.getOTP();
                SendOTPService.sendOTP(email,genOTP);
                System.out.println("Please enter OTP!");
                String otp = sc.nextLine();
                if(otp.equals(genOTP)) {
                    User user = new User(name,email);
                    int response = UserService.saveUser(user);
                    switch (response) {
                        case 0 -> System.out.println("User registered!");
                        case 1 -> System.out.println("registered successfully");
                    }
                }
                else {
                    System.out.println("Wrong OTP!");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


}
