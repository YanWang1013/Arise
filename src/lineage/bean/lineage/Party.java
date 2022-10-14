package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class Party {

	private long key;
	private PcInstance temp;
	private PcInstance master;
	private List<PcInstance> list;
	private List<PcInstance> temp_list;
	
	public Party(){
		temp_list = new ArrayList<PcInstance>();
		list = new ArrayList<PcInstance>();
		master = temp = null;
		key = 0;
	}
	
	public void close(){
		for(PcInstance pc : list){
			pc.setPartyId(0);
			// 파티를 해산했습니다.
			pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 418) );
			toUpdate(pc, false);
		}
		list.clear();
		master = temp = null;
		key = 0;
	}
	
	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public PcInstance getMaster(){
		return master;
	}
	
	public void setMaster(PcInstance pc){
		master = pc;
	}
	
	public PcInstance getTemp() {
		return temp;
	}

	public void setTemp(PcInstance temp) {
		this.temp = temp;
	}

	public void append(PcInstance pc){
		if(!list.contains(pc))
			list.add(pc);
	}
	
	public void remove(PcInstance pc){
		if(temp!=null && temp.getObjectId()==pc.getObjectId())
			temp = null;
		
		pc.setPartyId(0);
		// %0%s 파티를 떠났습니다
		toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 420, pc.getName()) );
		toUpdate(pc, false);
		list.remove(pc);
	}
	
	public int getSize(){
		return list.size();
	}
	
	public void toSender(BasePacket packet){
		if(packet instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)packet;
			for(PcInstance pc : list)
				pc.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
		}
		BasePacketPooling.setPool(packet);
	}
	
	public void toUpdate(PcInstance pc){
		for(PcInstance use : list){
			if(pc.getObjectId()!=use.getObjectId() && Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE))
				use.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, true));
		}
	}
	
	public void toUpdate(PcInstance pc, boolean visual){
		// 자기자신 처리.
		pc.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, visual));
		// 주변객체 처리.
		for(PcInstance use : list){
			if(pc.getObjectId()!=use.getObjectId() && Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE)){
				pc.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), use, visual));
				use.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, visual));
			}
		}
	}
	
	public void toExp(PcInstance pc, Character target, double exp){
		// 전체 파티구성원을 루프돌면서 거리에 있는녀석들만 추려낸다
		// 추려낸 녀석들에게만 받게될 경험치를 추려낸갯수만큼 나누고 그값을 더한다
		// 파티보너스 경험치도 지급한다.

		temp_list.clear();
		// 추려내기
		for(PcInstance use : list){
			if(Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE))
				temp_list.add(use);
		}
		
		// 갯수만큼 경험치 하향
		exp /= temp_list.size();
		// 파티 보너스 경험치
		exp *= Lineage.rate_party;
		// 라우풀 지급 값 추출.
		int lawful = (target.getLevel() * 3) / 2;
		// 갯수만큼 라우풀 지급 하향.
		lawful /= temp_list.size();
		// 라우풀 배율 에 따라 증가.
		lawful *= Lineage.rate_lawful;
		
		if(exp>0){
			// 지급
			for(PcInstance use : temp_list){
				if( !use.isDead() ){
					// 경험치 지급.
					use.toExp(target, exp);
					// 라우풀 지급.
					use.setLawful(use.getLawful() + lawful);
				}
			}
		}
	}
	
	public List<PcInstance> getList(){
		return list;
	}
}
