package temp;
//
//import javax.net.ssl.*;
//import java.io.FileInputStream;
//import java.security.KeyStore;
//
//public class Server {
//    private String keystore, truststore, passphrase, passphrase2;
//    private int port;
//    private SSLServerSocket sss;
//	static final int CLA_PORT = 8188;
//	static final int CTF_PORT = 8189;
//	static final String KEYSTORE = "src/temp/LIUkeystore.ks";
//	static final String TRUSTSTORE = "src/temp/LIUtruststore.ks";
//	static final String KEYSTOREPASS = "123456";
//	static final String TRUSTSTOREPASS = "abcdef";
//
//    public Server(int port) {
//        this.keystore = KEYSTORE;
//        this.truststore = TRUSTSTORE;
//        this.passphrase = KEYSTOREPASS;
//        this.passphrase2 = TRUSTSTOREPASS;
//        this.port = port;
//        init();
//    }
//
//    public void init() {
//        try {
////			KeyStore ks = KeyStore.getInstance("JCEKS");
////			ks.load(new FileInputStream(KEYSTORE), KEYSTOREPASS.toCharArray());
////
////			KeyStore ts = KeyStore.getInstance("JCEKS");
////			ts.load(new FileInputStream(TRUSTSTORE), TRUSTSTOREPASS.toCharArray());
////
////			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
////			kmf.init(ks, KEYSTOREPASS.toCharArray());
////
////			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
////			tmf.init(ts);
//            // Load keystores
//            System.out.print("Loading keystores...");
//            KeyStore ks = KeyStore.getInstance("JCEKS");
//            ks.load(new FileInputStream(keystore),
//                    passphrase.toCharArray());
//            KeyStore ts = KeyStore.getInstance("JCEKS");
//            ts.load(new FileInputStream(truststore),
//                    passphrase2.toCharArray());
//            System.out.print("Keystores done.\n");
//
//            // Setup key/trust managers
//            System.out.print("Preparing trust managers...");
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//            kmf.init(ks, passphrase.toCharArray());
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//            tmf.init(ts);
//            System.out.print("Trust managers loaded.\n");
//
//            // Setup ssl server
//            System.out.print("Starting server...");
//            SSLContext serverContext = SSLContext.getInstance("TLS");
//            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//            SSLServerSocketFactory sslServer = serverContext.getServerSocketFactory();
//            System.out.print("Server started.\n");
//
//            sss = (SSLServerSocket) sslServer.createServerSocket(port);
//            sss.setEnabledCipherSuites(sss.getSupportedCipherSuites());
//
//            // Require socket auth
//            sss.setNeedClientAuth(true);
//            System.out.println("Server running on port " + port);
//        } catch (Exception e) {
//            System.out.println("Could not initiate server:");
//            e.printStackTrace();
//        }
//    }
//
//    public SSLServerSocket getServerSocket() {
//        return sss;
//    }
//}

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.StringTokenizer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class Server {
	private int port;
	private SSLServerSocket sss;
	// This is not a reserved port number
	static final int CLA_PORT = 8189;
	static final int CTF_PORT = 8188;
	static final String KEYSTORE = "src/temp/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/temp/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	/**
	 * Constructor
	 * 
	 * @param port
	 *            The port where the server will listen for requests
	 */

	// Should it be public?
	public Server(int port) {
		this.port = port;
		run();
	}

	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(KEYSTORE), KEYSTOREPASS.toCharArray());

			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load(new FileInputStream(TRUSTSTORE), TRUSTSTOREPASS.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, KEYSTOREPASS.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			sss = (SSLServerSocket) sslServerFactory.createServerSocket(port);
			sss.setNeedClientAuth(true);
			sss.setEnabledCipherSuites(sss.getSupportedCipherSuites());

			System.out.println("\n>>>> Server: active ");
			SSLSocket incoming = (SSLSocket) sss.accept();

			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);

			// String str;
			// while ( !(str = in.readLine()).equals("") ) {
			// double result = 0;
			// StringTokenizer st = new StringTokenizer( str );
			// try {
			// while( st.hasMoreTokens() ) {
			// Double d = new Double( st.nextToken() );
			// result += d.doubleValue();
			// }
			// out.println( "The result is " + result );
			// }
			// catch( NumberFormatException nfe ) {
			// out.println( "Sorry, your list contains an invalid number" );
			// }
			// }
			// incoming.close();
		} catch (Exception x) {
			System.out.println(x);
			x.printStackTrace();
		}
	}

	public SSLServerSocket getServerSocket() {
		return sss;
	}
}
