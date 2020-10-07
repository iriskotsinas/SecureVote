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
	static final int DEFAULT_PORT = 8189;
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
	
    public void run() {
    }
	
    public static void main(String[] args) {
        try {
            Server s = new Server(DEFAULT_PORT);
            Vector<Voter> allVoters = new Vector<>();

            while (true) {
                SSLSocket socket = (SSLSocket) s.getServerSocket().accept();
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
