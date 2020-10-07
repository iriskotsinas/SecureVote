package Election;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Vector;
import javax.net.ssl.*;

import temp.Client;
import temp.Voter;
import CLA.CLA;

public class election {

    // Class variables
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private Vector<Voter> voters;
    
    
	 public void run() throws Exception {
		// Connect to cla
	    //startClient(InetAddress.getLocalHost(), );
	    Client client = new Client(InetAddress.getLocalHost(),CLA.CLA_PORT);
        SSLSocket c = client.getSocket();
        // Setup transmissions
        socketIn = new BufferedReader(new InputStreamReader(c.getInputStream()));
        socketOut = new PrintWriter(c.getOutputStream(), true);
		
     // Create 10 voters with the CLA
        voters = new Vector<>();
        for (int i = 0; i < 10; i++) {
            voters.add(new Voter(i % 2));
        }
        
        socketOut.println("get_validation_nr"); 
        
        // ------    STEP 1   AND STEP 2 --------
        for (Voter v : voters) {
        	
        	socketOut.println("ID="+v.getId()); 
        	String response; // response from from server, validation number
        	
        	while (!(response = socketIn.readLine()).equals("end")) {
                // System.out.println(response);
        		if (!new BigInteger(response).toString().equals("0")) {
        			v.setValidationNumber(new BigInteger(response));
        		} else {
        			System.out.println("Not authorized"); 
        		}
        	}
        			
        }
        socketOut.println("end"); 
        
        // STEP 4
   
        // Receive response (validation number) from server
	 }
	
	public static void main(String[] args) {
		try {
            election c = new election();
            c.run();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
	}

}
