import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class EOliverTCPClient 
{

	public static void main(String[] args) 
	{
		String serverName = "localhost";
		//String serverName = "192.168.1.135";
		int port = 9999;
		
		try 
		{
			System.out.println("Connecting to " + serverName + " on port " + port);
			
            /* (2) The client which runs on a different machine should send to the server (IP address of server machine is
            the first command line argument) a file that is provided as the second command line argument. Before
            sending the file, the client should compute the SHA256 hash code for the file. Also, after sending the
            file, the client should start a timer.*/

			Socket clientSocket = new Socket(serverName, port);  //create socket for connecting to server
			
			System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
			
			OutputStream outToServer = clientSocket.getOutputStream();  //stream of bytes
			
			DataOutputStream out = new DataOutputStream(outToServer);
			
			String outText = JOptionPane.showInputDialog("Enter Client Message: ");
			
			System.out.println("TCP Client says: " + outText);
			
			out.writeUTF(outText);
			
            /* (4) When the client receives the hash code from the server, it should stop the timer. The client should then
            check if the hash code from the server is same as its own computed hash code – if so, it should print
            “Successfully sent!” else it should print “Error!”*/

			InputStream inFromServer = clientSocket.getInputStream();  //stream of bytes
			
			DataInputStream in = new DataInputStream(inFromServer);
			
			System.out.println("TCP Server says: " + in.readUTF());
			
            /* (5) The client should then calculate the throughput for the transmission based on the file size and the time
            taken for the receipt of the hash code from server and print this value in Mbps (rounded to two
            decimals). Throughput equals file size in bits/ half the time for receipt of hash code – then convert units
            to Mbps (we are approximating the one-way time here to be half the RTT). The client should print the
            file name, its hash, file size in bits, time taken for server response in milliseconds (rounded to two
            decimals), and the throughput. Output from your client should be similar to the right-side column of
            bottom row of Table 1 - your data will be different for the parameters, of course.*/

			clientSocket.close();
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
