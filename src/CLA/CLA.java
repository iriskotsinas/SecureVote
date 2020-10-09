package CLA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Vector;
import javax.net.ssl.*;

import temp.Client;
import temp.Server;
import temp.Voter;

public class CLA implements Runnable {
	private int port;
	// This is not a reserved port number
	public static final int CLA_PORT = 8188;
	public static final int CTF_PORT = 8189;
	static final String KEYSTORE = "src/CLA/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/CLA/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	private SSLSocket incoming;
	private BufferedReader serverInput, clientInput;
	private PrintWriter serverOutput, clientOutput;

	private Vector<Voter> authorizedVoters;

	public CLA(SSLSocket incoming) {
		this.incoming = incoming;
	}

	// Setter
	public void setAuthorizedVoters(Vector<Voter> authorizedVoters) {
		this.authorizedVoters = authorizedVoters;
	}

	private void startClient(InetAddress host, int port) throws Exception {
		Client client = new Client(host, port);
		SSLSocket c = client.getSocket();
		clientInput = new BufferedReader(new InputStreamReader(c.getInputStream()));
		clientOutput = new PrintWriter(c.getOutputStream(), true);
	}

	private void registerVoter() throws Exception {
		String str;
		while (!(str = serverInput.readLine()).equals("end")) {
			System.out.println("s: " + str);
			// Clean the string from 'id=' and parse to int
			int id = Integer.parseInt(str.substring(3));
			Voter v_temp = new Voter(-1, id);
			authorizedVoters.add(v_temp); // validera!!
			serverOutput.println(v_temp.getValidationNr() + "\n" + "end");

			// Send to CTF !
			sendToCTF(v_temp.getValidationNr());
		}
		clientOutput.println("terminate");
	}

	// STEP 3
	// Send validation number to CTF
	private void sendToCTF(String valNr) throws Exception {
        System.out.println("SEND TO CTF");
		clientOutput.println("valid_voter");
		clientOutput.println(valNr);
		clientOutput.println("end");
	}

	public void run() {
		try {
			// Prepare incoming connections
			serverInput = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			serverOutput = new PrintWriter(incoming.getOutputStream(), true);

			// Should be CTF_PORT
			startClient(InetAddress.getLocalHost(), CTF_PORT);
			String str = serverInput.readLine();
			System.out.println("BEFORE CASE");

			while (str != null) {
				switch (str) {
				case "get_validation_nr":
					System.out.println("get_val_nr");
					registerVoter();
					break;
				case "":
					break;
				default:
					System.out.println("Unknown command: " + str);
					break;
				}

				str = serverInput.readLine();
			}

			incoming.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Server s = new Server(CLA_PORT);
			Vector<Voter> allVoters = new Vector<>();
			System.out.println("MAIN");

			while (true) {
				SSLSocket socket = (SSLSocket) s.getServerSocket().accept();
                System.out.println("New CLA client connected");
				CLA c = new CLA(socket);
				c.setAuthorizedVoters(allVoters);
				Thread t = new Thread(c);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
