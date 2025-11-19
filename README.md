1. Project Overview
Handshake Watcher is an automation tool designed to help students monitor the Handshake job portal for new internships and job postings.
Without repeatedly checking manually. The application continuously scans job listings and sends instant Telegram updates whenever new roles are available.
The main objective is to save time, reduce missed opportunities, and ensure fast responses to competitive job postings.

3. How It Works
The project is built using pure Java and structured into independent modules:
Component	Responsibility
HandshakeScraper: Logs into Handshake and scrapes job listings
StateStore: Stores previously seen listings in a JSON file to avoid duplicate alerts
HandshakeJobWatcher:	Scheduler to monitor new jobs periodically
TelegramNotifier:	Sends instant job alerts using Telegram Bot API
DailySummary:	Optionally sends a recap of job changes once per day
Main:	CLI entry point to run different operating modes

4. Technologies & Tools Used
Java (Core Java, Streams, Multithreading)
HTML scraping using internal parsing and DOM extraction
JSON-based persistent storage for tracking job state
Maven for dependency management & packaging
Telegram Bot API for notifications
Authentication via login cookies stored securely and reused for scraping
Scheduling logic for automated periodic job checks
AWS EC2 for 24/7 deployment
VS Code for development workflow

5. Authentication & Storage
To interact with Handshake securely:
The tool uses session login cookies instead of storing usernames/passwords.
Cookies are refreshed and managed automatically.
Previously detected jobs are saved in a local JSON file (like jobState.json) so only new postings trigger notifications — preventing spam.

6. Notifications
Notifications are delivered through Telegram with details like:
Job title
Company name
Apply link
Time discovered

This provides quick access and boosts the chance of applying early.

5. Deployment
The full system is deployed on an AWS EC2 Linux instance, running continuously as a background service. This ensures:
No local machine required
Always on - checks around the clock
Low-latency job alerts
