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
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.PatrolGuard;

public class Nerupa extends PatrolGuard {
	
	public Nerupa(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("페어리 더스트");
		if(i != null){
			i.getListCraft().put("request fairydust", 20);
			craft_list.put("request fairydust", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("정령의 돌"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("신성한 요정족 판금 갑옷");
		if(i != null){
			craft_list.put("request holy elven plate armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 보석"), 50) );
			l.add( new Craft(ItemDatabase.find("코리안코인"), 10000) );
			l.add( new Craft(ItemDatabase.find("요정족 판금 갑옷"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("신성한 엘름의 축복");
		if(i != null){
			craft_list.put("request holy bless of elm", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 보석"), 25) );
			l.add( new Craft(ItemDatabase.find("코리안코인"), 5000) );
			l.add( new Craft(ItemDatabase.find("엘름의 축복"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("신성한 요정족 방패");
		if(i != null){
			craft_list.put("request holy elven shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 보석"), 25) );
			l.add( new Craft(ItemDatabase.find("코리안코인"), 5000) );
			l.add( new Craft(ItemDatabase.find("요정족 방패"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("신성한 마법 방어 투구");
		if(i != null){
			craft_list.put("request holy magic defense helm", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 보석"), 25) );
			l.add( new Craft(ItemDatabase.find("코리안코인"), 5000) );
			l.add( new Craft(ItemDatabase.find("마법 방어 투구"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("신성한 보호 망토");
		if(i != null){
			craft_list.put("request holy protective cloak", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 보석"), 25) );
			l.add( new Craft(ItemDatabase.find("코리안코인"), 5000) );
			l.add( new Craft(ItemDatabase.find("보호 망토"), 1) );
			list.put(i, l);
		}

		i = ItemDatabase.find("화살");
		if(i != null){
			craft_list.put("request arrow", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("미스릴 화살");
		if(i != null){
			i.getListCraft().put("request mithril arrow", 100);
			craft_list.put("request mithril arrow", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 1) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("활");
		if(i != null){
			craft_list.put("request bow", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 1) );
			l.add( new Craft(ItemDatabase.find("실"), 5) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 활");
		if(i != null){
			craft_list.put("request elven bow", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 20) );
			l.add( new Craft(ItemDatabase.find("실"), 2) );
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 2) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("크로스 보우");
		if(i != null){
			craft_list.put("request crossbow", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 8) );
			l.add( new Craft(ItemDatabase.find("오리하루콘 판금"), 3) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 20) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 30) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("장궁");
		if(i != null){
			craft_list.put("request yumi", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘 도금 뿔"), 1) );
			l.add( new Craft(ItemDatabase.find("오리하루콘 판금"), 6) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 40) );
			l.add( new Craft(ItemDatabase.find("고급 에메랄드"), 2) );
			l.add( new Craft(ItemDatabase.find("고급 다이아몬드"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 5) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 단검");
		if(i != null){
			craft_list.put("request elven dagger", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 5) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 20) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("메일 브레이커");
		if(i != null){
			craft_list.put("request mail breaker", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 도금 뿔"), 1) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("단검신"), 1) );
			l.add( new Craft(ItemDatabase.find("다이아몬드"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 50) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 검");
		if(i != null){
			craft_list.put("request elven short sword", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("장검신"), 1) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 5) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 150) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 50) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("레이피어");
		if(i != null){
			craft_list.put("request rapier", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘 검신"), 1) );
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 2) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 50) );
			l.add( new Craft(ItemDatabase.find("고급 루비"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 25) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("몽둥이");
		if(i != null){
			craft_list.put("request club", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 5) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("전투 도끼");
		if(i != null){
			craft_list.put("request battle axe", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("단검신"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 5) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 60) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("귀사름");
		if(i != null){
			craft_list.put("request guisarme", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("단검신"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 10) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("미스릴"), 90) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 창");
		if(i != null){
			craft_list.put("request elven spear", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 도금 뿔"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 30) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("포챠드");
		if(i != null){
			craft_list.put("request fauchard", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘 도금 뿔"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 50) );
			l.add( new Craft(ItemDatabase.find("오리하루콘"), 60) );
			l.add( new Craft(ItemDatabase.find("고급 루비"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("나무 줄기 옷");
		if(i != null){
			craft_list.put("request wooden jacket", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 10) );
			l.add( new Craft(ItemDatabase.find("실"), 6) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("나무 갑옷");
		if(i != null){
			craft_list.put("request wooden armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 2) );
			l.add( new Craft(ItemDatabase.find("판의 갈기털"), 5) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 흉갑");
		if(i != null){
			craft_list.put("request elven breast plate", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 2) );
			l.add( new Craft(ItemDatabase.find("실"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 사슬 갑옷");
		if(i != null){
			craft_list.put("request elven chain mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 판금"), 4) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 판금 갑옷");
		if(i != null){
			craft_list.put("request elven plate mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("오리하루콘 판금"), 8) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 20) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("나무 방패");
		if(i != null){
			craft_list.put("request wooden shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 5) );
			l.add( new Craft(ItemDatabase.find("엔트의 줄기"), 5) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 방패");
		if(i != null){
			craft_list.put("request elven shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 판금"), 2) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 5) );
			l.add( new Craft(ItemDatabase.find("나무 방패"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 가죽 투구");
		if(i != null){
			craft_list.put("request elven leather helm", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 1) );
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 2) );
			l.add( new Craft(ItemDatabase.find("판의 갈기털"), 10) );
			l.add( new Craft(ItemDatabase.find("아라크네의 거미줄"), 20) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("엘름의 축복");
		if(i != null){
			craft_list.put("request bless of elm", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("요정족 가죽 투구"), 1) );
			l.add( new Craft(ItemDatabase.find("고급 다이아몬드"), 1) );
			l.add( new Craft(ItemDatabase.find("고급 에메랄드"), 1) );
			l.add( new Craft(ItemDatabase.find("고급 사파이어"), 1) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 5) );
			l.add( new Craft(ItemDatabase.find("오리하루콘 판금"), 3) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 150) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("활 골무");
		if(i != null){
			craft_list.put("request bracer", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 20) );
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 3) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("파워 글러브");
		if(i != null){
			craft_list.put("request power gloves", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 5) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 20) );
			l.add( new Craft(ItemDatabase.find("오우거의 피"), 1) );
			l.add( new Craft(ItemDatabase.find("고급 다이아몬드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("요정족 망토");
		if(i != null){
			craft_list.put("request elven cloak", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 2) );
			l.add( new Craft(ItemDatabase.find("페어리 더스트"), 120) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 10) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("짧은 장화");
		if(i != null){
			craft_list.put("request low boots", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 2) );
			l.add( new Craft(ItemDatabase.find("실"), 4) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("부츠");
		if(i != null){
			craft_list.put("request boots", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 2) );
			l.add( new Craft(ItemDatabase.find("실"), 10) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF
				|| pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL
				|| pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT
				|| pc.getClassType() == Lineage.LINEAGE_CLASS_WIZARD || pc.getGm() > 0) {
			if(pc.getLawful()<Lineage.NEUTRAL && pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaCE1"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaE1"));
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nerupaM1"));
		}
	}

}
