#  Crypto Trading Platform Manager



![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)

> **University Database Project** > A comprehensive desktop application for managing a cryptocurrency exchange platform database. Built with **JavaFX** and **PostgreSQL**.

## Key Features

### Modern User Interface
* **Floating Card Design:** A stunning welcome screen with directional lighting/shadow effects and gradient backgrounds.
* **Flat & Clean:** Minimalist navigation buttons with hover effects.
* **Responsive Layout:** The window maintains its structure and styling when navigating between scenes.

### Powerful Dashboard
* **Predefined Reports:** One-click access to complex database queries.
* **Real-time Sorting:** Interactive tables allowing mathematical sorting by price, quantity, or ID.
* **SQL Console View:** Displays the exact SQL query being executed behind the scenes for educational transparency.

### Advanced SQL Editor
* **Matrix Style Console:** A dedicated dark-mode environment (Black/Neon Green) for writing custom SQL commands.
* **Direct Execution:** Execute `SELECT`, `INSERT`, `UPDATE`, or `DELETE` commands directly against the database.
* **Error Handling:** User-friendly error messages for invalid SQL syntax.

---

## Database Logic & Complex Queries

The application demonstrates advanced SQL concepts including:
* **Complex Joins:** Connecting Users, Wallets, and Balances.
* **Subqueries & Nested Selects:** Finding users who registered *after* the last transaction.
* **Aggregations & Grouping:** Calculating total holdings per coin.
* **HAVING Clauses:** Identifying "Whale" users (deposits > average).
* **Business Logic:**
    * ⛔ *Inactive VIPs* detection.
    * 💎 *HODLers* (Users with deposits but zero withdrawals).
    * 📈 *Premium Coins* analysis (Price > Market Average).

---

## ⚙️ Tech Stack

* **Language:** Java (JDK 17+)
* **GUI Framework:** JavaFX
* **Database:** PostgreSQL 14+
* **IDE:** IntelliJ IDEA
* **Styling:** Custom CSS (Flat Design + Glassmorphism effects)

---

## Installation & Setup Guide

### Prerequisites
1.  **PostgreSQL** installed and running.
2.  **Java JDK** installed.
3.  **IntelliJ IDEA** (recommended).

### Step 1: Database Setup
1.  Open **pgAdmin 4** (or your preferred SQL tool).
2.  Create a new database named `BD_Platforma_Crypto`.
3.  Open the file `setup_baza_de_date.sql` located in this repository.
4.  Run the entire script to create tables and populate them with realistic dummy data.

### Step 2: Configure Connection
1.  Open the project in IntelliJ.
2.  Navigate to `src/main/java/bd/cryptodatabasefx/DBUtils.java`.
3.  Update the credentials to match your local Postgres setup:
    ```java
    private static final String USER = "postgres";
    private static final String PASS = "your_password_here"; // CHANGE THIS
    ```

### Step 3: Run the App
1.  Locate `Launcher.java` or `Main.java`.
2.  Right-click and select **Run**.

---

## 📂 Project Structure

```text
Crypto-Platform-Manager/
├── src/
│   ├── main/
│   │   ├── java/bd/cryptodatabasefx/
│   │   │   ├── Main.java          # Core Application Logic & UI
│   │   │   ├── DBUtils.java       # Database Connection Handler
│   │   │   └── Launcher.java      # Entry Point
│   │   └── resources/
│   │       ├── style.css          # CSS Styling
│   │       └── fundal.png         # Background Asset
├── setup_baza_de_date.sql         # SQL Script for Data generation
└── README.md                      # Documentation
