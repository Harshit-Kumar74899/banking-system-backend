import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;


class bmsMain {

    public static void main(String[] args) throws Exception {
        // Check DB Connection First
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            if (c != null) {
                System.out.println("Database Connection Successful!");
            }
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
            return; // Stop if DB connection fails
        }

        // Start HttpServer on Port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define context to serve HTML pages
        server.createContext("/", new HtmlHandler());
        server.createContext("/adminLogin", new AdminLoginHandler());
        server.createContext("/userLogin", new UserLoginHandler());
        server.createContext("/addAccount", new AddAccountHandler());
        server.createContext("/showUserInfo", new ShowUserInfoHandler());
        server.createContext("/deposit", new DepositHandler());
        server.createContext("/withdraw", new WithdrawHandler());
        server.createContext("/removeAccount", new RemoveAccountHandler());
        server.createContext("/viewUserDetails", new ViewUserDetailsHandler());


        server.setExecutor(null); // Default executor
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    // Serve HTML Pages
    static class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            File file = null;

            // Serve HTML files based on the path
            if (path.equals("/") || path.equals("/Front.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/Front.html");
            }else if (path.equals("/index.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/index.html");
            } else if (path.equals("/User.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/User.html");
            } else if (path.equals("/Admin.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/Admin.html");
            } else if (path.equals("/userPanel.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/userPanel.html");
            } else if (path.equals("/adminPanel.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/adminPanel.html");
            } 
            else if (path.equals("/Deposit.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/Deposit.html");
            } else if (path.equals("/Withdraw.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/Withdraw.html");
            }
            else if (path.equals("/TotalAmount.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/TotalAmount.html");
            }
            else if (path.equals("/AddAccount.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/AddAccount.html");
            } else if (path.equals("/RemoveAccount.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/RemoveAccount.html");
            } else if (path.equals("/ViewUserDetails.html")) {
                file = new File("C:/Users/H.P/OneDrive/Desktop/test_java/Banking System/web/ViewUserDetails.html");
            }            

            // Serve the file if it exists
            if (file != null && file.exists()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                String errorMessage = "404 - Page not found!";
                exchange.sendResponseHeaders(404, errorMessage.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorMessage.getBytes());
                os.close();
            }
        }
    }

    // Handle Admin Login
    static class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);

                // Decode URL-encoded values
                String AdName = URLDecoder.decode(params.get("AdminName"), "UTF-8");
                String AdPass = URLDecoder.decode(params.get("AdminPass"), "UTF-8");

                boolean loginSuccess = adminLogin(AdName, AdPass);

                String response = loginSuccess ?
                        "<html><script>window.location.href='/adminPanel.html';</script></html>" :
                        "Invalid Admin Credentials!";

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Handle User Login
    static class UserLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);

                int AccNumber = Integer.parseInt(params.get("AccNumber"));
                String Password = URLDecoder.decode(params.get("Password"), "UTF-8");

                boolean loginSuccess = userLogin(AccNumber, Password);

                // Get Username after successful login
                if (loginSuccess) {
                    String userName = getUserName(AccNumber);
                    String response = "<html><script>window.location.href='/userPanel.html?name=" + userName + "';</script></html>";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    String response = "Invalid User Credentials!";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }
    }

    // Add Account Handler
    static class AddAccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
    
                // Extract and convert PhoneNumber to long
                String phoneStr = params.get("PhoneNumber");
                long PhoneNumber = Long.parseLong(phoneStr);
    
                String UserName = params.get("UserName");
                double Amount = Double.parseDouble(params.get("Amount"));
                String Password = params.get("Password");
    
                boolean success = addUser(UserName, PhoneNumber, Amount, Password);
                String response = success ? "User Account Added Successfully!" : "Failed to Add User Account!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    

    // Show User Info Handler
    static class ShowUserInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Fetching User Info Triggered!");
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);

                int AccNumber = Integer.parseInt(params.get("AccNumber"));
                String userInfo = getUserInfo(AccNumber);
                exchange.sendResponseHeaders(200, userInfo.length());
                OutputStream os = exchange.getResponseBody();
                os.write(userInfo.getBytes());
                os.close();
            }
        }
    }

    // Deposit Handler
    static class DepositHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);

                int AccNumber = Integer.parseInt(params.get("AccNumber"));
                double amount = Double.parseDouble(params.get("Amount"));

                boolean success = depositAmount(AccNumber, amount);
                String response = success ? "Amount Deposited Successfully!" : "Failed to Deposit!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Withdraw Handler
    static class WithdrawHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);

                int AccNumber = Integer.parseInt(params.get("AccNumber"));
                double amount = Double.parseDouble(params.get("Amount"));

                boolean success = withdrawAmount(AccNumber, amount);
                String response = success ? "Amount Withdrawn Successfully!" : "Failed to Withdraw!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Remove Account Handler
