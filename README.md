# Laptop Friendly Places - Advanced Programming (LFP-AP)

## 🎓 Academic Project - Java Implementation

This is a Java-based implementation of the Laptop Friendly Places platform, developed using advanced programming concepts including JavaFX, Networking (Sockets), RMI (Remote Method Invocation), and Multithreading.

## 📚 Project Overview

**Laptop Friendly Places (LFP-AP)** is a distributed client-server application that helps students, freelancers, and remote workers find laptop-friendly locations such as cafés, libraries, and coworking spaces.

### Key Features:
- 🖥️ **JavaFX GUI** - Modern, responsive user interface
- 🌐 **Socket Programming** - Real-time client-server communication
- 🔗 **RMI** - Remote method invocation for distributed operations
- ⚡ **Multithreading** - Concurrent request handling
- 🗄️ **MySQL Database** - Persistent data storage
- 🔐 **Authentication** - Secure user login and registration
- 📍 **Place Management** - Browse, contribute, and rate locations
- ⭐ **Favorites System** - Save preferred places
- 🚨 **Report System** - Flag inappropriate content
- 👨‍💼 **Admin Dashboard** - Manage users, places, and reports

## 🏗️ Architecture

### Three-Tier Architecture:
1. **Presentation Layer** - JavaFX UI (Client)
2. **Business Logic Layer** - RMI Services + Socket Server
3. **Data Layer** - MySQL Database

### Communication Protocols:
- **RMI** - For complex business operations (CRUD operations)
- **Sockets** - For real-time notifications and chat
- **Threads** - For handling multiple concurrent clients

## 📁 Project Structure

```
LFP-AP/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── lfp/
│       │           ├── client/          # Client-side application
│       │           │   ├── LFPClient.java
│       │           │   └── ClientSocketHandler.java
│       │           ├── server/          # Server-side application
│       │           │   ├── LFPServer.java
│       │           │   ├── SocketServer.java
│       │           │   └── ClientHandler.java
│       │           ├── rmi/             # RMI interfaces & implementations
│       │           │   ├── PlaceService.java
│       │           │   ├── UserService.java
│       │           │   ├── PlaceServiceImpl.java
│       │           │   └── UserServiceImpl.java
│       │           ├── model/           # Data models
│       │           │   ├── User.java
│       │           │   ├── Place.java
│       │           │   ├── Report.java
│       │           │   └── Favorite.java
│       │           ├── socket/          # Socket communication
│       │           │   ├── Message.java
│       │           │   └── MessageType.java
│       │           ├── ui/              # JavaFX controllers
│       │           │   ├── LoginController.java
│       │           │   ├── DashboardController.java
│       │           │   ├── PlacesController.java
│       │           │   └── AdminController.java
│       │           └── util/            # Utilities
│       │               ├── DatabaseUtil.java
│       │               ├── PasswordUtil.java
│       │               └── ValidationUtil.java
│       └── resources/
│           ├── fxml/                    # JavaFX FXML files
│           ├── css/                     # Stylesheets
│           ├── images/                  # Images and icons
│           └── database.properties      # DB configuration
├── lib/                                 # External libraries
├── docs/                                # Documentation
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## 🛠️ Technology Stack

### Core Technologies:
- **Java 17+** - Programming language
- **JavaFX 19+** - GUI framework
- **RMI** - Remote Method Invocation
- **Sockets** - TCP/IP networking
- **Threads** - Concurrent programming
- **MySQL 8.0+** - Database
- **JDBC** - Database connectivity

### Build Tools:
- **Maven** - Dependency management and build automation

### Libraries:
- **JavaFX** - UI components
- **MySQL Connector/J** - JDBC driver
- **BCrypt** - Password hashing
- **Gson** - JSON serialization

## 🚀 Getting Started

### Prerequisites:
1. **Java Development Kit (JDK) 17 or higher**
2. **JavaFX SDK 19 or higher**
3. **MySQL Server 8.0 or higher**
4. **Maven 3.6+** (optional, for dependency management)
5. **IDE** - IntelliJ IDEA, Eclipse, or NetBeans

### Installation Steps:

#### Step 1: Clone/Download the Project
```bash
cd c:\xampp\htdocs\LFP-AP
```

#### Step 2: Setup Database
1. Start MySQL server
2. Open MySQL Workbench or command line
3. Run the database schema:
```bash
mysql -u root -p < docs/database_schema.sql
```

#### Step 3: Configure Database Connection
Edit `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/lfp_ap_db
db.username=root
db.password=your_password
```

#### Step 4: Compile the Project

**Using Maven:**
```bash
mvn clean compile
```

**Using javac (manual):**
```bash
javac -d bin --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml src/main/java/com/lfp/**/*.java
```

#### Step 5: Start the Server
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.lfp.server.LFPServer"

# Or using java command
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp bin com.lfp.server.LFPServer
```

#### Step 6: Start the Client
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.lfp.client.LFPClient"

