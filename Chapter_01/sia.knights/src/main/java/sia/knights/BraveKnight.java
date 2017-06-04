package sia.knights;

public class BraveKnight implements Knight {

	private Quest quest;

	public BraveKnight(Quest quest) {// Quest注入
		this.quest = quest;
	}

	@Override
	public void embarOnQuest() {
		quest.embark();
	}

}
