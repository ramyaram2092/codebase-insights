/**
 * After removing the ViewFactory I found these tests to be meaningless.
 * All they did was test the creation successfully worked.
 * In retrospect those tests are pretty useless b/c it's not testing our logic really.
 * At that point its just testing JavaFX creation which we do not need to test.
 */


//package intellij_extension.views;
//
//import intellij_extension.Constants;
//import intellij_extension.views.CommitDetailsPane;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Node;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.VBox;
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
//public class CommitDetailsPaneTest {
//
//    private static JFXPanel fxPanel;
//    private static CommitDetailsPane cdp;
//    private static VBox cdpParent;
//
//    @BeforeAll
//    public static void setUpBeforeClass() {
//        // Forces the JavaFX thread to start
//        // Without tests will fail
//        fxPanel = new JFXPanel();
//        // Create the main Pane
//        cdp = new CommitDetailsPane();
//        cdpParent = (VBox)cdp.getNode();
//    }
//
//    @AfterAll
//    public static void setUpAfterClass() {
//        // Flush the view factory?
//    }
//
//    @Test
//    public void constructor_BannerVBoxSuccessfullyCreated() {
//        // Assert banner object was created
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof VBox);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerHeaderTextSuccessfullyCreated() {
//        // Grab banner VBox
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        VBox header = (VBox) view.get(0);
//
//        // Assert header text
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.CD_HEADER_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerDescriptionTextSuccessfullyCreated() {
//        // Grab banner VBox
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        VBox header = (VBox) view.get(0);
//
//        // Assert commit detail text object
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.CD_DESCRIPTION_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerAuthorTextSuccessfullyCreated() {
//        // Grab banner VBox
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        VBox header = (VBox) view.get(0);
//
//        // Assert commit detail text object
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.CD_AUTHOR_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerDateTextSuccessfullyCreated() {
//        // Grab banner VBox
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        VBox header = (VBox) view.get(0);
//
//        // Assert commit detail text object
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.CD_DATE_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_BannerHashTextSuccessfullyCreated() {
//        // Grab banner VBox
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_BANNER_ID).collect(Collectors.toList());
//        VBox header = (VBox) view.get(0);
//
//        // Assert commit detail text object
//        view = header.getChildren().stream().filter(node -> node.getId() == Constants.CD_HASH_TEXT_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof Text);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_FileListContainerSuccessfullyCreated() {
//        // Assert File Scroll Pane
//        List<Node> view = cdpParent.getChildren().stream().filter(node -> node.getId() == Constants.CD_FILE_LIST_CONTAINER_ID).collect(Collectors.toList());
//        assertNotNull(view);
//        assertTrue(view.get(0) instanceof ScrollPane);
//        assertEquals(1, view.size());
//    }
//
//    @Test
//    public void constructor_FileListVBoxSuccessfullyCreated() {
//        // Grab details ScrollPane
//        List<Node> view = cdp.getChildren().stream().filter(node -> node.getId() == Constants.CD_FILE_LIST_CONTAINER_ID).collect(Collectors.toList());
//        ScrollPane fileListContainer = (ScrollPane) view.get(0);
//
//        // Assert VBox inside ScrollPane
//        Node content = fileListContainer.getContent();
//        assertNotNull(content);
//        assertTrue(content instanceof VBox);
//        assertTrue(content.getId() == Constants.CD_FILE_LIST_ID);
//    }
//
//    @Test
//    public void constructor_FileListTextsSuccessfullyCreated() {
//        // Grab File List ScrollPane (Container)
//        List<Node> view = cdp.getChildren().stream().filter(node -> node.getId() == Constants.CD_FILE_LIST_CONTAINER_ID).collect(Collectors.toList());
//        ScrollPane fileListContainer = (ScrollPane) view.get(0);
//        // Grab VBox inside ScrollPane
//        Node content = fileListContainer.getContent();
//
//        // Assert Test Data created (36 entries)
//        assertEquals(36, ((VBox) content).getChildren().size());
//    }
//}