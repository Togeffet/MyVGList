import java.util.Comparator;

public class GameListComparator implements Comparator<StoredGame>{
	
	@Override
	public int compare(StoredGame game1, StoredGame game2) {
		if (game1.getRank() > game2.getRank()) {
			return 1;
		} else if (game1.getRank() == game2.getRank()) {
			return 0;
		} else {
			return -1;
		}
		
	}
}
