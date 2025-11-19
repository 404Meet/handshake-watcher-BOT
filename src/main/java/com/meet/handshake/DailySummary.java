package com.meet.handshake;

public class DailySummary {

    public static void sendDailySummary() {
        HandshakeScraper.JobPageResult result = HandshakeScraper.fetchJobs();

        StringBuilder sb = new StringBuilder();
        sb.append("Daily Handshake On-Campus Jobs\n");
        sb.append("Total jobs: ").append(result.totalCount).append("\n\n");

        int maxJobs = 30; // limit to avoid huge Telegram messages
        int i = 1;

        for (HandshakeScraper.JobInfo job : result.jobs) {
            if (i > maxJobs) {
                sb.append("...and more.\n");
                break;
            }
            sb.append(i++).append(". ").append(job.title)
              .append(" — ").append(job.company)
              .append("\n").append(job.link)
              .append("\n\n");
        }

        TelegramNotifier.sendMessage(sb.toString());
    }
}
