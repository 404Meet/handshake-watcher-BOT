package com.meet.handshake;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

public class HandshakeLoginOnce {

    // Change this to your Mac username path
    private static final String PROFILE_DIR = "/Users/meet/handshake-profile";

    // Handshake login URL
    private static final String LOGIN_URL = "https://app.joinhandshake.com/login";

    public static void run() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=" + PROFILE_DIR);
        // IMPORTANT: DO NOT run headless here; you need to see the browser.
        // options.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(LOGIN_URL);

            System.out.println("Browser opened with Handshake login page.");
            System.out.println("1. Log in using GMU SSO as usual.");
            System.out.println("2. Navigate to your jobs search page with your filters.");
            System.out.println("3. When done and you see the jobs list, return here and press ENTER.");

            System.in.read();

            System.out.println("Login process completed. Cookies are stored in: " + PROFILE_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Only close the browser, cookies/profile will persist in PROFILE_DIR
            driver.quit();
        }
    }

    public static String getProfileDir() {
        return PROFILE_DIR;
    }
}
