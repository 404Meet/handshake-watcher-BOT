package com.meet.handshake;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        // If we have arguments, behave like before (manual modes)
        if (args != null && args.length > 0) {
            String mode = args[0];
            switch (mode) {
                case "login-once":
                    System.out.println("Running login-once...");
                    HandshakeLoginOnce.run();
                    break;

                case "check":
                    System.out.println("Checking for new jobs once...");
                    HandshakeJobWatcher.checkForNewJobs();
                    break;

                case "daily-summary":
                    System.out.println("Sending daily summary once...");
                    DailySummary.sendDailySummary();
                    break;

                default:
                    System.out.println("Unknown mode: " + mode);
                    System.out.println("Supported modes: login-once, check, daily-summary");
            }
            return;
        }

        // No arguments -> run as a long-running scheduler
        System.out.println("🚀 Handshake Watcher started in scheduler mode.");
        System.out.println("• It will check for new jobs roughly every 2 hours.");
        System.out.println("Other functions (check, login-once, daily-summary) are still available via arguments.");

        ZoneId zone = ZoneId.of("America/New_York");  // EST / EDT
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Track last time we ran the "check" job
        ZonedDateTime lastCheckTime = null;
        final long CHECK_INTERVAL_MINUTES = 120; // 2 hours

        while (true) {
            try {
                ZonedDateTime now = ZonedDateTime.now(zone);
                System.out.println("⏰ Tick at " + now.format(fmt) + " (" + zone + ")");

                boolean shouldRunCheck = false;
                if (lastCheckTime == null) {
                    // First run after startup
                    shouldRunCheck = true;
                } else {
                    long minutesSinceLastCheck = Duration.between(lastCheckTime, now).toMinutes();
                    if (minutesSinceLastCheck >= CHECK_INTERVAL_MINUTES) {
                        shouldRunCheck = true;
                    }
                }

                if (shouldRunCheck) {
                    System.out.println("🔍 Running 2-hour job check...");
                    try {
                        HandshakeJobWatcher.checkForNewJobs();  // will only notify if count increased
                    } catch (Exception e) {
                        System.out.println("⚠️ Error while running job check:");
                        e.printStackTrace();
                    } finally {
                        lastCheckTime = now;
                    }
                } else {
                    System.out.println("⏭ Less than " + CHECK_INTERVAL_MINUTES
                            + " minutes since last check; skipping job check this tick.");
                }

                // Sleep 60 seconds between ticks
                System.out.println("😴 Sleeping for 60 seconds...");
                Thread.sleep(60 * 1000L);

            } catch (InterruptedException ie) {
                System.out.println("⏹ Scheduler interrupted, exiting.");
                return;
            } catch (Exception e) {
                System.out.println("⚠️ Unexpected error in scheduler loop, continuing...");
                e.printStackTrace();
            }
        }
    }
}
