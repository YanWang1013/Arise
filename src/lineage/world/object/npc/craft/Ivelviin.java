package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Ivelviin extends CraftInstance {
	
	public Ivelviin(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("싸울아비 장검");
		if(i != null){
			craft_list.put("request tsurugi", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 500) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 5) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 5) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 5) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 5) );
			l.add( new Craft(ItemDatabase.find("아시타지오의 재"), 30) );
			list.put(i, l);
		}
		i = ItemDatabase.find("화룡 비늘 갑옷");
		if(i != null){
			craft_list.put("request red dragon armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 1000) );
			l.add( new Craft(ItemDatabase.find("화룡 비늘"), 15) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 500) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 5) );
			l.add( new Craft(ItemDatabase.find("아시타지오의 재"), 10) );
			list.put(i, l);
		}
		i = ItemDatabase.find("수룡 비늘 갑옷");
		if(i != null){
			craft_list.put("request blue dragon armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 1000) );
			l.add( new Craft(ItemDatabase.find("수룡 비늘"), 15) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 500) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 5) );
			l.add( new Craft(ItemDatabase.find("아시타지오의 재"), 10) );
			list.put(i, l);
		}
		i = ItemDatabase.find("풍룡 비늘 갑옷");
		if(i != null){
			craft_list.put("request azure dragon armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 1000) );
			l.add( new Craft(ItemDatabase.find("풍룡 비늘"), 15) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 500) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 5) );
			l.add( new Craft(ItemDatabase.find("아시타지오의 재"), 10) );
			list.put(i, l);
		}
		i = ItemDatabase.find("지룡 비늘 갑옷");
		if(i != null){
			craft_list.put("request green dragon armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 1000) );
			l.add( new Craft(ItemDatabase.find("지룡 비늘"), 15) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 500) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 5) );
			l.add( new Craft(ItemDatabase.find("아시타지오의 재"), 10) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ivelviin1"));
	}

}
