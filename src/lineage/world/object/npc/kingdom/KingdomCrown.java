package lineage.world.object.npc.kingdom;

import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_KingdomAgent;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.ClanController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class KingdomCrown extends object {

	private Kingdom kingdom;
	
	public KingdomCrown(Kingdom kingdom){
		this.kingdom = kingdom;
	}

	@Override
	public void toPickup(Character cha){
		// 처리 가능여부 확인. 조건문 졸라많네..ㅡㅡㅋ
		if(	cha.isDead() || kingdom==null || cha.getGfx()!=cha.getClassGfx() || cha.isInvis() || cha.getLevel()<25 || 
			cha.getClanId()==0 || cha.getClanId()==kingdom.getClanId() || cha.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL || 
			KingdomController.find((PcInstance)cha)!=null || AgitController.find((PcInstance)cha)!=null || 
			(!kingdom.getListWar().contains(cha.getClanName()) && kingdom.getClanId() != 0))
			return;
		
		// 줍기 표현.
		cha.setHeading( Util.calcheading(cha, x, y) );
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);
		
		// 성에 혈맹이 지정되있을 경우에만 처리.
		if(kingdom.getClanId() > 0){
			// 전쟁 종료 알림 패킷 전송.
			for(String clan_name : kingdom.getListWar()){
				Clan clan = ClanController.find(clan_name);
				if(clan != null){
					clan.setWarClan(null);
					World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan_name, kingdom.getClanName()));
				}
			}
			kingdom.getListWar().clear();
	
			// 픽업한 혈맹이 승리한거 알리기.
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 4, cha.getClanName(), kingdom.getClanName()));
		}
		
		// 성정보 변경.
		kingdom.setAgentId(cha.getObjectId());
		kingdom.setAgentName(cha.getName());
		kingdom.setClanId(cha.getClanId());
		kingdom.setClanName(cha.getClanName());
		// 리니지 월드에 해당 성에 대한 성주정보 다시 잡아주기
		World.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), kingdom.getUid(), kingdom.getAgentId()));
		
		// 모두 마을로 귀환. 성혈은 내성으로 이동됨.
		kingdom.toTeleport(false, true);
		
		// 수호탑 복구.
		for(KingdomCastleTop kct : kingdom.getListCastleTop())
			kct.toRevival(null);
		
		// 면류관 표현 제거.
		kingdom.getCrown().clearList(true);
		kingdom.getCrownVisual().clearList(true);
		World.remove(kingdom.getCrown());
		World.remove(kingdom.getCrownVisual());
	}
	
}
