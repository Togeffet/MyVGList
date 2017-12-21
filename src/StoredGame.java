
public class StoredGame { // Gives a way to be able to store games along with a rank (for local lists)
	private int rank;
	private String name;
	
	public StoredGame(String name) {
		this.name = name;
		this.rank = -1;
	}
	
	public StoredGame(String name, int rank) {
		this.name = name;
		this.rank = rank;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRank() {
		return rank;
	}
	
}
