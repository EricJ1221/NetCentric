import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EOliverTCPServer {

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

    public static void main(String[] args) {
        ServerSocket serverSocket;

        try {
            // The server runs on port 9999
            serverSocket = new ServerSocket(9999); // Create a socket and bind it to port 9999
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

            while (true) {
                // Wait for a client connection
                Socket connectionSocket = serverSocket.accept();
                //System.out.println("Client connected from: " + connectionSocket.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

                try {
                    // 1. Receive the file name or file data from the client
                    String fileName = in.readUTF();  // Expecting the client to send the file name
                    //System.out.println("Received file name: " + fileName);

                    // 2. Receive the file size
                    int fileLength = in.readInt();  // Expecting the client to send the file size
                    //System.out.println("Expected file size in bytes: " + fileLength);

                    // 3. Receive the file content (fileBytes) from the client
                    byte[] fileBytes = new byte[fileLength];
                    in.readFully(fileBytes);  // Read the file bytes sent by the client
                    int fileSizeInBits = fileBytes.length * 8;
                    System.out.println("Received file content of size: " + fileSizeInBits + " bits");

                    // 4. Compute SHA-256 hash for the received file
                    String fileHash = computeSHA256(fileBytes);
                    System.out.println("Received file SHA-256 hash: " + fileHash + "=");

                    // 5. Send the computed hash back to the client
                    out.writeUTF(fileHash);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Close the connection after processing the client
                connectionSocket.close();
                System.out.println("Connection closed.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
