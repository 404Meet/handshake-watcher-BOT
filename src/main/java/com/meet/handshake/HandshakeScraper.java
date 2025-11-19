package com.meet.handshake;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandshakeScraper {

    // Use the same profile as HandshakeLoginOnce
    private static final String PROFILE_DIR = HandshakeLoginOnce.getProfileDir();

    // Your filtered Handshake jobs URL
    private static final String JOBS_URL =
            "https://gmu.joinhandshake.com/job-search?per_page=50&jobType=6&sort=posted_date_desc&page=1";

    public static class JobInfo {
        public String title;
        public String company;
        public String pay;
        public String link;

        public JobInfo(String title, String company, String pay, String link) {
            this.title = title;
            this.company = company;
            this.pay = pay;
            this.link = link;
        }
    }

    public static class JobPageResult {
        public int totalCount;
        public List<JobInfo> jobs;

        public JobPageResult(int totalCount, List<JobInfo> jobs) {
            this.totalCount = totalCount;
            this.jobs = jobs;
        }
    }

    public static JobPageResult fetchJobs() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=" + PROFILE_DIR);
        // options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(JOBS_URL);

            // --- 1) JOB COUNT ---

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            WebElement countElement;
            try {
                // From screenshot: <div class="sc-lhealw tvmyL">30 jobs found</div>
                countElement = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("div.sc-lhealw.tvmyL")
                        )
                );
            } catch (TimeoutException e) {
                // Fallback: any div that contains "jobs found"
                countElement = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//div[contains(.,'jobs found')]")
                        )
                );
            }

            String countText = countElement.getText();   // e.g. "30 jobs found"
            int count = parseCount(countText);

            // --- 2) JOB CARDS ---

            List<WebElement> cards = driver.findElements(
                    By.cssSelector("div[data-hook*='job-result-card']")
            );

            System.out.println("DEBUG: found job cards = " + cards.size());

            List<JobInfo> jobs = new ArrayList<>();

            int debugIndex = 1;
            for (WebElement card : cards) {
                try {
                    // DEBUG: print first few cards’ raw text
                    if (debugIndex <= 3) {
                        System.out.println("DEBUG: card " + debugIndex + " text:");
                        System.out.println(card.getText());
                    }
                    debugIndex++;

                    WebElement linkEl = card.findElement(
                            By.cssSelector("a[role='button'][href*='/job-search/']")
                    );

                    String href = linkEl.getAttribute("href");
                    if (href != null && href.startsWith("/")) {
                        href = "https://gmu.joinhandshake.com" + href;
                    }

                    String ariaLabel = linkEl.getAttribute("aria-label");
                    String title;
                    if (ariaLabel != null && !ariaLabel.isEmpty()) {
                        title = ariaLabel.replaceFirst("(?i)^view\\s+", "").trim();
                    } else {
                        title = linkEl.getText().trim();
                    }

                    String cardText = card.getText();
                    String company = "";
                    String pay = "";

                    if (cardText != null && !cardText.isEmpty()) {
                        String[] lines = cardText.split("\\R"); // split on newlines

                        // 1) first line = company (for your cards)
                        if (lines.length > 0) {
                            company = lines[0].trim();
                        }

                        // 2) look for a line that starts with '$' (pay info)
                        for (String line : lines) {
                            String t = line.trim();
                            if (t.startsWith("$")) {
                                pay = t;
                                break;
                            }
                        }

                        // 3) fallback: if we didn't find $, try line 3 (often pay line)
                        if (pay.isEmpty() && lines.length >= 3) {
                            pay = lines[2].trim();
                        }
                    }

                    jobs.add(new JobInfo(title, company, pay, href));

                } catch (NoSuchElementException ignored) {
                    // skip weird cards
                }
            }

            return new JobPageResult(count, jobs);

        } finally {
            driver.quit();
        }
    }

    private static int parseCount(String text) {
        System.out.println("DEBUG: raw count text = '" + text + "'");

        // 1) Try to find "[number] jobs found"
        Matcher m = Pattern.compile("(\\d+)\\s+jobs\\s+found").matcher(text);
        if (m.find()) {
            String jobsNumber = m.group(1);
            return Integer.parseInt(jobsNumber);
        }

        // 2) Fallback: first number in text
        m = Pattern.compile("\\d+").matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }

        return 0;
    }
}