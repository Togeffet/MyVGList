
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.PriorityQueue;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class MainApp extends Application {
	static NodeList gamesList;
	static Vector<Game> searchResults = new Vector<Game>(100);
	static BinaryNode<Game> root;
	static BinaryNode<Game> root2;
	static Account user;
	
	static ObservableList searchList;
	static boolean searchResultsDisplayed = false;
	static HBox accountMainHBox;
	static HBox mainCreateHBox;
	static VBox mainGameVBox;
	static TextField searchBar;
	static ObservableList anchorList;
	static ScrollPane mainScroll;
	static VBox queueQueue;
	static ObservableList<Node> queueList;
	static VBox searchScreen;
	static String lastAddedGame;
	static Label addGameLink;

	@Override
	public void start(Stage primaryStage) {
		AnchorPane anchor = new AnchorPane();
		anchorList = anchor.getChildren();
		
		Scene homePage = new Scene(anchor, 1292, 720);
		
		
		homePage.getStylesheets().add("style.css");
		
		primaryStage.setTitle("MyVGList");
		
		GridPane navBar = new GridPane();
		navBar.setPrefSize(1292, 100);
		navBar.getStyleClass().add("navBar");
		
		
		
		mainScroll = new ScrollPane();
		mainScroll.setMinSize(1292, 620);
		mainScroll.setMaxSize(1292, 620);
		mainScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		mainScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		ScrollPane searchScroll = new ScrollPane();
		searchScroll.setMinSize(1292, 620);
		searchScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		searchScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		queueQueue = new VBox();
		queueQueue.setPrefSize(1280, 620);
		queueList = queueQueue.getChildren();
		
		HBox searchQueue = createQueue(); 
		searchList = searchQueue.getChildren();
		
		ScrollPane search = createQueueScrollPaneFor(searchQueue);
		SplitPane searchQueueFull = createLabeledContainer(search, "Results", 1280);
		
		searchScreen = new VBox();
		
		searchBar = new TextField();
		
		
		
		searchBar.setPromptText("Search...");
		
		addGameLink = new Label();
		addGameLink.setText("This game isn't in our database. Click here to change that!");
		addGameLink.setMinWidth(1280);
		addGameLink.getStyleClass().add("addGameText");
		addGameLink.setAlignment(Pos.BASELINE_CENTER);
		addGameLink.setOnMouseEntered(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.HAND); //Change cursor to hand
		    }
		});
		
		addGameLink.setOnMouseExited(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.DEFAULT); //Change cursor to default
		    }
		});
		
		addGameLink.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			showAddGameScreen(primaryStage);
            
            event.consume();
        });
		
		searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
			searchList.clear();
			
			if (!newValue.equals("")) {
				if (!searchResultsDisplayed) { // If the stage isn't set to show search results yet
					showHomePage();
					if (!searchScreen.getChildren().contains(searchQueueFull))
						searchScreen.getChildren().add(searchQueueFull);
					mainScroll.setContent(searchScreen);
					searchResultsDisplayed = true;
					
				} else {
					search(newValue, root);
					for(int i = 0; i < searchResults.size(); i++) {
						
						print(searchResults.get(i), searchList);
					}
					
					if (searchResults.size() == 0) {
						if (!searchScreen.getChildren().contains(addGameLink))
							searchScreen.getChildren().add(addGameLink);
					} else {
						if (searchScreen.getChildren().contains(addGameLink))
							searchScreen.getChildren().remove(addGameLink);
					}
					
					//print(search(newValue, root), searchList);
				}
			} else {
				mainScroll.setContent(queueQueue);
				//searchResults.clear();
				searchList.clear();
				searchResultsDisplayed = false;
				
			}
		
		});
		
		Label logo = new Label();
		logo.setMinHeight(100);
		logo.setText("MyVGList");
		logo.getStyleClass().add("logo");
		logo.setPadding(new Insets(0, 0, 0, 10));
		logo.setAlignment(Pos.CENTER_LEFT);
		logo.setOnMouseEntered(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.HAND); //Change cursor to hand
		    }
		});
		
		logo.setOnMouseExited(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
		    }
		});
		
		Pane accountPane = new Pane();
		Image accountImg = new Image("file:assets/account_circle.png");
		
        ImageView accountView = new ImageView(accountImg);
        accountView.setPreserveRatio(true);
        
        accountView.setFitWidth(55);
        accountView.setFitHeight(55);
        
        accountPane.setId("accountImg");
        accountPane.setMinSize(50, 50);
        accountPane.setMaxSize(50, 50);
        
        accountPane.setOnMouseEntered(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.HAND); //Change cursor to hand
		    }
		});
		
        accountPane.setOnMouseExited(new EventHandler() {
		    public void handle(Event event) {
		        homePage.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
		    }
		});
        
        accountPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        	showAccountPage(anchorList, mainScroll);
        });
        
        logo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        	searchBar.clear();
        	showHomePage();
        });
        
        accountView.setClip(new Rectangle(150, 170));
        accountPane.getChildren().add(accountView);
        
        GridPane.setConstraints(accountPane, 2, 0);
        GridPane.setHalignment(accountPane, HPos.CENTER);
		
		navBar.getColumnConstraints().add(new ColumnConstraints(900));
		navBar.getColumnConstraints().add(new ColumnConstraints(292));
		navBar.getColumnConstraints().add(new ColumnConstraints(110));
		
		navBar.add(logo, 0, 0);
		navBar.add(searchBar, 1, 0);
		navBar.add(accountPane, 2, 0);
		
		
		GridPane.setHalignment(searchBar, HPos.RIGHT);
		
		//mainScroll.setContent(popular);
		mainScroll.setContent(queueQueue);
		anchorList.add(navBar);
		anchorList.add(mainScroll);
		
		
		createRandomQueues();
		
		AnchorPane.setTopAnchor(mainScroll, 100.0);
		
		primaryStage.setScene(homePage);
		//primaryStage.setScene(accountPage);
		primaryStage.show();
	}
	
	public static void refreshSearch() {
		search(lastAddedGame, root);
		for(int i = 0; i < searchResults.size(); i++) {
			
			print(searchResults.get(i), searchList);
		}
		
		if (searchResults.size() == 0) {
			if (!searchScreen.getChildren().contains(addGameLink))
				searchScreen.getChildren().add(addGameLink);
		} else {
			if (searchScreen.getChildren().contains(addGameLink))
				searchScreen.getChildren().remove(addGameLink);
		}
	}
	
	public static SplitPane createEntireQueueFor(String name, String searchTerm, int width) {
		ObservableList list;
		
		HBox queue = createQueue(); 
		
		list = queue.getChildren();
		
		ScrollPane scroll = createQueueScrollPaneFor(queue);
		
		SplitPane queueFull = createLabeledContainer(scroll, name, width);
		
		print(search(searchTerm, root), list);
		
		return queueFull;
	}
	
	
	public static void showAddGameScreen(Stage primaryStage) {
		mainScroll.setContent(null);
		mainCreateHBox = new HBox(50);
		mainCreateHBox.setAlignment(Pos.TOP_CENTER);
		mainCreateHBox.setMinSize(1292, 620);
		mainCreateHBox.setMaxSize(1292, 620);
		mainCreateHBox.setLayoutY(120);
		
		Pane art = new Pane();
		Image addGameImg = new Image("file:assets/addGame.png");
		ImageView addGameImgView = new ImageView(addGameImg);
		addGameImgView.setClip(new Rectangle(120, 136));
		art.getChildren().add(addGameImgView);
		art.setMaxSize(120, 136);
        art.getStyleClass().add("topGame");
        
        art.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        	FileChooser fileChooser = new FileChooser();
        	ExtensionFilter filter = new ExtensionFilter("jpg", "png", "gif");
            fileChooser.setSelectedExtensionFilter(filter);
            
        	File source = fileChooser.showOpenDialog(primaryStage);
            
            if(source != null) {
                String fName = source.getName();
                String extension = fName.substring(fName.lastIndexOf("."), fName.length());
                
                
                File dest = new File("assets/art_" + gamesList.getLength() + extension);
                try {
					Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);
					Image thumbNail = new Image("file:" + "assets/art_" + gamesList.getLength() + extension);
					addGameImgView.setClip(new Rectangle(120, thumbNail.getHeight()));
					addGameImgView.setPreserveRatio(true);
					
					double ratio = 120 / addGameImg.getWidth();
					
					double newHeight = (addGameImg.getHeight() * ratio);
					
					art.setMaxHeight(newHeight);
					
					addGameImgView.setFitWidth(120);
					
					
					addGameImgView.setImage(thumbNail);
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            
        	event.consume();
        });
		
		
		VBox gameAttributesVBox = new VBox(10);
		gameAttributesVBox.setMinSize(800, 620);
		gameAttributesVBox.setMaxSize(800, 620);
		
		TextField gameName = new TextField();
		gameName.setPromptText("Name");
		gameName.setText(searchBar.getText());
		gameName.setMaxWidth(200);
		TextArea gameDesc = new TextArea();
		gameDesc.setPromptText("Description");
		gameDesc.setMaxSize(350, 80);
		
		ObservableList<String> gameGenres = 
			    FXCollections.observableArrayList(
			        "Action", "Adventure", "Fighting", "Platformer",
			        "Puzzle", "Racing", "RPG", "Shooter", "Simulation",
			        "Sports", "Strategy"
			    );
		ComboBox gameGenre = new ComboBox(gameGenres);
		
			
		Button addGameBtn = new Button();
		addGameBtn.setText("Add Game");
		
		addGameBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				createGame(gameName.getText(), gameDesc.getText(), (String) gameGenre.getValue());
				showHomePage();
				mainScroll.setContent(searchScreen);
				searchResultsDisplayed = true;
				lastAddedGame = gameName.getText();
				refreshSearch();
			} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
			
			event.consume();
		});
		
		gameAttributesVBox.getChildren().addAll(gameName, gameDesc, gameGenre, addGameBtn);
		mainCreateHBox.getChildren().addAll(art, gameAttributesVBox);
		
		anchorList.remove(mainScroll);
		
		//anchor.add(mainScroll);
		anchorList.remove(accountMainHBox);
		anchorList.add(mainCreateHBox);
	}
	
	
	public static void showGameScreen(int gameID) {
		mainGameVBox = new VBox(0);
		mainGameVBox.setPadding(new Insets(40, 0, 0, 0));
		
		
		HBox mainCreateHBox = new HBox(50);
		mainCreateHBox.setAlignment(Pos.TOP_CENTER);
		mainCreateHBox.setMinSize(1280, 400);
		mainCreateHBox.setPrefSize(1280, 400);
		
		mainCreateHBox.setMaxWidth(1280);
		
		
		Game game = getGameAt(gameID);
		
		Pane art = new Pane();
		Image addGameImg = new Image("file:assets/art_" + gameID + ".jpg");
		
		ImageView addGameImgView = new ImageView(addGameImg);
		
		double ratio = 240 / addGameImg.getWidth();
		
		double newHeight = (addGameImg.getHeight() * ratio);
		
		if (newHeight > 347) {
			mainCreateHBox.setMinSize(1280, newHeight + 75);
		}
		
		art.setMaxHeight(newHeight);
		addGameImgView.setPreserveRatio(true);
		art.setMaxWidth(240);
		
		addGameImgView.setClip(new Rectangle(240, newHeight));
		art.getChildren().add(addGameImgView);
		
		addGameImgView.setFitWidth(240);
		
		
        art.getStyleClass().add("topGame");
        
        
        HBox bottomBar = new HBox();
        bottomBar.setMinSize(240,53);
    	bottomBar.setMaxSize(240,53);
    	
    	StackPane heart = new StackPane();
    	heart.setPrefSize(60, 40);
    	
    	
    	if (user.isInFavorites(game.getName(), "Favorites")) 
    		heart.getStyleClass().add("heartClicked");
    	 else 
    		heart.getStyleClass().add("heart");
    
    	heart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "Favorites")) { // If it isn't in the user's favorites already
            	try {
					Account.addToList(game.getName(), "Favorites");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	heart.getStyleClass().remove("heart");
            	heart.getStyleClass().add("heartClicked");
            } else {
            	try {
					Account.removeFromList(game.getName(), "Favorites");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	heart.getStyleClass().remove("heartClicked");
            	heart.getStyleClass().add("heart");
            }
            
            event.consume();
        });
    	
    	
    	StackPane check = new StackPane();
    	check.setPrefSize(60, 40);
    	if (user.isInFavorites(game.getName(), "Beaten")) {
    		check.getStyleClass().add("checkClicked");
    	} else {
    		check.getStyleClass().add("check");
		}
    	
    	check.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
           if (!user.isInFavorites(game.getName(), "Beaten")) { // If it's been added to user's favs already
            	try {
					Account.addToList(game.getName(), "Beaten");
					Account.removeFromList(game.getName(), "BackLog");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	check.getStyleClass().remove("check");
            	check.getStyleClass().add("checkClicked");
            } else {
            	try {
					Account.removeFromList(game.getName(), "Beaten");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					//e.printStackTrace();
				}
            	check.getStyleClass().remove("checkClicked");
            	check.getStyleClass().add("check");
            }
           
            event.consume();
        });
    	
    	StackPane future = new StackPane();
    	future.setPrefSize(60, 40);
    	if (user.isInFavorites(game.getName(), "BackLog")) {
    		future.getStyleClass().add("backLogClicked");
    	} else {
    		future.getStyleClass().add("backLog");
		}
    	
    	future.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "BackLog")) { // If it's been added to user's favs already
             	try {
 					Account.addToList(game.getName(), "BackLog");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					e.printStackTrace();
 				}
             	future.getStyleClass().remove("backLog");
            	future.getStyleClass().add("backLogClicked");
             } else {
             	try {
 					Account.removeFromList(game.getName(), "BackLog");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					//e.printStackTrace();
 				}
             	future.getStyleClass().remove("backLogClicked");
            	future.getStyleClass().add("backLog");
             }
            
             event.consume();
         });
    	
    	StackPane dots = new StackPane();
    	dots.setPrefSize(60, 40);
    	
    	if (user.isInFavorites(game.getName(), "Top4")) {
    		dots.getStyleClass().add("top4Clicked");
    	} else {
    		dots.getStyleClass().add("top4");
		}
    	
    	dots.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "Top4")) { // If it's been added to user's favs already
             	try {
 					Account.addToList(game.getName(), "Top4");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					e.printStackTrace();
 				}
             	dots.getStyleClass().remove("top4");
            	dots.getStyleClass().add("top4Clicked");
             } else {
             	try {
 					Account.removeFromList(game.getName(), "Top4");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					//e.printStackTrace();
 				}
             	dots.getStyleClass().remove("top4Clicked");
            	dots.getStyleClass().add("top4");
             }
            
             event.consume();
         });
    	
    	
    	bottomBar.getChildren().add(heart);
    	bottomBar.getChildren().add(check);
    	bottomBar.getChildren().add(future);
    	bottomBar.getChildren().add(dots);
    	//GridPane.setConstraints(bottomBar, 0, 2);
    	VBox artAndBar = new VBox(0);
    	artAndBar.setMinHeight(600);
    	
    	artAndBar.getChildren().addAll(art, bottomBar);
    	
    	
		VBox gameAttributesVBox = new VBox(15);
		gameAttributesVBox.setMinSize(800, 320);
		gameAttributesVBox.setMaxSize(800, 320);
		
		Label gameName = new Label();
		gameName.setText(game.getName());
		gameName.setMaxWidth(800);
		gameName.getStyleClass().add("gameName");
		
		Text description = new Text(game.getDescription());
		description.getStyleClass().add("gameDesc");
		description.setFont(Font.font("Roboto Light", 18));
		
		TextFlow gameDesc = new TextFlow(description);
		gameDesc.setId("gameDesc");
		gameDesc.setMaxSize(450, 500);
		gameDesc.getStyleClass().add("gameDesc");
		
		
		Label gameGenre = new Label();
		gameGenre.setText("Genre: " + game.getGenre());
		gameGenre.setMaxWidth(200);
		gameGenre.getStyleClass().add("gameGenre");
		
		
		gameAttributesVBox.getChildren().addAll(gameName, gameDesc, gameGenre);
		
		mainCreateHBox.getChildren().addAll(artAndBar, gameAttributesVBox);
		
		mainGameVBox.getChildren().add(mainCreateHBox);
		
		ObservableList list;
		
		HBox queue = createQueue(); 
		
		list = queue.getChildren();
		
		ScrollPane scroll = createQueueScrollPaneFor(queue);
		
		SplitPane queueFull = createLabeledContainer(scroll, "Similar games", 1280);
		Vector<Game> similarGamesList = search(game.getGenre(), root);
		
		for(int i = 0; i < similarGamesList.size(); i++) {
			if (gameID == similarGamesList.elementAt(i).getID()) {
				similarGamesList.remove(i);
			}
		}
		
		print(similarGamesList, list);
		
		mainGameVBox.getChildren().add(queueFull);
		
		
		mainScroll.setContent(mainGameVBox);
		
		if (!anchorList.contains(mainScroll))
			anchorList.add(mainScroll);
	}
	
	public static void createRandomQueues() {
		String random = "" + Math.random() * 50 + 1;
		random = random.substring(0, 7);
		
		//System.out.println(random);
		if (random.contains("4")) 
			queueList.add(createEntireQueueFor("Action", "Action", 1280));
		else 
			queueList.add(createEntireQueueFor("Adventure", "Adventure", 1280));
		
		
		if (random.contains("5"))
			queueList.add(createEntireQueueFor("RPGs", "RPG", 1280));
		 else 
			queueList.add(createEntireQueueFor("Shooter", "Shooter", 1280));
		
		
		if (random.contains("6"))
			queueList.add(createEntireQueueFor("Puzzle", "Puzzle", 1280));
		else
			queueList.add(createEntireQueueFor("Platformers", "Platformer", 1280));
		
		
		if (random.contains("7"))
			queueList.add(createEntireQueueFor("Racing", "Racing", 1280));
		else
			queueList.add(createEntireQueueFor("Fighting", "Fighting", 1280));
		
		if (random.contains("8"))
			queueList.add(createEntireQueueFor("Simulation", "Simulation", 1280));
		else
			queueList.add(createEntireQueueFor("Sports", "Sports", 1280));
		
		if (random.contains("9")) 
			queueList.add(createEntireQueueFor("Strategy", "Strategy", 1280));
		
	}
	
	
	public static void showAccountPage(ObservableList<Region> anchor, ScrollPane mainScroll) {
		
		Account.loadLists();
		searchResultsDisplayed = false;
		searchBar.clear();
		
		accountMainHBox = new HBox();
		VBox accountVBox = new VBox();
		ScrollPane queuesScrollPane = new ScrollPane();
		VBox queuesVBox = new VBox();
		
		accountMainHBox.setMinSize(1292, 620);
		accountMainHBox.setMaxSize(1292, 620);
		accountMainHBox.setLayoutY(100);
		
		accountVBox.setMinSize(280, 620);
		accountVBox.setMaxSize(280, 620);
		accountVBox.getStyleClass().add("accountLeft");
		
		// Account left side
		StackPane accountImgHolder = new StackPane();
		Image accountImg = new Image("file:assets/account_circle.png");
		accountImgHolder.setMinSize(150, 150);
		ImageView accountImgView = new ImageView();
		accountImgView.setImage(accountImg);
		accountImgView.setFitHeight(150);
		accountImgView.setFitWidth(150);
		accountImgHolder.getChildren().add(accountImgView);
		
		String[] userStats = user.getStats();
		
		Label accountName = new Label();
		accountName.setText(userStats[0]);
		accountName.setMinWidth(280);
		accountName.setMaxWidth(280);
		accountName.setAlignment(Pos.CENTER);
		accountName.getStyleClass().addAll("accountName");
		
		Label accountStats = new Label();
		accountStats.setMinSize(280, 100);
		accountStats.getStyleClass().add("accountStats");
		accountStats.setText(" Games Favorited: " + userStats[1] + "\n Games Beaten: " + userStats[2] + "\n Games on Back Log: " + userStats[3]);
		
		PriorityQueue<StoredGame> top4Games = user.getTop4();
		
		Label top4Label = new Label();
		top4Label.getStyleClass().add("topGamesTitle");
		top4Label.setText("Top 4 Games");
		top4Label.setMinSize(280, 30);
		top4Label.setMaxSize(280, 30);
		top4Label.setAlignment(Pos.TOP_CENTER);
		
		HBox top2 = new HBox(10);
		top2.setMinWidth(280);
		top2.setAlignment(Pos.CENTER);
		
		for (int i = 0; i < 2; i++) {
			Pane art = new Pane();
			art.setMaxSize(120, 136);
			Image bottomGame;
			ImageView bottomGameView = new ImageView();
			
			if (top4Games.size() > 0) {
				int gameID = getFirstResultFor(top4Games.remove().getName()).getID();
				
				bottomGame = new Image("file:assets/art_" + gameID + ".jpg");
				
				 if (bottomGame.getWidth()/bottomGame.getHeight() == 1) {
					 	bottomGameView.setFitHeight(136);
			        	
			     } else {
			        	bottomGameView.setFitWidth(120);
			     }
				 
				 art.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			    		showGameScreen(gameID);
			    	});
				
			} else {
				bottomGame = new Image("file:assets/addGame.png");
			}
			
			
			bottomGameView.setPreserveRatio(true);
			bottomGameView.setImage(bottomGame);
			bottomGameView.setClip(new Rectangle(120, 136));
	        art.getChildren().add(bottomGameView);
	        art.getStyleClass().add("topGame");
	        
	        top2.getChildren().add(art);
			
		}
		
		
		HBox bottom2 = new HBox(10);
		
		bottom2.setMinSize(280, 160);
		bottom2.setAlignment(Pos.CENTER);
		
		for(int i = 0; i < 2; i++) {
			Pane art = new Pane();
			art.setMaxSize(120, 136);
			Image bottomGame;
			ImageView bottomGameView = new ImageView();
			
			if (top4Games.size() > 0) {
				int gameID = getFirstResultFor(top4Games.remove().getName()).getID();
				
				bottomGame = new Image("file:assets/art_" + gameID + ".jpg");
				
				 if (bottomGame.getWidth()/bottomGame.getHeight() == 1) {
					 	bottomGameView.setFitHeight(136);
			        	
			     } else {
			        	bottomGameView.setFitWidth(120);
			     }
				 
				 art.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			    		showGameScreen(gameID);
			    	});
				
			} else {
				
				bottomGame = new Image("file:assets/addGame.png");
				
			}
			
			
			bottomGameView.setPreserveRatio(true);
			bottomGameView.setImage(bottomGame);
			bottomGameView.setClip(new Rectangle(120, 136));
	        art.getChildren().add(bottomGameView);
	        art.getStyleClass().add("topGame");
			
	        bottom2.getChildren().add(art);
			
		}
		
		accountVBox.getChildren().addAll(accountImgHolder, accountName, accountStats, top4Label, top2, bottom2);
		
		
		queuesScrollPane.setMinSize(1012, 620);
		queuesScrollPane.setMaxSize(1012, 620);
		queuesScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		queuesScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		queuesVBox.setMinSize(1000, 620);
		queuesVBox.setMaxWidth(1000);
		
		
		queuesScrollPane.setContent(queuesVBox);
		
		accountMainHBox.getChildren().add(accountVBox);
		accountMainHBox.getChildren().add(queuesScrollPane);
		
		HBox rpgQueue = createQueue(); 
		
		ObservableList favoritesObsList = rpgQueue.getChildren();
		
		ScrollPane rpgs = createQueueScrollPaneFor(rpgQueue);
		
		SplitPane rpgQueueFull = createLabeledContainer(rpgs, "Favorites", 1000);
		
		while (!user.getFavorites().isEmpty()) {
        	print(getFirstResultFor(user.getFavorites().remove().getName()), favoritesObsList);
        }
		
		queuesVBox.getChildren().add(rpgQueueFull);
		
		HBox beatenQueue = createQueue(); 
		
		ObservableList beatenObsList = beatenQueue.getChildren();
		
		ScrollPane beaten = createQueueScrollPaneFor(beatenQueue);
		
		SplitPane beatenQueueFull = createLabeledContainer(beaten, "Beaten", 1000);
		
		while (!user.getBeaten().isEmpty()) {
        	print(getFirstResultFor(user.getBeaten().remove().getName()), beatenObsList);
        }
		
		queuesVBox.getChildren().add(beatenQueueFull);
		
		HBox backLogQueue = createQueue(); 
		
		ObservableList backLogObsList = backLogQueue.getChildren();
		
		ScrollPane backLog = createQueueScrollPaneFor(backLogQueue);
		
		SplitPane backLogQueueFull = createLabeledContainer(backLog, "BackLog", 1000);
		
		while (!user.getBackLog().isEmpty()) {
        	print(getFirstResultFor(user.getBackLog().remove().getName()), backLogObsList);
        }
		
		queuesVBox.getChildren().add(backLogQueueFull);
		
		anchor.remove(mainCreateHBox);
		anchor.remove(mainScroll);
		
		anchor.add(accountMainHBox);
		
	}
	
	public static void showHomePage() { 
		//user.loadFavorites();
		Account.loadLists();
		searchResultsDisplayed = false;
		queueList.clear();
		createRandomQueues();
		
		
		anchorList.remove(mainCreateHBox);
		anchorList.remove(accountMainHBox);
		mainScroll.setContent(queueQueue);
		
		if (!anchorList.contains(mainScroll))
			anchorList.add(mainScroll);
	}
	
	public static SplitPane createLabeledContainer(ScrollPane queue, String name, int width) {
		SplitPane queueAndLabel = new SplitPane();
		queueAndLabel.setOrientation(Orientation.VERTICAL);
		queueAndLabel.setMinSize(100, 350);
		
		Label title = new Label();
		title.setText(name);
    	title.getStyleClass().add("queueTitle");
    	title.setMinSize(width, 33);
    	title.setMaxSize(width, 33);
    	title.setAlignment(Pos.BASELINE_LEFT);
    	//title.setWrapText(true);
    	title.setPadding(new Insets(5, 0, 0, 10));
    	title.setPrefSize(150, 40);
    	
    	queueAndLabel.getItems().addAll(title, queue);
    	
    	return queueAndLabel;
	}
	public static HBox createQueue() {
		HBox gameQueue = new HBox(20); // Creates the gamequeue with spacing of 5
		gameQueue.autosize();
		gameQueue.setMinHeight(250);
		gameQueue.setMaxHeight(280);
		
		gameQueue.setAlignment(Pos.CENTER_LEFT);
		gameQueue.setPadding(new Insets(15, 5, 25, 5));
		
		return gameQueue;
	}
	
	public static ScrollPane createQueueScrollPaneFor(HBox hbox) {
		ScrollPane scroll = new ScrollPane();
		scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setMinHeight(300);
		scroll.setContent(hbox);
		
		return scroll;
	}

	public static void main(String[] args) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		loadGames();
	  
		user = new Account("Frankie Fanelli");
	  	
		
		launch(args);
	}
	
	// Load games into the observable list, and then into the tree
	public static void loadGames() {
		try {
			File fXmlFile = new File("games.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			gamesList = doc.getElementsByTagName("game");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	  	root = new BinaryNode<Game>(getGameAt(0));
	  	
		for (int i = 1; i < gamesList.getLength(); i++) {
			insert(getGameAt(i), root); // Load the games into a tree
		}
	}
	
	
	public static GridPane createGameIcon(Game game) {
    	GridPane icon = new GridPane();
    	icon.setMinSize(150, 250);
        icon.setMaxSize(150, 250);
        icon.getStyleClass().add("gameIcon");
        icon.getColumnConstraints().add(new ColumnConstraints(150)); // column 0 is 150 wide
        
        
        Pane art = new Pane();
        Label title = new Label();
        HBox bottomBar = new HBox();
        
        Image gameArt = new Image("file:assets/art_"+ game.getID() +".jpg");
        ImageView imageView = new ImageView(gameArt);
        imageView.setPreserveRatio(true);
        
        if (gameArt.getWidth()/gameArt.getHeight() >= 1) {
        	imageView.setFitHeight(170);
        	
        } else {
        	imageView.setFitWidth(150);
        }
        
        imageView.setClip(new Rectangle(150, 170));
        
        
        
        art.getChildren().add(imageView);
        
        //art.setId("art_" + gameID);
        art.getStyleClass().add("gameArt");
        art.setMinSize(150, 170);
        art.setMaxSize(150, 170);
        GridPane.setConstraints(art, 0, 0);
        
    	icon.getChildren().add(art);
    	
    	title.setText(game.getName());
    	title.getStyleClass().add("gameTitle");
    	title.setMinHeight(40);
    	title.setPrefSize(150, 40);
    	title.setAlignment(Pos.CENTER_LEFT);
    	//title.setWrapText(true);
    	title.setPadding(new Insets(0, 4, 0, 4));
    	
    	GridPane.setConstraints(title, 0, 1);
    	icon.getChildren().add(title);
    	
    	art.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
    		showGameScreen(game.getID());
    	});
    	
    	
    	bottomBar.setMinSize(150,33);
    	bottomBar.setMaxSize(150,33);
    	
    	StackPane heart = new StackPane();
    	heart.setPrefSize(37, 33);
    	
    	if (user.isInFavorites(game.getName(), "Favorites")) {
    		heart.getStyleClass().add("heartClicked");
    	} else {
    		heart.getStyleClass().add("heart");
		}
    	
    	heart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "Favorites")) { // If it isn't in the user's favorites already
            	try {
					Account.addToList(game.getName(), "Favorites");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	heart.getStyleClass().remove("heart");
            	heart.getStyleClass().add("heartClicked");
            } else {
            	try {
					Account.removeFromList(game.getName(), "Favorites");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	heart.getStyleClass().remove("heartClicked");
            	heart.getStyleClass().add("heart");
            }
            
            event.consume();
        });
    	
    	
    	StackPane check = new StackPane();
    	check.setPrefSize(37, 33);
    	if (user.isInFavorites(game.getName(), "Beaten")) {
    		check.getStyleClass().add("checkClicked");
    	} else {
    		check.getStyleClass().add("check");
		}
    	
    	check.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
           if (!user.isInFavorites(game.getName(), "Beaten")) { // If it's been added to user's favs already
            	try {
					Account.addToList(game.getName(), "Beaten");
					Account.removeFromList(game.getName(), "BackLog");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
            	check.getStyleClass().remove("check");
            	check.getStyleClass().add("checkClicked");
            } else {
            	try {
					Account.removeFromList(game.getName(), "Beaten");
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
					//e.printStackTrace();
				}
            	check.getStyleClass().remove("checkClicked");
            	check.getStyleClass().add("check");
            }
           
            event.consume();
        });
    	
    	StackPane future = new StackPane();
    	future.setPrefSize(38, 33);
    	if (user.isInFavorites(game.getName(), "BackLog")) {
    		future.getStyleClass().add("backLogClicked");
    	} else {
    		future.getStyleClass().add("backLog");
		}
    	
    	future.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "BackLog")) { // If it's been added to user's favs already
             	try {
 					Account.addToList(game.getName(), "BackLog");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					e.printStackTrace();
 				}
             	future.getStyleClass().remove("backLog");
            	future.getStyleClass().add("backLogClicked");
             } else {
             	try {
 					Account.removeFromList(game.getName(), "BackLog");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					//e.printStackTrace();
 				}
             	future.getStyleClass().remove("backLogClicked");
            	future.getStyleClass().add("backLog");
             }
            
             event.consume();
         });
    	
    	StackPane dots = new StackPane();
    	dots.setPrefSize(38, 33);
    	
    	if (user.isInFavorites(game.getName(), "Top4")) {
    		dots.getStyleClass().add("top4Clicked");
    	} else {
    		dots.getStyleClass().add("top4");
		}
    	
    	dots.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            
            if (!user.isInFavorites(game.getName(), "Top4")) { // If it's been added to user's favs already
             	try {
 					Account.addToList(game.getName(), "Top4");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					e.printStackTrace();
 				}
             	dots.getStyleClass().remove("top4");
            	dots.getStyleClass().add("top4Clicked");
             } else {
             	try {
 					Account.removeFromList(game.getName(), "Top4");
 				} catch (TransformerException | ParserConfigurationException | SAXException | IOException e) {
 					//e.printStackTrace();
 				}
             	dots.getStyleClass().remove("top4Clicked");
            	dots.getStyleClass().add("top4");
             }
            
             event.consume();
         });
    	
    	
    	bottomBar.getChildren().add(heart);
    	bottomBar.getChildren().add(check);
    	bottomBar.getChildren().add(future);
    	bottomBar.getChildren().add(dots);
    	GridPane.setConstraints(bottomBar, 0, 2);
    	icon.getChildren().add(bottomBar);
    	
		return icon;
    }
	
	public static void createGame(String gameName, String gameDesc, String gameGenre) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File("games.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
		Element newGame = doc.createElement("game");

		newGame.setAttribute("id", "" + gamesList.getLength());
		
		Element root = doc.getDocumentElement();
		
		Element name = doc.createElement("name");
		Element desc = doc.createElement("description");
		Element genre = doc.createElement("genre");
		 

         name.appendChild(doc.createTextNode(gameName));
         desc.appendChild(doc.createTextNode(gameDesc));
         genre.appendChild(doc.createTextNode(gameGenre));

         newGame.appendChild(name);
         newGame.appendChild(desc);
         newGame.appendChild(genre);
         
         root.appendChild(newGame);
		
		
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer();
         DOMSource domSource = new DOMSource(doc);
         
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         
         
         StreamResult streamResult = new StreamResult(new File("games.xml"));
         transformer.transform(domSource, streamResult);
         
         loadGames();
	}
	
	// Inserts the game into the tree alphabetically=
	public static BinaryNode<Game> insert(Game game, BinaryNode<Game> root) { 
  		if (root == null) { 
  			root = new BinaryNode<Game>(game);
  		} else if (game.getName().compareTo(((Game) root.getData()).getName()) < 0) { // Comes before the root
  			root.setLeftChild(insert(game, root.getLeftChild()));
  		} else {
  			root.setRightChild(insert(game, root.getRightChild()));
  		}
  		return root;
	}
	
	public static Game getFirstResultFor(String name) {
		if (!search(name, root).isEmpty())
			return search(name, root).get(0);
		else 
			return null;
	}
		
  	public static Game getGameAt(int index) {
		Element game = (Element) gamesList.item(index);
		String title = game.getElementsByTagName("name").item(0).getTextContent();
		String desc = game.getElementsByTagName("description").item(0).getTextContent();
		//int rating = Integer.parseInt(game.getElementsByTagName("rating").item(0).getTextContent());
		String genre = game.getElementsByTagName("genre").item(0).getTextContent();
		int gameID = Integer.parseInt(game.getAttributes().getNamedItem("id").getNodeValue());
		
		
			
		Game result = new Game(title, desc, genre, gameID);
		//System.out.print(result.getID());
		return result;
	}
  	
  	public static Vector<Game> search(String term, BinaryNode<Game> root) {
  		searchResults.clear();
  		return recursiveSearch(term, root);
  	}
  	
	public static Vector<Game> recursiveSearch(String term, BinaryNode<Game> root) {
  		if (root.hasLeftChild()) {
  			recursiveSearch(term, root.getLeftChild());
  		}
			
  			if (((Game) root.getData()).getName().contains(term) || (((Game) root.getData()).getGenre().contains(term))) {
  				if (!searchResults.contains(root.getData())) 
  					searchResults.add((Game) root.getData());
  			}
  		
  		
		
		if (root.hasRightChild()) {
			recursiveSearch(term, root.getRightChild());
  		}
		
		return searchResults;
	}
  	
  	public static Vector<Game> search(int id, BinaryNode<Game> root) {
  		return recursiveSearch(id, root);
  	}
  	
	public static Vector<Game> recursiveSearch(int id, BinaryNode<Game> root) {
  		if (root.hasLeftChild()) {
  			recursiveSearch(id, root.getLeftChild());
  		}
		
  		if ((((Game) root.getData()).getID() == id)) {
  	  		searchResults.add((Game) root.getData());
  		}
  		
		if (root.hasRightChild()) {
			recursiveSearch(id, root.getRightChild());
  		}
		
		return searchResults;
		
	}
  	
	public static void print(BinaryNode<Game> root, ObservableList<GridPane> location) {
  		if (root == null)
  			return;
  		
		if (root.getLeftChild() != null)
			print(root.getLeftChild(), location);
		
		location.add(createGameIcon((Game) root.getData()));
		
		if (root.getRightChild() != null)
			print(root.getRightChild(), location);
		
	}
  	public static void print(Vector<Game> root, ObservableList<GridPane> location) {
		for (int i = 0; i < root.size(); i++) {
			location.add(createGameIcon((Game) root.get(i)));
		}
		
		
	}
  	
  	public static void print(Game gameId, ObservableList<GridPane> location) {
		location.add(createGameIcon(gameId));
		
		
	}
}
