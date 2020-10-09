package temp;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;

public class Client {

    private String keystore, truststore, passphrase, passphrase2;
    private int port;
    private InetAddress host;
	static final int CLA_PORT = 8188;
	static final int CTF_PORT = 8189;
	static final String KEYSTORE = "src/temp/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/temp/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

    SSLSocket socket;

    public Client(InetAddress host, int port) {
        this.keystore = KEYSTORE;
        this.truststore = TRUSTSTORE;
        this.passphrase = KEYSTOREPASS;
        this.passphrase2 = TRUSTSTOREPASS;
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        try {
            // Load keystores
            KeyStore ks = KeyStore.getInstance("JCEKS");
            ks.load(new FileInputStream(keystore),
                    passphrase.toCharArray());
            KeyStore ts = KeyStore.getInstance("JCEKS");
            ts.load(new FileInputStream(truststore),
                    passphrase2.toCharArray());

            // Setup key managers
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passphrase.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);

            // Setup ssl
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLSocketFactory sslFact = sslContext.getSocketFactory();

            System.out.println("Connecting socket to " + host.toString() + ":" + port);
            socket = (SSLSocket) sslFact.createSocket(host, port);
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SSLSocket getSocket() {
        return socket;
    }
}
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.security.KeyStore;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocket;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManagerFactory;
//
//public class Client {
//	private InetAddress host;
//	private int port;
//	private SSLSocket client;
//	// This is not a reserved port number 
//	static final int DEFAULT_PORT = 8189;
//	static final String KEYSTORE = "src/temp/LIUkeystore.ks";
//	static final String TRUSTSTORE = "src/temp/LIUtruststore.ks";
//	static final String KEYSTOREPASS = "123456";
//	static final String TRUSTSTOREPASS = "abcdef";
//  
//	
//	// Constructor @param host Internet address of the host where the server is located
//	// @param port Port number on the host where the server is listening
//	public Client( InetAddress host, int port ) {
//		this.host = host;
//		this.port = port;
//		run();
//	}
//	
//	public void run() {
//		try {
//			KeyStore ks = KeyStore.getInstance( "JCEKS" );
//			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
//			
//			KeyStore ts = KeyStore.getInstance( "JCEKS" );
//			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
//			
//			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
//			kmf.init( ks, KEYSTOREPASS.toCharArray() );
//			
//			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
//			tmf.init( ts );
//			
//			SSLContext sslContext = SSLContext.getInstance( "TLS" );
//			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
//			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
//			client = (SSLSocket)sslFact.createSocket(host, port);
//			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
//			System.out.println("\n>>>> SSL/TLS handshake completed");
//
//			
//			BufferedReader socketIn;
//			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
//			PrintWriter socketOut = new PrintWriter( client.getOutputStream(), true );
//			
////			String numbers = "1.2 3.4 5.6";
////			System.out.println( ">>>> Sending the numbers " + numbers+ " to SecureAdditionServer" );
////			socketOut.println( numbers );
////			System.out.println( socketIn.readLine() );
////
////			socketOut.println ( "" );
//		}
//		catch( Exception x ) {
//			System.out.println( x );
//			x.printStackTrace();
//		}
//	}
//	
//    public SSLSocket getSocket() {
//        return client;
//    }
//}
