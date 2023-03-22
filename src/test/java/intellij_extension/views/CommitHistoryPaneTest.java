/**
 * After removing the ViewFactory I found these tests to be meaningless.
 * All they did was test the creation successfully worked.
 * In retrospect those tests are pretty useless b/c it's not testing our logic really.
 * At that point its just testing JavaFX creation which we do not need to test.
 */

//package intellij_extension.views;
//
//import intellij_extension.Constants;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Node;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TableView;
//import javafx.scene.layout.HBox;
//import javafx.scene.text.Text;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class CommitHistoryPaneTest {
//
//    private static JFXPanel fxPanel;
//    private static FileHistoryPane chp;
//
//    @BeforeAll
//    public static void setUpBeforeClass() {
//        // Forces the JavaFX thread to start
//        // Without tests will fail
//        fxPanel = new JFXPanel();
//        // Create the main Pane
//        chp = new FileHistoryPane();
//    }
//
//    @AfterAll
//    public static void setUpAfterClass() {
//        // Flush the view factory?
//    }
//
//    @Test
//    public void constructor_BannerHBoxSuccessfullyCreated() {
//        // Assert banner object was created
//        List<Node> view = chp.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BANNER_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof HBox);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerHeaderTextSuccessfullyCreated() {
//        // Grab banner HBox
//        List<Node> view = chp.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BANNER_ID).collect(Collectors.toList());
//        HBox header = (HBox) view.get(0);
//
//        // Assert header text
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.FCH_HEADER_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerComboBoxSuccessfullyCreated() {
//        // Grab banner HBox
//        List<Node> view = chp.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BANNER_ID).collect(Collectors.toList());
//        HBox header = (HBox) view.get(0);
//
//        // Assert combo box
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BRANCH_COMBOBOX_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof ComboBox);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerTableViewSuccessfullyCreated() {
//        // Grab banner HBox
//        List<Node> view = chp.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BANNER_ID).collect(Collectors.toList());
//        HBox header = (HBox) view.get(0);
//
//        // Assert TableView
//        view = chp.getChildren().stream().filter(node -> node.getId() == Constants.FCH_BRANCH_TABLEVIEW_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof TableView);
//        assertEquals(1, view.size());
//    }
//
//    // I haven't figured out how to assert info in table view is correct.
//    // These tests are needed!
//}
