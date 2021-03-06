package io.fabric8.maven.docker.access.hc.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import jnr.unixsocket.UnixSocketAddress;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

final class UnixConnectionSocketFactory implements ConnectionSocketFactory {

    private final File unixSocketFile;

    public UnixConnectionSocketFactory(String unixSocketPath) {
        this.unixSocketFile = new File(unixSocketPath);
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return new UnixSocket();
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host, InetSocketAddress remoteAddress,
            InetSocketAddress localAddress, HttpContext context)
            throws IOException {
        sock.connect(new UnixSocketAddress(unixSocketFile), connectTimeout);
        return sock;
    }
}
