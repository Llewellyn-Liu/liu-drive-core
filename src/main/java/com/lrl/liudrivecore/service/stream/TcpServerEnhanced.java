package com.lrl.liudrivecore.service.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * v2 - Removed the no data timeout. Client will be closed when no data coming in.
 *
 * The TcpServerEnhanced is a tool based on Socket to listen to tcp port and get the data
 * as stream.
 * A server has only one instance and listen to single port.
 * A server can repeatedly listen to a connection, if former connection is properly closed.
 * An output stream is need for the constructor.
 *
 * Timeout used when:
 * - a connection closed and no new connect attempt made.
 */
public class TcpServerEnhanced {

    Logger logger = LoggerFactory.getLogger(TcpServerEnhanced.class);
    private ServerSocket serverSocket;

    private Socket client;

    private BufferedOutputStream socketOut;

    private BufferedInputStream socketIn;

    private OutputStream targetOutputStream;

    private static final Integer PORT = 8002;

    private static final int CONNECTION_TIMEOUT = 5 * 1000;

    private static final int NO_DATA_TIMEOUT = 5 * 1000;

    private static final int MAX_TIMEOUT_RETRY_TIMES = 5;

    /**
     * Legal status value:
     * 0: idle(need init)
     * 1: wait for connection
     * 2: connected and reading
     * 3: connection closed
     */
    private int status = 0;

    // Init
    public TcpServerEnhanced(OutputStream targetOut) {
        targetOutputStream = targetOut;
        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
            status = 1;
        } catch (IOException e) {
            logger.error("Tcp Server build failed.");
        }

    }

    public TcpServerEnhanced() {

        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
            status = 1;
        } catch (IOException e) {
            logger.error("Tcp Server build failed.");
        }

    }

    public void run() {
        int retries = 0;

        while (status < 4) {
            if (!startAndListen()) {
                retries++;
                logger.info("Timeout. Retry: " + retries);
                if (retries < MAX_TIMEOUT_RETRY_TIMES) continue;
                else close();
                break;
            }
            status = 3;

            if (connectedAndRead()) close();

        }

        //Terminate
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("ServerSocket close failed");
        }
        logger.info("Tcp server terminated. You may close your output stream.");
    }


    public boolean startAndListen() {
        logger.info("Tcp server listening.");

        try {
            client = serverSocket.accept();
            logger.info("Tcp connection built.");

            // Prepare I/O channels
            socketOut = new BufferedOutputStream(client.getOutputStream());
            socketIn = new BufferedInputStream(client.getInputStream(), 65536);

        } catch (SocketTimeoutException e) {
            return false;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean connectedAndRead() {

        // Signals
        int available = 0, lines = 0, sum = 0;
        long startTime = System.currentTimeMillis();
        long hasWaited = 0;

        int sign;
        try {
            while ((sign = socketIn.read()) != -1) {
                    byte[] data = socketIn.readNBytes(socketIn.available());
                    lines++;
                    sum += available;

                    if(targetOutputStream != null) targetOutputStream.write(data);

                    String dataStr = new String(data);
                    if (dataStr.equals("End")) {
                        logger.info(String.format("%s, lines: %d, size: %d", dataStr, lines, sum));
                        return true;
                    }

                }
            System.out.println("Sign == -1 closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }

    /**
     * Close I/O channels
     * DO NOT CLOSE ServerSocket, it may be used for next retry.
     */
    public void close() {
        logger.info("Closing");
        try {
            if (socketIn != null) socketIn.close();
            if (socketOut != null) socketOut.close();
            if (client != null) client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public int getStatus() {
        return status;
    }

    public void attachOutputStream(OutputStream outputStream){
        this.targetOutputStream = outputStream;
    }

    public void detachOutputStream(){
        this.targetOutputStream = null;
    }

}
