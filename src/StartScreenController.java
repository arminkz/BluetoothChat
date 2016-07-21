import javafx.fxml.FXML;

/**
 * Created by Armin on 7/18/2016.
 */
public class StartScreenController implements ControlledScreen {

    ScreensController myScreenController;
    Main myLogicalParent;

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }


    @FXML
    protected void initServer(){
        myLogicalParent.initServer();
    }

    @FXML
    protected void initClient(){
        myLogicalParent.initClient();
    }


}
