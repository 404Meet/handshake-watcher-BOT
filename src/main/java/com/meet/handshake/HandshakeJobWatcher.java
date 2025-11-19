package com.meet.handshake;

public class HandshakeJobWatcher {

    public static void checkForNewJobs() {
        // Scrape the current jobs list + count
        HandshakeScraper.JobPageResult result = HandshakeScraper.fetchJobs();

        // Load previous state (lastCount)
        StateStore.State state = StateStore.load();
        int last = state.lastCount;
        int current = result.totalCount;

        System.out.println("Last count = " + last + ", current count = " + current);

        // Only alert when the job count increased compared to last time
        if (current > last) {
            int diff = current - last;

            int totalJobs = result.jobs.size();
            int jobsPerMessage = 15;

            if (totalJobs == 0) {
                // Fallback: no jobs parsed, just send the count info
                StringBuilder sb = new StringBuilder();
                sb.append("🎓 Handshake on-campus alert\n\n");
                sb.append("Previous count: ").append(last).append("\n");
                sb.append("Current count: ").append(current).append("\n");
                sb.append("New jobs since last check: ").append(diff).append("\n");
                sb.append("\n(⚠ No job titles were parsed from the page.)");
                TelegramNotifier.sendMessage(sb.toString());
            } else {
                // Send jobs in multiple messages, jobsPerMessage each
                //for (int start = 0; start < totalJobs; start += jobsPerMessage) {
                for (int start = 0; start < 15; start += jobsPerMessage) {
                    int end = Math.min(start + jobsPerMessage, totalJobs);

                    StringBuilder sb = new StringBuilder();
                    sb.append("🎓 Handshake on-campus alert\n\n");
                    sb.append("Top 15 newest jobs:\n\n");
                    sb.append("Previous count: ").append(last).append("\n");
                    sb.append("Current count: ").append(current).append("\n");
                    sb.append("New jobs since last check: ").append(diff).append("\n");
                    sb.append("Jobs ").append(start + 1).append("–").append(end)
                      .append(" of ").append(totalJobs).append(":\n\n");

                    for (int i = start; i < end; i++) {
                        int displayIndex = i + 1;
                        HandshakeScraper.JobInfo job = result.jobs.get(i);
                        sb.append(displayIndex).append(". ").append(job.title);
                        if (job.company != null && !job.company.isBlank()) {
                            sb.append(" — ").append(job.company);
                        }
                        if (job.pay != null && !job.pay.isBlank()) {
                            sb.append("\n   ").append(job.pay);
                        }
                        if (job.link != null && !job.link.isBlank()) {
                            sb.append("\n").append(job.link);
                        }
                        sb.append("\n\n");
                    }

                    // One Telegram message per chunk
                    TelegramNotifier.sendMessage(sb.toString());
                }
            }
        } else {
            System.out.println("No increase in job count; no notification sent.");
        }

        // Always save the latest count for the next run
        state.lastCount = current;
        StateStore.save(state);
    }
}
