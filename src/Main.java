import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {

    SPPServer server;
    SPPClient client;

    String myName;
    String partnerName;

    BufferedReader in;
    PrintWriter out;

    ScreensController mainContainer;

    public Main(){
        mainContainer = new ScreensController(this);
        mainContainer.loadScreen("ChatScreen","ChatScreen.fxml");
        mainContainer.loadScreen("StartScreen","StartScreen.fxml");
        mainContainer.loadScreen("LoadingScreen","LoadingScreen.fxml");
        mainContainer.loadScreen("ServerListScreen","ServerListScreen.fxml");
        mainContainer.setScreen("StartScreen");

        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            myName = localDevice.getFriendlyName();
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    public void initServer(){
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();

            System.out.println("Address: "+localDevice.getBluetoothAddress());
            System.out.println("Name: "+localDevice.getFriendlyName());

            SPPServer server = new SPPServer();

            ((LoadingScreenController)mainContainer.getScreenController("LoadingScreen")).setText("Waiting for Connection ...");
            mainContainer.setScreen("LoadingScreen");

            server.setOnConnectionSuccessful((ActionEvent e) -> {
                in = server.in;
                out = server.out;
                partnerName = server.partnerName;
                mainContainer.setScreen("ChatScreen");
                (new streamPoller()).start();
            });
            server.start();

        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void initClient(){
        client = new SPPClient();
        client.setOnDeviceDiscovery((ActionEvent e) -> {
            ObservableList<RemoteDeviceInfo> devInfo = FXCollections.observableList(client.getDeviceInfos());
            ((ServerListScreenController)mainContainer.getScreenController("ServerListScreen")).setTableData(devInfo);
            mainContainer.setScreen("ServerListScreen");
        });
        ((LoadingScreenController)mainContainer.getScreenController("LoadingScreen")).setText("Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        client.startDiscovery();

        client.setOnConnectionFailed((ActionEvent e) -> mainContainer.setScreen("ServerListScreen"));
        client.setOnConnectionSuccessful((ActionEvent e) -> {
            in = client.in;
            out = client.out;
            partnerName = client.partnerName;
            mainContainer.setScreen("ChatScreen");
            (new streamPoller()).start();
        });
    }

    public void refresh(){
        ((LoadingScreenController)mainContainer.getScreenController("LoadingScreen")).setText("Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        client.startDiscovery();
    }


    public void connectToDevice(int index){
        ((LoadingScreenController)mainContainer.getScreenController("LoadingScreen")).setText("Connecting ...");
        mainContainer.setScreen("LoadingScreen");
        client.connect(index);
    }

    public void sendMsg(String s){
        System.out.println("must send " + s);
        out.write(s+"\r\n");
        out.flush();
    }

    public void recieveMsg(String s){
        ChatScreenController ch = ((ChatScreenController)mainContainer.getScreenController("ChatScreen"));
        ch.recivedMassage(s);
    }

    class streamPoller extends Thread{

        public void run(){
            boolean isRun = true;
            while(isRun){
                try {
                    if(in != null) {
                        System.out.println("in not null");
                        String s = in.readLine();
                        if (s != null) Platform.runLater(() -> recieveMsg(s));
                        else isRun = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(mainContainer);
        AnchorPane.setTopAnchor(mainContainer,0.0);
        AnchorPane.setBottomAnchor(mainContainer,0.0);
        AnchorPane.setLeftAnchor(mainContainer,0.0);
        AnchorPane.setRightAnchor(mainContainer,0.0);

        Scene scene = new Scene(root,350,600);
        primaryStage.setTitle("Bluetooth Chat v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
