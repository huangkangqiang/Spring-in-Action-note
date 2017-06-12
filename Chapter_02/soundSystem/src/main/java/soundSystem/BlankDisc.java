package soundSystem;

public class BlankDisc implements CompactDisc {

	private String title;
	private String artist;

	public BlankDisc(String title, String artist) {
		this.title = title;
		this.artist = artist;
	}

	@Override
	public String play() {
		return "Playing " + title + " for " + artist;
	}

}
