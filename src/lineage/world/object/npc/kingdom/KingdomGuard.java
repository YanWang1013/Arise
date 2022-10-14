package lineage.world.object.npc.kingdom;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.SentryGuard;

public class KingdomGuard extends SentryGuard {

	private Npc npc;
	private Kingdom kingdom;
	private List<String> list_html;
	private List<object> list_temp;
	
	public KingdomGuard(Npc npc, Kingdom kingdom){
		super(npc);
		this.kingdom = kingdom;
		list_html = new ArrayList<String>();
		list_temp = new ArrayList<object>();
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(kingdom==null)
			return;

		list_html.clear();
		list_html.add(kingdom.getClanName());
		list_html.add(kingdom.getAgentName());
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mercenary1", null, list_html));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(kingdom==null)
			return;
		
		// 다음공성전 시간 호출
		if(action.equalsIgnoreCase("askwartime")){
			if(kingdom.getWarDay() > 0){
				list_html.clear();
				list_html.add( String.valueOf(Util.getYear(kingdom.getWarDay())+1900) );
				list_html.add( String.valueOf(Util.getMonth(kingdom.getWarDay())) );
				list_html.add( String.valueOf(Util.getDate(kingdom.getWarDay())) );
				list_html.add( String.valueOf(Util.getHours(kingdom.getWarDay())) );
				list_html.add( String.valueOf(Util.getMinutes(kingdom.getWarDay())) );
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mercenary2", null, list_html));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mercenary3"));
			}
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type){
		// 성처리 경비병일경우 같은 소속혈맹이 아닐경우에만 처리하기.
		if(kingdom==null || kingdom.getClanId()==0 || kingdom.getClanId()!=cha.getClanId())
			super.toDamage(cha, dmg, type);
	}
	
	@Override
	protected void toSearchPKer(){
		if(kingdom==null || kingdom.getClanId()==0){
			super.toSearchPKer();
			return;
		}
		
		// 성처리 경비병일경우 같은 소속혈맹이 아닐경우에만 처리하기.
		for(object o : insideList){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(kingdom.getClanId()!=pc.getClanId() && pc.getPkTime()>0)
					addAttackList(pc);
			}
		}
	}
	
	@Override
	public void toAiAttack(long time){
		if(kingdom.getClanId() != 0){
			list_temp.clear();
			list_temp.addAll(attackList);
			// 전투목록 둘러보면서 같은 혈맹 소속자가 있을경우 제거.
			for(object o : list_temp){
				if(o.getClanId() == kingdom.getClanId())
					attackList.remove(o);
			}
		}
		super.toAiAttack(time);
	}
	
}
