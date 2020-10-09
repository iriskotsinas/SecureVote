package Election;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Vector;
import javax.net.ssl.*;

import temp.Client;
import temp.Voter;

public class election {
	public static final int CLA_PORT = 8188;
	public static final int CTF_PORT = 8189;

	// Class variables
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	private Vector<Voter> voters;

	private void startClient(InetAddress host, int port) throws Exception {
		Client client = new Client(host, port);
		SSLSocket c = client.getSocket();
		// Setup transmissions
		socketIn = new BufferedReader(new InputStreamReader(c.getInputStream()));
		socketOut = new PrintWriter(c.getOutputStream(), true);
	}

	public void run() throws Exception {
		// Connect to CLA
		// startClient(InetAddress.getLocalHost(), );
		Client client = new Client(InetAddress.getLocalHost(), CLA_PORT);
		SSLSocket c = client.getSocket();
		// Setup transmissions
		socketIn = new BufferedReader(new InputStreamReader(c.getInputStream()));
		socketOut = new PrintWriter(c.getOutputStream(), true);

		// STEP 4
		// Create 10 voters with the CLA, rand id nr
		voters = new Vector<>();
		for (int i = 0; i < 20; i++) {
			voters.add(new Voter((int) Math.round(Math.random())));
			// voters.add(new Voter(i % 2));
			// System.out.println(voters.get(i).getChoice());
		}

		// TODO random 0 or 1

		socketOut.println("get_validation_nr");

		// ------ STEP 1 AND STEP 2 --------
		for (Voter v : voters) {
			socketOut.println("ID=" + v.getId());
			String response; // response from from server, validation number

			while (!(response = socketIn.readLine()).equals("end")) {
				// System.out.println(response);
				if (!new BigInteger(response).toString().equals("0")) {
					v.setValidationNumber(new BigInteger(response));
				}
			}
		}
		socketOut.println("end");

		// STEP 4
		// Receive response (validation number) from server
		startClient(InetAddress.getLocalHost(), CTF_PORT);

		// Send votes
		for (Voter v : voters) {
			socketOut.println("register_vote");
			socketOut.println(v.myVote());
			socketOut.println("end");
		}

		System.out.println("\n*********************************************");
		System.out.println("* WELCOME TO THE PRESIDENTIAL ELECTION 2020 *");
		System.out.println("*********************************************");
		System.out.println("\n");
		System.out.println("Please, vote 0 for Biden and 1 for Trump. \n");
		// Get result
		socketOut.println("result");
		String result;
		while (!(result = socketIn.readLine()).equals("end")) {
			System.out.println(result);
		}
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
