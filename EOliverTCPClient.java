import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EOliverTCPClient 
{
    public static void main(String[] args) 
    {
        if (args.length != 2) {
            System.out.println("Usage: java EOliverTCPClient <server IP> <file path>");
            return;
        }

        String serverName = "localhost";  // You can change this to args[0] for the real IP if needed
        String filePath = args[1];    // File path from the second command-line argument
        int port = 9999;

        // Declare fileSizeInBits and fileHash outside the inner try block
        long fileSizeInBits = 0;
        String fileHash = "";

        try 
        {
            // Inner try block for file reading and hashing
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }

            // Calculate the size of the file in bits
            long fileSizeInBytes = file.length();  // Get the file size in bytes
            fileSizeInBits = fileSizeInBytes * 8;  // Convert to bits
            System.out.println("File size in bits = " + fileSizeInBits);

            byte[] fileBytes = Files.readAllBytes(file.toPath());  // Read file as bytes
            fileHash = computeSHA256(fileBytes);  // Compute SHA-256 hash
            System.out.println("Computed file SHA256 hash: " + fileHash);

            // Create socket for connecting to server
            Socket clientSocket = new Socket(serverName, port);  
            System.out.println("Connecting to " + serverName + " on port " + port + "...");
            System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());

            OutputStream outToServer = clientSocket.getOutputStream();  // Stream of bytes
            DataOutputStream out = new DataOutputStream(outToServer);
            
            // Send file name
            out.writeUTF(file.getName());

            // Send file size in bytes (not bits)
            out.writeInt((int) fileSizeInBytes);  // Send the file size in bytes

            // Send file content
            out.write(fileBytes);  // Send the file bytes to the server

            // Start the timer after sending the file
            long startTime = System.currentTimeMillis();  // Start timer

            // Receive the hash code from the server
            InputStream inFromServer = clientSocket.getInputStream();  // Stream of bytes
            DataInputStream in = new DataInputStream(inFromServer);
            String serverHash = in.readUTF();  // Read the hash from the server
            System.out.println("SHA256 hash from server: " + serverHash);

            // Stop the timer when response is received
            long endTime = System.currentTimeMillis();  // End timer
            long elapsedTime = endTime - startTime;  // Calculate elapsed time in milliseconds
            double elapsedTimeInSeconds = elapsedTime / 1000.0;  // Convert milliseconds to seconds

            System.out.println("Time taken for server response: " + elapsedTime + " milliseconds");

            // Compare the client-side hash with the server-side hash
            if (fileHash.equals(serverHash)) {
                System.out.println("SHA256 hash matches! File integrity is verified.");
            } else {
                System.out.println("Error: SHA256 hash does not match! File integrity may be compromised.");
            }

            // Calculate the throughput (Mbps)
            double throughput = (fileSizeInBits / elapsedTimeInSeconds) / 1_000_000;  // Convert to Mbps
            System.out.printf("Throughput: %.2f Mbps\n", throughput);

            // Close the client connection
            clientSocket.close();

        } 
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Helper method to compute SHA-256 hash
    private static String computeSHA256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);  // Compute the hash

        // Convert the byte array into a hexadecimal string
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }

}
