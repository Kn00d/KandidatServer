/**
 * Created by KarlFredrik on 2016-04-13.
 */
import java.io.*;
import java.net.*;
import java.util.*;

//import Database.Database;
import com.google.gson.JsonArray;

public class ClientSender extends Thread
{
    private LinkedList<String> messageQueue = new LinkedList<String>();
    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private PrintWriter mOut;
    private String message;
    //
    // private Database db;
    //

    public ClientSender(ClientInfo aClientInfo, String message)
            throws IOException
    {
        mClientInfo = aClientInfo;
        this.message=message;
        //mServerDispatcher = aServerDispatcher;
        Socket socket = aClientInfo.mSocket;
        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Adds given message to the message queue and notifies this thread
     * (actually getNextMessageFromQueue method) that a message is arrived.
     * sendMessage is called by other threads (ServeDispatcher).
     */
    public synchronized void sendMessage(String aMessage)
    {
        messageQueue.add(aMessage);
        notify();
    }

    /**
     * @return and deletes the next message from the message queue. If the queue
     * is empty, falls in sleep until notified for message arrival by sendMessage
     * method.
     */

    private synchronized String getNextMessageFromQueue() throws InterruptedException
    {
        while(messageQueue.size()==0){
            wait();
        }
        String message = messageQueue.get(0);
        messageQueue.remove(message);
        return message;
    }

    /**
     * Sends given message to the client's socket.
     */
    private void sendMessageToClient(String aMessage)
    {
        mOut.println(aMessage);
        mOut.flush();
    }

    /**
     * Until interrupted, reads messages from the message queue
     * and sends them to the client's socket.
     */
    public void run()
    {
        //db = new Database();
        //JsonArray jsonArray;
        try {
            //while (!isInterrupted()) {
            //String message = getNextMessageFromQueue();
            sendMessageToClient(message);
            /**
             * DATABASKOD
             */
            /*if(mServerDispatcher.action.equalsIgnoreCase("update")){
               jsonArray = db.selectData(message);
                sendJSON(jsonArray);
            } else if(mServerDispatcher.action.equalsIgnoreCase("delete")){
                db.updateData(message);
            }else if (mServerDispatcher.action.equalsIgnoreCase("add")){
                db.updateData(message);
            } else {

                System.out.println(mClientInfo.mSocket.getInetAddress() + " : "+mClientInfo.mSocket.getPort()+ " sent request: " + message);
            }*/
            //}
        } catch (Exception e) {
            // Communication problem
        }
        // Communication is broken. Interrupt both listener and sender threads
        mClientInfo.mClientListener.interrupt();
        // mServerDispatcher.deleteClient(mClientInfo);
    }

    void sendJSON(JsonArray jsonArray) {
        String message = jsonArray.toString();
        sendMessageToClient(message);

    }

}