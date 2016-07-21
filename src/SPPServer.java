import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
public class SPPServer extends Thread {

    //Create a UUID for SPP / RFComm
    UUID uuid = new UUID("1101", true);

    //Create the Servicve URL
    String connectionString = "btspp://localhost:" + uuid + ";name=BlutoothChat";

    public BufferedReader in;
    public PrintWriter out;

    StreamConnectionNotifier streamConnNotifier;

    private ActionListener onConnectionSuccessful;

    public void setOnConnectionSuccessful(ActionListener onConnectionSuccessful) {
        this.onConnectionSuccessful = onConnectionSuccessful;
    }

    //start server
    public void run(){
        try {
            //open server url
            streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

            //Wait for client connection
            System.out.println("\nServer Started. Waiting for clients to connect…");
            StreamConnection connection = streamConnNotifier.acceptAndOpen();

            if(onConnectionSuccessful != null) onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));

            RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
            System.out.println("Remote device address: " + dev.getBluetoothAddress());
            System.out.println("Remote device name: " + dev.getFriendlyName(true));

            //init in/out streams
            InputStream inStream = connection.openInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));

            OutputStream outStream = connection.openOutputStream();
            out = new PrintWriter(new OutputStreamWriter(outStream));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            streamConnNotifier.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //display local device address and name
        /*LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());

        SPPServer sampleSPPServer=new SPPServer();
        sampleSPPServer.startServer();*/
    }

}