

public class Game {

	private String name;
	private String description;
	private String boxArt; // TODO COME DO THIS
	private String genre;
	private String developer;
	private int id;
	
	public Game() {
		this("DEFAULT_NAME", "DEFAULT_DESC", "DEFAULT_GENRE", 0);
	}
	
	public Game(String name) {
		this(name, "DEFAULT_DESC", "DEFAULT_GENRE", 0);
	}
	
	public Game(String name, String description) {
		this(name, description, "DEFAULT_GENRE", 0);
	}
	
	
	public Game(String name, String description, String genre) {
		Game game = new Game(name, description, genre, 0);
	}
	
	public Game(String name, String description, String genre, int id) {
		this.name = name;
		this.description = description;
		this.genre = genre;
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	
	public String getGenre() {
		return this.genre;
	}
	
	public int getID() {
		return this.id;
	}
}
