# LFP-AP Setup Guide

## Complete Installation and Configuration Guide

This guide will walk you through setting up the Laptop Friendly Places - Advanced Programming (LFP-AP) Java application from scratch.

---

## 📋 Prerequisites

### Required Software:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use OpenJDK: https://adoptium.net/

2. **JavaFX SDK 19 or higher**
   - Download from: https://gluonhq.com/products/javafx/
   - Extract to a known location (e.g., `C:\javafx-sdk-19`)

3. **MySQL Server 8.0 or higher**
   - Already installed with XAMPP
   - Or download from: https://dev.mysql.com/downloads/mysql/

4. **Maven 3.6+ (Optional but Recommended)**
   - Download from: https://maven.apache.org/download.cgi
   - Or use IDE's built-in Maven

5. **IDE (Choose one)**
   - IntelliJ IDEA (Recommended)
   - Eclipse
   - NetBeans
   - VS Code with Java extensions

---

## 🚀 Step-by-Step Setup

### Step 1: Verify Java Installation

Open Command Prompt and run:
```cmd
java -version
javac -version
```

You should see Java version 17 or higher.

### Step 2: Setup MySQL Database

1. **Start MySQL Server**
   - Open XAMPP Control Panel
   - Click "Start" on MySQL

2. **Create Database**
   - Open MySQL Workbench or phpMyAdmin
   - Run the database schema:

```cmd
cd c:\xampp\htdocs\LFP-AP\docs
mysql -u root -p < database_schema.sql
```

Or manually:
- Open phpMyAdmin: http://localhost/phpmyadmin
- Click "SQL" tab
- Copy and paste content from `database_schema.sql`
- Click "Go"

3. **Verify Database Creation**
```sql
USE lfp_ap_db;
SHOW TABLES;
SELECT * FROM users;
```

### Step 3: Configure Database Connection

