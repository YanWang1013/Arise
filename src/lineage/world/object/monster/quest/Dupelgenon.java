/*
 * Felix: Create Dupelgenon Class
 */

package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Dupelgenon extends MonsterInstance {

	private Item quest_item;
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Dupelgenon();
		return MonsterInstance.clone(mi, m);
	}
	
	public Dupelgenon(){
		quest_item = ItemDatabase.find("듀펠게넌의 목걸이");
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		// System.out.print("Hey! Dupelgenon \n");
		// Dupelgenon shadow
		setGfx(901);
		//toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
		setGfxMode(Lineage.GFX_MODE_WALK);
		//toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), true);
		// 처리
		super.toTeleport(x, y, map, effect);
	}
	@Override
	// Felix : When kill Dupelgenon, drop necklace on land
	protected void toAiDead(long time){
		// 공격자 검색 하여 공격자가 1명이며, 퀘스트진행중이고 퀘스트스탭이 맞을경우 퀘아이템 오토루팅 지급 처리. 1개이상 지급못하도록 하기위해 인벤 검색도 함.
		if(quest_item!=null && attackList.size()==1){
			object o = attackList.get(0);
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15_DUPELGENON);
				if(q!=null && q.getQuestStep()==2 && pc.getInventory().findDbNameId(quest_item.getNameIdNumber())==null){
					ItemInstance ii = ItemDatabase.newInstance(quest_item);
					ii.setObjectId(ServerDatabase.nextItemObjId());
					inv.append(ii, true);
				}
			}
		}
		super.toAiDead(time);
	}
	@Override
	// Felix: wizard magic "Detection" > hide monster search skill..
	public void toMagicalAttackEncounters(Character cha){
		if(!isDead() && cha.getClassType() == Lineage.LINEAGE_CLASS_ELF){
			// attack
			setGfx(183);
			setGfxMode(Lineage.GFX_MODE_WALK);
			super.toTeleport(getX(), getY(), getMap(), false);
		}
	}

}
