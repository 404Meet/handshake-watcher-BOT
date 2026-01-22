# Handshake Job Watcher Bot 🤖🔔

[![Java](https://img.shields.io/badge/Java-17+-informational)](#)
[![Build](https://img.shields.io/badge/Build-Maven-blue)](#)
[![Automation](https://img.shields.io/badge/Automation-Selenium-brightgreen)](#)
[![Alerts](https://img.shields.io/badge/Alerts-Telegram-blue)](#)

A **Java + Selenium** bot that monitors **Handshake job postings** and sends **Telegram alerts** when new jobs appear. It reuses an authenticated **Chrome profile/session**, tracks state in a local JSON file, and can run continuously (e.g., on an **AWS EC2** instance).

---

## Features
- ✅ Reuses SSO login using a persistent **Chrome user profile**
- ✅ Scrapes job cards (title/company/pay/link) from the Handshake job search page
- ✅ Saves last-seen state to `~/.handshake_state.json`
- ✅ Sends Telegram alerts (top newest jobs)
- ✅ Runs in a **scheduler loop** (default: every ~120 minutes)

---

## Tech Stack
- **Java 17+**
- **Maven**
- **Selenium WebDriver** + **WebDriverManager**
- **Jackson** (state persistence)
- **Telegram Bot API**

---

## Quick Start

### 1) Prerequisites
- Java **17+**
- Maven
- Google Chrome installed

### 2) Configure environment variables (recommended)
Set these before running:

**Required**
- `TELEGRAM_BOT_TOKEN` — your bot token from @BotFather  
- `TELEGRAM_CHAT_ID` — chat ID to send notifications  
- `HANDSHAKE_PROFILE_DIR` — local path used to store the Chrome profile/session

**Optional**
- `HANDSHAKE_JOBS_URL` — job search URL with your filters applied  
- `CHECK_INTERVAL_MINUTES` — polling interval (default: `120`)  
- `HEADLESS` — `true/false` (default: `false`)

#### macOS / Linux
bash
`export TELEGRAM_BOT_TOKEN="xxx"`
`export TELEGRAM_CHAT_ID="123456789"`
`export HANDSHAKE_PROFILE_DIR="$HOME/handshake-profile"`
`export HANDSHAKE_JOBS_URL="https://gmu.joinhandshake.com/job-search?per_page=50&sort=posted_date_desc&page=1"`
`export CHECK_INTERVAL_MINUTES="120"`
`export HEADLESS="false"`

#### Windows
PowerShell
`$env:TELEGRAM_BOT_TOKEN="xxx"`
`$env:TELEGRAM_CHAT_ID="123456789"`
`$env:HANDSHAKE_PROFILE_DIR="$env:USERPROFILE\handshake-profile"`
`$env:HANDSHAKE_JOBS_URL="https://gmu.joinhandshake.com/job-search?per_page=50&sort=posted_date_desc&page=1"`
`$env:CHECK_INTERVAL_MINUTES="120"`
`$env:HEADLESS="false"`

#### Run Modes
A) Login Once (first time)
Opens Chrome so you can complete SSO, then saves the session to your profile directory.
mvn -q exec:java -Dexec.args="login-once"

B) Run One Check (manual)
mvn -q exec:java -Dexec.args="check"

C) Daily Summary (manual)
mvn -q exec:java -Dexec.args="daily-summary"

D) Scheduler (recommended)
Runs continuously and checks every CHECK_INTERVAL_MINUTES.
mvn -q exec:java

E) One-Command Run (Example)
After setting env vars:
mvn -q exec:java

#### Configuration Notes
Handshake Job URL / Filters
Set HANDSHAKE_JOBS_URL to the Handshake job-search URL you want (include your filters + sort order).
Example: sort newest, 50 results per page

#### Headless Mode
If HEADLESS=true, the bot runs without opening a browser window.
Tip: Keep login-once non-headless so you can complete SSO.

#### State File
The bot stores state at: ~/.handshake_state.json
This is used to detect new jobs between checks.

#### Disclaimer
Use responsibly and respect Handshake’s Terms of Service. The default interval is intentionally conservative.