# Or using java command
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp bin com.lfp.client.LFPClient
```

## 🎯 Advanced Programming Concepts Demonstrated

### 1. JavaFX (GUI Programming)
- **FXML** - Declarative UI design
- **Controllers** - MVC pattern implementation
- **CSS Styling** - Custom themes
- **Scene Builder** - Visual UI design
- **Event Handling** - User interactions
- **Property Binding** - Reactive UI updates

### 2. Networking with Sockets
- **TCP Sockets** - Reliable client-server communication
- **ServerSocket** - Accepting multiple client connections
- **Input/Output Streams** - Data transmission
- **Protocol Design** - Custom message format
- **Connection Management** - Handling disconnections

### 3. RMI (Remote Method Invocation)
- **Remote Interfaces** - Service contracts
- **Remote Objects** - Distributed implementations
- **Registry** - Service discovery
- **Stub/Skeleton** - Transparent remote calls
- **Serialization** - Object transmission

### 4. Multithreading
- **Thread Pools** - Efficient resource management
- **ExecutorService** - Task scheduling
- **Synchronization** - Thread-safe operations
- **Concurrent Collections** - Thread-safe data structures
- **Locks** - Fine-grained concurrency control

### 5. Design Patterns
- **MVC** - Model-View-Controller
- **Singleton** - Database connection
- **Factory** - Object creation
- **Observer** - Event notification
- **DAO** - Data Access Object

## 📊 Database Schema

### Tables:
1. **users** - User accounts (id, username, email, password, role, is_blocked)
2. **places** - Laptop-friendly locations (id, name, description, location, ratings, status)
3. **favorites** - User favorites (user_id, place_id)
4. **reports** - Place reports (id, place_id, user_id, reason, status)
5. **login_attempts** - Security tracking (email, ip_address, attempted_at)

## 🔐 Default Credentials

### Admin Account:
- **Username:** admin
- **Email:** admin@laptopfriendly.com
- **Password:** admin123

### Test User Account:
- **Username:** testuser
- **Email:** test@example.com
- **Password:** test123

⚠️ **Change these credentials in production!**

## 🧪 Testing the Application

### Test Scenarios:

#### 1. User Authentication
- [ ] Register new user
- [ ] Login with valid credentials
- [ ] Login with invalid credentials
- [ ] Logout

#### 2. Place Management
- [ ] Browse all places
- [ ] View place details
- [ ] Contribute new place
- [ ] Rate a place
- [ ] Search places

#### 3. Favorites
- [ ] Add place to favorites
- [ ] View favorites list
- [ ] Remove from favorites

#### 4. Reports
- [ ] Report a place
- [ ] View report status

#### 5. Admin Functions
- [ ] View all users
- [ ] Block/unblock users
- [ ] Approve pending places
- [ ] Delete places
- [ ] Manage reports

#### 6. Real-time Features (Sockets)
- [ ] Receive notifications
- [ ] Real-time place updates
- [ ] Multiple concurrent clients

#### 7. RMI Operations
- [ ] Remote place CRUD
- [ ] Remote user management
- [ ] Remote report handling

## 📝 API Documentation

### RMI Services:

#### UserService
```java
User login(String email, String password) throws RemoteException;
User register(String username, String email, String password) throws RemoteException;
List<User> getAllUsers() throws RemoteException;
void blockUser(int userId) throws RemoteException;
```

#### PlaceService
```java
List<Place> getAllPlaces() throws RemoteException;
Place getPlaceById(int id) throws RemoteException;
void addPlace(Place place) throws RemoteException;
void updatePlace(Place place) throws RemoteException;
void deletePlace(int id) throws RemoteException;
List<Place> searchPlaces(String query) throws RemoteException;
```

### Socket Messages:

#### Message Types:
- `NOTIFICATION` - System notifications
- `PLACE_UPDATE` - Place data changed
- `USER_ONLINE` - User connected
- `USER_OFFLINE` - User disconnected
- `CHAT` - Chat messages

## 🎓 Learning Objectives

This project demonstrates:
1. ✅ **JavaFX** - Building modern desktop applications
2. ✅ **Socket Programming** - Network communication
3. ✅ **RMI** - Distributed computing
4. ✅ **Multithreading** - Concurrent programming
5. ✅ **Database Integration** - JDBC and SQL
6. ✅ **Design Patterns** - Software architecture
7. ✅ **MVC Architecture** - Separation of concerns
8. ✅ **Security** - Authentication and authorization

## 👥 Team Members

| Name | Student ID | Role |
|------|-----------|------|
| Amen Teshome | ETS 0165/16 | Team Lead, RMI Implementation |
| Amir Abduljelil | ETS 0167/16 | Socket Programming, Networking |
| Betsegaw Tesfaye | ETS 0285/16 | JavaFX UI, Frontend |
| Biniyam Kinfe | ETS 0304/16 | Database Design, Backend |
| Binyam Yalew | ETS 0297/15 | Threading, Concurrency |
| Degaga Desta | ETS 0352/16 | Testing, Documentation |

## 📚 References

- [JavaFX Documentation](https://openjfx.io/)
- [Java RMI Tutorial](https://docs.oracle.com/javase/tutorial/rmi/)
- [Java Socket Programming](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [Java Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 📄 License

This is an academic project for educational purposes.

## 🤝 Contributing

This is a closed academic project. For questions or issues, contact the team members.

---

**Made with ❤️ for Advanced Programming Course**
**Addis Ababa University - Software Engineering Department**
