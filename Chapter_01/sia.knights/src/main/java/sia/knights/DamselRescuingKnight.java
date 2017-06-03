package sia.knights;

public class DamselRescuingKnight implements Knight {

	private RescueDamselQuest quest;

	public DamselRescuingKnight() {
		this.quest = new RescueDamselQuest();// 与ResuceDamselQuest紧耦合
	}

	@Override
	public void embarOnQuest() {
		quest.embark();
	}

}