Edit `src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/lfp_ap_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

**Note:** If you're using XAMPP default, password is usually empty.

### Step 4: Install Dependencies

#### Option A: Using Maven (Recommended)

```cmd
cd c:\xampp\htdocs\LFP-AP
mvn clean install
```

This will download all required dependencies:
- JavaFX libraries
- MySQL Connector/J
- BCrypt
- Gson

#### Option B: Manual Installation

Download and add to `lib/` folder:
1. **MySQL Connector/J**: https://dev.mysql.com/downloads/connector/j/
2. **BCrypt**: https://mvnrepository.com/artifact/org.mindrot/jbcrypt
3. **Gson**: https://mvnrepository.com/artifact/com.google.code.gson/gson

### Step 5: Configure IDE

#### IntelliJ IDEA:

1. **Open Project**
   - File → Open → Select `LFP-AP` folder

2. **Configure JDK**
   - File → Project Structure → Project
   - Set SDK to Java 17+

3. **Add JavaFX Library**
   - File → Project Structure → Libraries
   - Click "+" → Java
   - Navigate to JavaFX SDK lib folder
   - Select all JAR files

4. **Configure VM Options**
   - Run → Edit Configurations
   - Add VM options:
   ```
   --module-path "C:\path\to\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
   ```

#### Eclipse:

1. **Import Project**
   - File → Import → Existing Maven Projects
   - Select `LFP-AP` folder

2. **Configure Build Path**
   - Right-click project → Build Path → Configure Build Path
   - Add External JARs (JavaFX and other libraries)

3. **Add VM Arguments**
   - Run → Run Configurations
   - Arguments tab → VM arguments:
   ```
   --module-path "C:\path\to\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
   ```

### Step 6: Compile the Project

#### Using Maven:
```cmd
mvn compile
```

#### Using javac (Manual):
```cmd
javac -d bin --module-path "C:\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml -cp "lib\*" src\main\java\com\lfp\**\*.java
```

---

## 🎮 Running the Application

### Step 1: Start the Server

The server handles RMI services and socket connections.

#### Using Maven:
```cmd
mvn exec:java -Dexec.mainClass="com.lfp.server.LFPServer"
```

#### Using java command:
```cmd
java --module-path "C:\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml -cp "bin;lib\*" com.lfp.server.LFPServer
```

#### Using IDE:
- Right-click `LFPServer.java`
- Select "Run 'LFPServer.main()'"

**Expected Output:**
```
✓ Database configuration loaded successfully
✓ Database connection established
✓ RMI Registry started on port 1099
✓ UserService registered
✓ PlaceService registered
✓ ReportService registered
✓ Socket Server started on port 8888
✓ LFP Server is running...
```

### Step 2: Start the Client

Open a new terminal/command prompt.

#### Using Maven:
```cmd
mvn exec:java -Dexec.mainClass="com.lfp.client.LFPClient"
```

#### Using java command:
```cmd
java --module-path "C:\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml -cp "bin;lib\*" com.lfp.client.LFPClient
```

#### Using IDE:
- Right-click `LFPClient.java`
- Select "Run 'LFPClient.main()'"

**Expected Output:**
- JavaFX login window should appear

---

## 🧪 Testing the Application

### Test 1: Login

1. **Admin Login**
   - Email: `admin@laptopfriendly.com`
   - Password: `admin123`
   - Should redirect to admin dashboard

2. **Regular User Login**
   - Email: `test@example.com`
   - Password: `test123`
   - Should redirect to user dashboard

### Test 2: Register New User

1. Click "Register" button
2. Fill in:
   - Username: `newuser`
   - Email: `newuser@example.com`
   - Password: `password123`
3. Click "Register"
4. Should create account and login

### Test 3: Browse Places

1. Navigate to "Places" section
2. Should see list of approved places
3. Click on a place to view details

### Test 4: Add to Favorites

1. Click heart icon on a place
2. Navigate to "Favorites"
3. Should see the place in favorites list

### Test 5: Contribute Place (User)

1. Click "Contribute" button
2. Fill in place details
3. Submit
4. Place should be pending approval

### Test 6: Admin Functions

1. Login as admin
2. Navigate to "Manage Places"
3. Approve/reject pending places
4. Navigate to "Manage Users"
5. Block/unblock users
6. Navigate to "Reports"
7. Resolve reports

### Test 7: Real-time Notifications (Sockets)

1. Open two client windows
2. Login with different accounts
3. Perform actions (add place, approve, etc.)
4. Both clients should receive notifications

---

## 🔧 Troubleshooting

### Problem 1: "Cannot connect to database"

**Solution:**
- Verify MySQL is running in XAMPP
- Check database name is `lfp_ap_db`
- Verify credentials in `database.properties`
- Test connection:
  ```cmd
  mysql -u root -p
  USE lfp_ap_db;
  ```

### Problem 2: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
- Add MySQL Connector/J to classpath
- If using Maven: `mvn clean install`
- If manual: Download and add to `lib/` folder

### Problem 3: "Error: JavaFX runtime components are missing"

**Solution:**
- Add VM options:
  ```
  --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml
  ```
- Verify JavaFX SDK path is correct

### Problem 4: "RMI Registry already in use"

**Solution:**
- Another instance is running
- Kill the process:
  ```cmd
  taskkill /F /IM java.exe
  ```
- Or change RMI port in `database.properties`

### Problem 5: "Socket bind failed: Address already in use"

**Solution:**
- Another server is running on port 8888
- Kill the process or change port in `database.properties`

### Problem 6: "BCrypt class not found"

**Solution:**
- Add jbcrypt library to classpath
- Maven: Already in `pom.xml`
- Manual: Download from https://mvnrepository.com/artifact/org.mindrot/jbcrypt

---

## 📦 Building Executable JAR

### Using Maven:

```cmd
mvn clean package
```

This creates `target/laptop-friendly-places-ap-1.0.0.jar`

### Running the JAR:

**Server:**
```cmd
java -cp target/laptop-friendly-places-ap-1.0.0.jar com.lfp.server.LFPServer
```

**Client:**
```cmd
java --module-path "C:\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml -cp target/laptop-friendly-places-ap-1.0.0.jar com.lfp.client.LFPClient
```

---

## 🌐 Network Configuration

### Running on Different Machines:

1. **Server Machine:**
   - Note the IP address: `ipconfig` (Windows) or `ifconfig` (Linux/Mac)
   - Update `database.properties`:
     ```properties
     rmi.host=192.168.1.100
     socket.host=192.168.1.100
     ```

2. **Client Machine:**
   - Update connection settings to point to server IP
   - Ensure firewall allows connections on ports 1099 and 8888

---

## 📚 Additional Resources

- **JavaFX Documentation**: https://openjfx.io/
- **RMI Tutorial**: https://docs.oracle.com/javase/tutorial/rmi/
- **Socket Programming**: https://docs.oracle.com/javase/tutorial/networking/sockets/
- **MySQL JDBC**: https://dev.mysql.com/doc/connector-j/8.0/en/

---

## ✅ Verification Checklist

- [ ] Java 17+ installed
- [ ] JavaFX SDK downloaded and configured
- [ ] MySQL server running
- [ ] Database `lfp_ap_db` created
- [ ] Sample data inserted
- [ ] Dependencies installed (Maven or manual)
- [ ] IDE configured with JavaFX
- [ ] Server starts without errors
- [ ] Client GUI appears
- [ ] Can login with admin credentials
- [ ] Can browse places
- [ ] Can add to favorites
- [ ] Admin can approve places
- [ ] Real-time notifications work

---

## 🆘 Getting Help

If you encounter issues:

1. Check server console for error messages
2. Check client console for exceptions
3. Verify database connection
4. Review this guide step-by-step
5. Check firewall settings
6. Consult team members

---

**Setup completed successfully! 🎉**

You're now ready to develop and test the LFP-AP application!
