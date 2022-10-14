package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Bugbear extends MonsterInstance {
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Bugbear();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void readDrop(){
		// 본던 7층 버그베어 일경우 비밀방 열쇠 드랍하도록 하기.
		// Gludio dungeon 7F, monster bugbear > secret room key drop
		if(getMap() == 13){
			Item item = ItemDatabase.find("비밀방 열쇠");
			if(item != null){
				int chance = 2000 + item.getDropChance();
				if(Util.random(1,10000) <= chance*Lineage.rate_drop){
					ItemInstance ii = ItemDatabase.newInstance(item);
					if(ii != null)
						inv.append(ii, true);
				}
			}
		}
		super.readDrop();
	}

}
