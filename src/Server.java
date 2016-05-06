/**
 * Created by KarlFredrik on 2016-04-13.
 */

import java.net.*;
import java.io.*;



public class Server {

    public static final int port = 9002;
    public static String receiverPort;
    public static String receiverIP;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Can't start listening on port " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        VoteReceiver voteReceiver = new VoteReceiver();

        //ServerDispatcher serverDispatcher = new ServerDispatcher();
        //serverDispatcher.start();

        while (true) {
            try {

                Socket socket = serverSocket.accept();
                System.out.println("Connection received from " +
                        socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort());
                ClientInfo clientInfo = new ClientInfo();
                clientInfo.mSocket = socket;

                ClientListener clientListener =
                        new ClientListener(clientInfo, voteReceiver);
                clientListener.start();


            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
