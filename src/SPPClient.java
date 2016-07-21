import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * A simple SPP client that connects with an SPP server
 */
public class SPPClient implements DiscoveryListener{

    //object used for waiting
    private static Object lock = new Object();

    //vector containing the devices discovered
    private static Vector<RemoteDevice> vecDevices=new Vector();

    private static String connectionURL=null;

    private ActionListener onDeviceDiscovery;
    private ActionListener onConnectionSuccessful;
    private ActionListener onConnectionFailed;

    DiscoveryAgent agent;

    String partnerName;

    BufferedReader in;
    PrintWriter out;

    public SPPClient(){
        try {
            //display local device address and name
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: "+localDevice.getBluetoothAddress());
            System.out.println("Name: "+localDevice.getFriendlyName());

            agent = localDevice.getDiscoveryAgent();

        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    public void setOnDeviceDiscovery(ActionListener onDeviceDiscovery) {
        this.onDeviceDiscovery = onDeviceDiscovery;
    }

    public void setOnConnectionFailed(ActionListener onConnectionFailed) {
        this.onConnectionFailed = onConnectionFailed;
    }

    public void setOnConnectionSuccessful(ActionListener onConnectionSuccessful) {
        this.onConnectionSuccessful = onConnectionSuccessful;
    }

    public void startDiscovery(){
        vecDevices=new Vector();
        System.out.println("Starting device inquiry…");
        try {
            agent.startInquiry(DiscoveryAgent.GIAC, this);
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RemoteDeviceInfo> getDeviceInfos(){
        ArrayList<RemoteDeviceInfo> res = new ArrayList<>();
        for(RemoteDevice rd : vecDevices){
            try {
                RemoteDeviceInfo rdi = new RemoteDeviceInfo(rd.getFriendlyName(true),rd.getBluetoothAddress());
                res.add(rdi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public void connect(int index){
        try {
            // check for spp service
            RemoteDevice remoteDevice = vecDevices.elementAt(index);
            partnerName = remoteDevice.getFriendlyName(true);
            UUID[] uuidSet = new UUID[1];
            uuidSet[0]=new UUID("1101",true);
            System.out.println("\nSearching for service...");
            agent.searchServices(null,uuidSet,remoteDevice,this);
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

        /*SPPClient client = new SPPClient();



        try {
            synchronized(lock){
                lock.wait();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }



        //print all devices in vecDevices
        int deviceCount = vecDevices.size();

        if(deviceCount <= 0){
            System.out.println("No Devices Found .");
            System.exit(0);
        }else{
            //print bluetooth device addresses and names in the format [ No. address (name) ]
            System.out.println("Bluetooth Devices: ");
            for (int i = 0; i < deviceCount; i++) {
                RemoteDevice remoteDevice=(RemoteDevice)vecDevices.elementAt(i);
                System.out.println((i+1)+". "+remoteDevice.getBluetoothAddress()+" ("+remoteDevice.getFriendlyName(true)+")");
            }
        }

        System.out.print("Choose Device index: ");
        BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));
        String chosenIndex = bReader.readLine();
        int index = Integer.parseInt(chosenIndex.trim());

        // check for spp service
        RemoteDevice remoteDevice = (RemoteDevice)vecDevices.elementAt(index-1);
        UUID[] uuidSet = new UUID[1];
        uuidSet[0]=new UUID("1101",true);
        System.out.println("\nSearching for service...");
        agent.searchServices(null,uuidSet,remoteDevice,client);

        try {
            synchronized(lock){
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(connectionURL==null){
            System.out.println("Device does not support SPP Service.");
            System.exit(0);
        }
        //connect to the server and send a line of text
        StreamConnection streamConnection = (StreamConnection)Connector.open(connectionURL);
        // send string
        OutputStream outStream = streamConnection.openOutputStream();
        PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write("Test String from SPP Client\r\n");
        pWriter.flush();
        // read response
        InputStream inStream = streamConnection.openInputStream();
        BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
        String lineRead = bReader2.readLine();
        System.out.println(lineRead);*/
    }//main

    // methods of DiscoveryListener
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        // add the device to the vector
        if(!vecDevices.contains(btDevice)){
            vecDevices.addElement(btDevice);
        }
    }

    boolean isOK = false;

    //implement this method since services are not being discovered
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        if(servRecord!=null && servRecord.length>0){
            connectionURL = servRecord[0].getConnectionURL(0,false);
        }
        isOK = true;
        try {
            StreamConnection streamConnection = (StreamConnection) Connector.open(connectionURL);
            // send string
            OutputStream outStream = streamConnection.openOutputStream();
            out = new PrintWriter(new OutputStreamWriter(outStream));
            // read response
            InputStream inStream = streamConnection.openInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
            if(onConnectionSuccessful != null) onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //implement this method since services are not being discovered
    public void serviceSearchCompleted(int transID, int respCode) {
        if(!isOK) {
            if(onConnectionFailed != null) onConnectionFailed.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
        }
    }

    public void inquiryCompleted(int discType) {
        if(onDeviceDiscovery != null) onDeviceDiscovery.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX + 1,""));
        System.out.println("Device Inquiry Completed. ");
    }

}