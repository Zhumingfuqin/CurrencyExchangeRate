# CurrencyExchangeRate
CS220 Project

## 💻 Project Description

This application is a Java-based currency exchange rate viewer that fetches real-time data from the CurrencyFreaks API, processes and stores it in a MySQL database (locally or on AWS RDS), and displays it via a user-friendly Swing GUI. The project was built to demonstrate end-to-end integration between HTTP API consumption, database storage, and graphical front-end rendering.

---

## 🧠 Implementation Details

- **Currency Fetching:** The application uses OkHttp to call CurrencyFreaks' API and retrieves ~950 currency exchange rate entries in JSON format.
- **Data Parsing:** Gson is used to parse JSON responses into Java objects encapsulated in the `CurrencyInfo` class.
- **Null Filtering:** Null or malformed exchange rates are automatically filtered out to prevent runtime exceptions or invalid database entries.
- **Long Code Handling:** Certain currency codes exceed standard length (e.g., `SOLVBTC.BBN`, `CGETH.HASHKEY`). The `currency_code` column in the database was manually adjusted to `VARCHAR(1000)` to ensure successful insertion.
- **Batch Insert:** Data insertion is done using `PreparedStatement` with batch commit, ensuring both efficiency and transaction safety.
- **Dual Database Support:** The program supports both local MySQL and AWS RDS MySQL instances. Switching between them is done through `DatabaseConnector.java` by modifying connection parameters.

---

## ☁️ AWS Integration

- The project has been tested and deployed using **AWS RDS (MySQL)** for remote database access.
- An **EC2 instance** was configured to securely access and test the RDS instance. This includes setting up:
  - SSH access using PEM keys
  - Security Groups allowing inbound MySQL access (port 3306) from fixed IPs or trusted EC2 sources
  - MySQL clients such as Dockerized CLI or EC2 native CLI for remote inspection
- Database operations such as `SELECT COUNT(*)` and data integrity tests were executed from EC2 to confirm successful insertions (~976 records expected).

---

## 🧪 Debug & Enhancements (Resolved)

- ✅ **Duplicate Insertion Fixed:** Log showed “Inserted 976 data” multiple times. Resolved by checking method call paths (`main()` vs GUI trigger).
- ✅ **NullPointerException on GUI Search:** Caused by `getCountryName()` returning null. Fixed by adding null checks.
- ✅ **Field Length Errors:** MySQL `Data too long for column` fixed by modifying schema in DBeaver.
- ✅ **Local vs RDS Confusion:** Connection metadata logging via `conn.getMetaData().getURL()` added to distinguish insert targets.

---

## 🔧 How to Run (for teammates)

Any teammate can run the full system on their own environment by following these steps:

1. **Install Java 8+ and MySQL** (e.g., via XAMPP or native installation)
2. **Create the database:**

```sql
CREATE DATABASE currency_db;
USE currency_db;
CREATE TABLE exchange_rates (
  currency_code VARCHAR(1000),
  rate DOUBLE,
  timestamp VARCHAR(50)
);

// Local MySQL setup
String url = "jdbc:mysql://localhost:3306/currency_db";
String username = "your_username";
String password = "your_password";


