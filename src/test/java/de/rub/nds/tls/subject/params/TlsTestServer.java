/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.nds.tls.subject.params;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TlsTestServer extends Thread {

    private final int port;
    private boolean isServerDone = false;
    private boolean isConnectionSuccessful = false;
    private static final String PATH_TO_KEYSTORE = "./certs/keys.jks";
    private static final String KEYSTORE_PASSWORD = "password";

    TlsTestServer(int port) {
        this.port = port;
    }

    private SSLContext createSSLContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(PATH_TO_KEYSTORE), KEYSTORE_PASSWORD.toCharArray());
            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();
            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();
            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(km, tm, null);
            return sslContext;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        SSLServerSocket sslServerSocket = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = this.createSSLContext();
        try {
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);
            while (!isServerDone) {
                sslSocket = (SSLSocket) sslServerSocket.accept();
                new ServerConnectionThread(sslSocket).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (sslServerSocket != null) {
                try {
                    sslServerSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ServerConnectionThread extends Thread {

        private SSLSocket sslSocket = null;

        ServerConnectionThread(SSLSocket sslSocket) {
            this.sslSocket = sslSocket;
        }

        @Override
        public void run() {
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            try {
                if (sslSocket.isConnected()) {
                    isConnectionSuccessful = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isServerDone() {
        return isServerDone;
    }

    public void stop(String host, int port) throws IOException {
        this.isServerDone = true;
        Socket socket = new Socket(host, port);
    }

    public void setIsConnectionSuccessful(boolean isConnectionSuccessful) {
        this.isConnectionSuccessful = isConnectionSuccessful;
    }

    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}
