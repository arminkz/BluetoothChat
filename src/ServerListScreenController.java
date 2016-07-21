import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

/**
 * Created by Armin on 7/19/2016.
 */
public class ServerListScreenController  implements ControlledScreen {

    ScreensController myScreenController;
    Main myLogicalParent;

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }

    @FXML
    private TableView serverTable;

    public void setTableData(ObservableList<RemoteDeviceInfo> data){
        serverTable.setItems(data);
    }

    @FXML
    protected void connectToDevice(){
        myLogicalParent.connectToDevice(serverTable.getSelectionModel().getSelectedIndex());
    }

    @FXML
    protected void refresh(){
        myLogicalParent.refresh();
    }

}