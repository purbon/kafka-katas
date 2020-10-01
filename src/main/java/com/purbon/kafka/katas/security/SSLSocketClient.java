package com.purbon.kafka.katas.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketClient {

  private final FileInputStream keystore;
  private final String keystorepass;
  private final String type;
  private SSLSocket socket;

  public SSLSocketClient(FileInputStream keystore, String keystorepass, String type) {
    this.keystore = keystore;
    this.keystorepass = keystorepass;
    this.type = type;
  }

  public void connect(String host, int port) throws IOException {

    SSLSocketFactory factory = null;
    try {
      SSLContext context = SSLContext.getInstance("TLS");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      KeyStore ks = KeyStore.getInstance(type);

      ks.load(keystore, keystorepass.toCharArray());
      kmf.init(ks,  keystorepass.toCharArray());
      context.init(kmf.getKeyManagers(), null, null);

      factory = context.getSocketFactory();

    } catch (Exception ex) {
      throw new IOException(ex);
    }

    socket = (SSLSocket)factory.createSocket(host, port);
  }

  public void read(String path) throws IOException {
    socket.startHandshake();
    PrintWriter out = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
    out.println("GET " + path + " HTTP/1.0");
    out.println();
    out.flush();

    if (out.checkError())
      System.out.println("SSLSocketClient: java.io.PrintWriter error");

    /* read response */
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    String inputLine;

    while ((inputLine = in.readLine()) != null)
      System.out.println(inputLine);

    in.close();
    out.close();
  }

  public void close() throws IOException {
    socket.close();
  }

  public static void main(String [] args) throws IOException {

    System.out.println("JKS execution");
    SSLSocketClient client = new SSLSocketClient(new FileInputStream(args[1]), "confluent", "JKS");
    client.connect("news.ycombinator.com", 443);
    client.read("/");
    client.close();

    System.out.println("PKS execution");
    client = new SSLSocketClient(new FileInputStream(args[1]), "confluent", "pkcs12");
    client.connect("news.ycombinator.com", 443);
    client.read("/");
    client.close();


  }
}
