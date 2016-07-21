import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ChatScreenController implements ControlledScreen {

    ScreensController myScreenController;
    Main myLogicalParent;

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }

    @FXML
    private TextArea chatLog;

    @FXML
    private TextArea chatText;

    @FXML
    protected void sendMassage() {
        String msg = chatText.getText();
        chatLog.appendText("Me : " + msg + "\n");
        chatText.setText("");
        myLogicalParent.sendMsg(msg);
    }

    public void recivedMassage(String sender,String msg){
        chatLog.appendText(sender + " : " + msg + "\n");
    }

}
