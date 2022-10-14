package lineage.world.object.monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.MonsterInstance;

public class Blob extends Slime {
	
	private Map<Item, List<Craft>> list;			// 제작될아이템(item) 과 연결된 재료 목록
	private Map<String, Item> craft_list;			// 요청청 문자(action)와 연결될 제작될아이템(item)
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Blob();
		return MonsterInstance.clone(mi, m);
	}
	
	public Blob(){
		list = new HashMap<Item, List<Craft>>();
		craft_list = new HashMap<String, Item>();
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("단검신");
		if(i != null){
			craft_list.put("1", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 1) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 50) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("장검신");
		if(i != null){
			craft_list.put("2", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 3) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 150) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("오리하루콘 검신");
		if(i != null){
			craft_list.put("3", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 3) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 150) );
			l.add( new Craft(ItemDatabase.find("루비"), 3) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("미스릴 도금 뿔");
		if(i != null){
			craft_list.put("4", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판의 뿔"), 2) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 80) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("오리하루콘 도금 뿔");
		if(i != null){
			craft_list.put("5", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판의 뿔"), 4) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 80) );
			l.add( new Craft(ItemDatabase.find("루비"), 3) );
			list.put(i, l);
		}
	}
	
	@Override
	protected void toAiDead(long time){
		// 재료 아이템 확인해서 처리하기.
		for(Item i : craft_list.values()){
			List<Craft> l = list.get(i);
			// 재료 확인.
			while(CraftController.isCraft(this, l, false)){
				// 재료 제거.
				CraftController.toCraft(this, l);
				// 제작된 아이템 등록.
				CraftController.toCraft(this, this, i, 1, false);
			}
		}
		// 뒷처리.
		super.toAiDead(time);
	}
	
}
