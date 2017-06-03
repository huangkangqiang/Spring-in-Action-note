package sia.knights;

import static org.mockito.Mockito.*;

import org.junit.Test;

public class BraveKnightTest {

	@Test
	public void testEmbarOnQuest() {
		Quest mockQuest = mock(Quest.class);// Mock一个Quest
		BraveKnight knight = new BraveKnight(mockQuest);// 将Mock出来的Quest注入
		knight.embarOnQuest();
		verify(mockQuest, times(1)).embark();
	}

}
