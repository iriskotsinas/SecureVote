package temp;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {
	private InetAddress host;
	private int port;
	private SSLSocket client;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "src/temp/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/temp/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	// Constructor @param host Internet address of the host where the server is
	// located
	// @param port Port number on the host where the server is listening
	public Client(InetAddress host, int port) {
		this.host = host;
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
			SSLSocketFactory sslFact = sslContext.getSocketFactory();
			client = (SSLSocket) sslFact.createSocket(host, port);
			client.setEnabledCipherSuites(client.getSupportedCipherSuites());
			System.out.println("\n>>>> SSL/TLS handshake completed");

		} catch (Exception x) {
			System.out.println(x);
			x.printStackTrace();
		}
	}

	public SSLSocket getSocket() {
		return client;
	}
}
