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

---

### 🌐 Optional: Connect to AWS RDS (Skip Local Setup)

If you don’t want to set up a local MySQL database, you can directly connect to the team-hosted AWS RDS instance using a MySQL client. This is ideal if you just want to run and test the application without configuring MySQL locally.

#### ✅ Step 1: Download MySQL Client

**Windows:**

Download and install MySQL (Community version) from the official website:

🔗 https://dev.mysql.com/downloads/mysql/

During installation:
- You can skip MySQL Server and **only select MySQL Client / Command Line Client**.
- After installation, open **"MySQL Command Line Client"** or **Command Prompt / PowerShell**.

**macOS (for reference):**

brew install mysql-client  
echo 'export PATH="/opt/homebrew/opt/mysql-client/bin:$PATH"' >> ~/.zprofile  
source ~/.zprofile  
echo 'export PATH="/opt/homebrew/opt/mysql-client/bin:$PATH"' >> ~/.zshrc  
source ~/.zshrc  

To check if the correct client is in use:

which mysql  
mysql --version  

#### 📡 Step 2: Connect to the AWS RDS database

In your terminal or command prompt, enter the following command:

mysql -h currency-db.cs5y8w8isga2.us-east-1.rds.amazonaws.com -u currency_user -p

When prompted, enter the password:

yizhimodouli

If successful, you'll enter the MySQL CLI prompt (mysql>), meaning you're connected to the remote database.

#### 📊 Step 3: Verify Connection with SQL

After connecting, you can test the connection using the following SQL commands:

USE currency_db;  
SHOW TABLES;  
SELECT COUNT(*) FROM exchange_rates;  
SELECT * FROM exchange_rates LIMIT 5;  

#### ⚠️ Step 4: Notes & Guidelines

✅ This is a standard MySQL server hosted on AWS RDS  
✅ All SQL syntax and tools are the same as local MySQL  
🧪 The exchange_rates table is preloaded with ~976 records  
❗ Please do not modify or delete data unless you are testing insert/update logic  
❌ You do not need to SSH into EC2 or install Docker  

#### 🛠️ Step 5: macOS Compatibility Fix (Optional)

If you see the following error when using mysql on macOS:

ERROR 2059 (HY000): Authentication plugin 'mysql_native_password' cannot be loaded

This is caused by using MySQL 9.x+, which dropped support for mysql_native_password.

✅ To fix it:

brew uninstall mysql  
brew install mysql-client  
echo 'export PATH="/opt/homebrew/opt/mysql-client/bin:$PATH"' >> ~/.zprofile  
source ~/.zprofile  

Or bypass the issue without uninstalling:

/opt/homebrew/opt/mysql-client/bin/mysql -h currency-db.cs5y8w8isga2.us-east-1.rds.amazonaws.com -u currency_user -p







