import com.lfp.rmi.UserService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestRMI {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            UserService userService = (UserService) registry.lookup("UserService");
            
            // Test login
            var user = userService.login("admin@laptopfriendly.com", "admin123");
            System.out.println("✓ Login successful: " + user.getUsername());
            
            // Test get all users
            var users = userService.getAllUsers();
            System.out.println("✓ Total users: " + users.size());
            
        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

Compile and run:
```powershell
javac -cp "target\classes;lib\*" TestRMI.java
java -cp ".;target\classes;lib\*" TestRMI
```

Expected output:
```
✓ Login successful: admin
✓ Total users: 2
```

### Test 3: Check Socket Server
Create a simple socket client:

```java
// TestSocket.java
import com.lfp.socket.Message;
import com.lfp.socket.MessageType;
import java.io.*;
import java.net.Socket;

public class TestSocket {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Receive welcome message
            Message welcome = (Message) in.readObject();
            System.out.println("✓ Received: " + welcome.getContent());
            
            // Send heartbeat
            out.writeObject(new Message(MessageType.HEARTBEAT, "ping"));
            out.flush();
            
            // Receive response
            Message response = (Message) in.readObject();
            System.out.println("✓ Heartbeat response: " + response.getContent());
            
            socket.close();
            System.out.println("✓ Socket test successful!");
            
        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}