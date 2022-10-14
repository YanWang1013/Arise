package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_PartyInfo;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public final class PartyController {

	static private Map<PcInstance, Party> list;
	static private List<Party> pool;
	static private long key;
	
	static public void init() {
		TimeLine.start("PartyController..");
		
		pool = new ArrayList<Party>();
		list = new HashMap<PcInstance, Party>();
		key = 1;
		
		TimeLine.end();
	}
	
	/**
	 * 월드 나갈때 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		try {
			close( pc );
		} catch (Exception e) { }
	}
	
	/**
	 * 파티 경험치 처리.
	 * @param cha
	 * @param target
	 * @param exp
	 * @return
	 */
	static public boolean toExp(Character cha, Character target, double exp){
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			Party p = find(pc);
			if(p != null){
				p.toExp(pc, target, exp);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 파티 초대 처리
	 * @param pc
	 * @param use
	 */
	static public void toParty(PcInstance pc, PcInstance use){
		if(use!=null && !use.isDead()){
			if(use.getPartyId()==0){
				Party p = find(pc);
				if(p == null){
					p = getPool();
					p.setKey(key++);
					p.setMaster(pc);
					p.append(pc);
					pc.setPartyId(p.getKey());
					list.put(pc, p);
				}
				
				if(p.getTemp() == null){
					if(p.getMaster().getObjectId() == pc.getObjectId()){
						p.setTemp(use);
						use.setPartyId(p.getKey());
						// %0%s 파티에 참여하기를 원합니다. 승낙하시겠습니까? (y/N)
						use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 422, pc.getName()));
						return;
					}else{
						// 파티의 지도자만이 초청할 수 있습니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 416));
					}
				}
				
			}else{
				// 이미 다른 파티의 구성원입니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 415));
			}
		}
	}
	
	/**
	 * 파티 승낙여부 뒷처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toParty(PcInstance pc, boolean yes){
		Party p = find(pc);
		if(p!=null){
			if(p.getTemp()!=null && p.getTemp().getObjectId()==pc.getObjectId()){
				if(yes){
					if(p.getSize() < Lineage.party_max){
						// %0%s 파티에 들어왔습니다.
						p.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 424, pc.getName()));
						p.append(pc);
						p.toUpdate(pc, true);
						p.setTemp(null);
						return;
					}else{
						// 더 이상 파티 구성원을 받아 들일 수 없습니다.
						p.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 417));
					}
				}else{
					// %0%s 초청을 거부했습니다.
					p.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 423, pc.getName()));
				}
			}
			p.setTemp(null);
			
			// 파티 를 해산해도 될경우 처리.
			if(p.getSize()==1)
				close(p.getMaster());
		}
		pc.setPartyId(0);
	}
	
	/**
	 * 파티 정보 표현 처리 함수.
	 * @param pc
	 */
	static public void toInfo(PcInstance pc){
		Party p = PartyController.find(pc);
		if(p != null){
			pc.toSender(S_PartyInfo.clone(BasePacketPooling.getPool(S_PartyInfo.class), p, "party"));
		}else{
			// 파티에 가입하지 않았습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 425));
		}
	}
	
	/**
	 * 미니 hp바 갱신 처리 함수.
	 * @param pc
	 */
	static public void toUpdate(PcInstance pc){
		Party p = find(pc);
		if(p != null)
			p.toUpdate(pc);
	}
	
	/**
	 * 객체별 파티 찾기.
	 * @param pc
	 * @return
	 */
	static public Party find(PcInstance pc){
		Party p = null;
		if(pc.getPartyId()>0){
			p = list.get(pc);
			if(p == null){
				for(Party pp : list.values()){
					if(pp.getKey() == pc.getPartyId())
						return pp;
				}
			}
		}
		return p;
	}
	
	static public void close(PcInstance pc){
		Party p = find(pc);
		if(p != null){
			try {
				p.remove(pc);
				if(p.getMaster().getObjectId()==pc.getObjectId() || p.getSize()==1)
					close(p);
			} catch (Exception e) {
				lineage.share.System.printf("%s : close(PcInstance pc)\r\n", PartyController.class.toString());
				lineage.share.System.println(e);
			}
		}
	}
	
	static public void close(Party p){
		if(p != null){
			list.remove(p.getMaster());
			setPool(p);
		}
	}
	
	static private Party getPool(){
		Party p = null;
		if(pool.size()>0){
			p = pool.get(0);
			pool.remove(0);
		}else{
			p = new Party();
		}
		return p;
	}
	
	static private void setPool(Party p){
		p.close();
		pool.add(p);
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
