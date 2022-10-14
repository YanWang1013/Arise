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
import lineage.share.Lineage;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Ladar extends CraftInstance {
	
	public Ladar(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("아데나");
		if(i != null){
			i.getListCraft().put("request fur sell", 2);
			craft_list.put("request fur sell", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고급피혁");
		if(i != null){
			craft_list.put("request hard leather", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 20) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽모자");
		if(i != null){
			craft_list.put("request leather cap", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 5) );
			l.add( new Craft(ItemDatabase.find("철괴"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽샌달");
		if(i != null){
			craft_list.put("request leather sandal", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 6) );
			l.add( new Craft(ItemDatabase.find("철괴"), 2) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽조끼");
		if(i != null){
			craft_list.put("request leather vest", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽방패");
		if(i != null){
			craft_list.put("request leather shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("동물가죽"), 7) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽부츠");
		if(i != null){
			craft_list.put("request leather boots", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("징박은 가죽샌달"), 1) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 10) );
			l.add( new Craft(ItemDatabase.find("철괴"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 300) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("가죽투구");
		if(i != null){
			craft_list.put("request leather helmet", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("투구"), 1) );
			l.add( new Craft(ItemDatabase.find("가죽모자"), 1) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 5) );
			l.add( new Craft(ItemDatabase.find("철괴"), 15) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("중갑가죽조끼");
		if(i != null){
			craft_list.put("request hard leather vest", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽조끼"), 1) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 15) );
			l.add( new Craft(ItemDatabase.find("철괴"), 15) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("벨트달린 가죽조끼");
		if(i != null){
			craft_list.put("request leather vest with belt", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽조끼"), 1) );
			l.add( new Craft(ItemDatabase.find("벨트"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("벨트");
		if(i != null){
			craft_list.put("request belt", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급피혁"), 5) );
			l.add( new Craft(ItemDatabase.find("철괴"), 2) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ladar2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ladar1"));
	}

}