static class RemoveAccountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String, String> params = parseFormData(formData);

            int AccNumber = Integer.parseInt(params.get("AccNumber"));
            boolean success = removeAccount(AccNumber);

            String response = success ? "Account Removed Successfully!" : "Failed to Remove Account!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

// View User Details Handler
static class ViewUserDetailsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String, String> params = parseFormData(formData);

            int AccNumber = Integer.parseInt(params.get("AccNumber"));
            String userInfo = getUserInfo(AccNumber);

            exchange.sendResponseHeaders(200, userInfo.length());
            OutputStream os = exchange.getResponseBody();
            os.write(userInfo.getBytes());
            os.close();
        }
    }
}


    // Database Logic for Admin Login
    public static boolean adminLogin(String AdName, String AdPass) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("SELECT * FROM admin_info WHERE BINARY Admin_Name = ? AND BINARY Admin_Password = ?");
            ps.setString(1, AdName);
            ps.setString(2, AdPass);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Database Logic for User Login
    public static boolean userLogin(int AccNumber, String Password) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("SELECT * FROM bank WHERE Acc_Number = ? AND BINARY password = ?");
            ps.setInt(1, AccNumber);
            ps.setString(2, Password);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Get User Name after successful login
    public static String getUserName(int AccNumber) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("SELECT User_Name FROM bank WHERE Acc_Number = ?");
            ps.setInt(1, AccNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("User_Name");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return "User";
    }

    // Add User Account
    public static boolean addUser(String UserName, long PhoneNumber, double Amount, String Password) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("INSERT INTO bank (User_Name, Contact_Number, Amount, Password) VALUES (?, ?, ?, ?)");
    
            ps.setString(1, UserName);
            ps.setLong(2, PhoneNumber);  // Corrected
            ps.setDouble(3, Amount);
            ps.setString(4, Password);
    
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    

    // Remove User Account
public static boolean removeAccount(int AccNumber) {
    try {
        Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
        PreparedStatement ps = c.prepareStatement("DELETE FROM bank WHERE Acc_Number = ?");
        ps.setInt(1, AccNumber);
        int rowsAffected = ps.executeUpdate();

        return rowsAffected > 0;
    } catch (SQLException e) {
        System.out.println(e);
    }
    return false;
}


    // Deposit Amount
    public static boolean depositAmount(int AccNumber, double amount) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("UPDATE bank SET Amount = Amount + ? WHERE Acc_Number = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, AccNumber);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Withdraw Amount
    public static boolean withdrawAmount(int AccNumber, double amount) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement checkBalance = c.prepareStatement("SELECT Amount FROM bank WHERE Acc_Number = ?");
            checkBalance.setInt(1, AccNumber);
            ResultSet rs = checkBalance.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("Amount");
                if (balance >= amount) {
                    PreparedStatement ps = c.prepareStatement("UPDATE bank SET Amount = Amount - ? WHERE Acc_Number = ?");
                    ps.setDouble(1, amount);
                    ps.setInt(2, AccNumber);
                    int rowsAffected = ps.executeUpdate();
                    return rowsAffected > 0;
                } else {
                    System.out.println("Insufficient Balance!");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Get User Info
    public static String getUserInfo(int AccNumber) {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/admin", "root", "1234");
            PreparedStatement ps = c.prepareStatement("SELECT Acc_Number, User_Name, Amount FROM bank WHERE Acc_Number = ?");
            ps.setInt(1, AccNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int accNum = rs.getInt("Acc_Number");
                String userName = rs.getString("User_Name");
                double amount = rs.getDouble("Amount");
                return "Account Number: " + accNum + "<br>User Name: " + userName + "<br>Balance: " + amount;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return "User not found!";
    }

    // Parse Form Data
    public static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }
}