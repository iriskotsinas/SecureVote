package temp;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Server {
	private int port;
	private SSLServerSocket sss;
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

		} catch (Exception x) {
			System.out.println(x);
			x.printStackTrace();
		}
	}

	public SSLServerSocket getServerSocket() {
		return sss;
	}
}
