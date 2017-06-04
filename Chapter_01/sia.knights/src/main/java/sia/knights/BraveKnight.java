package sia.knights;

public class BraveKnight implements Knight {

	private Quest quest;
	private Minstrel minstrel;

	public BraveKnight(Quest quest, Minstrel minstrel) {// Quest注入
		this.quest = quest;
		this.minstrel = minstrel;
	}

	@Override
	public void embarOnQuest() {
		minstrel.singBeforeQuest();// 骑士需要管理吟游诗人吗？
		quest.embark();
		minstrel.singAfterQuest();
	}

}
