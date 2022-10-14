package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

public class Clan {
	private int uid;					// 혈맹 고유 키
	private String name;				// 혈맹 이름
	private String lord;				// 군주 이름
	private byte[] icon;				// 혈맹 문장
	private List<String> member_list;	// 혈맹원들 이름 목록
	private List<PcInstance> list;		// 접속중인 혈맹원 목록
	private PcInstance temp_pc;			// 사용자 정보 임시 저장용으로 활용중. 가입요청시 기록함.
	private String war_clan;			// 전쟁중인 혈맹에 이름.
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLord() {
		return lord;
	}

	public void setLord(String lord) {
		this.lord = lord;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public PcInstance getTempPc() {
		return temp_pc;
	}

	public void setTempPc(PcInstance temp_pc) {
		this.temp_pc = temp_pc;
	}
	
	public String getWarClan() {
		return war_clan;
	}

	public void setWarClan(String war_clan) {
		this.war_clan = war_clan;
	}

	public List<String> getMemberList(){
		return member_list;
	}

	public Clan(){
		list = new ArrayList<PcInstance>();
		member_list = new ArrayList<String>();
		war_clan = null;
	}
	
	/**
	 * 혈맹이 해체될때 호출됨.
	 *  : 풀에 넣기위한 준비 단계.
	 *    사용된 메모리 제거 등 처리함.
	 */
	public void close(){
		list.clear();
		member_list.clear();
		uid = 0;
		temp_pc = null;
		icon = null;
		war_clan = null;
	}
	
	/**
	 * 해당 객체의 혈맹원이 월드에 진입할때 호출됨.
	 * @param pc
	 */
	public void toWorldJoin(PcInstance pc){
		// 혈맹원 %0님께서 방금 게임에 접속하셨습니다.
		toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("혈맹원 %s님께서 방금 게임에 접속하셨습니다.", pc.getName())));

		list.add(pc);
	}
	
	/**
	 * 해당 객체의 혈맹원이 월드에 빠져나갈때 호출됨.
	 * @param pc
	 */
	public void toWorldOut(PcInstance pc){
		list.remove(pc);
	}
	
	public List<PcInstance> getList(){
		return list;
	}
	
	/**
	 * 접속되있는 전체 혈맹원들에게 패킷 전송 처리 함수.
	 * @param bp
	 */
	public void toSender(BasePacket bp){
		if(bp instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)bp;
			for(PcInstance pc : list)
				pc.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
		}
		BasePacketPooling.setPool(bp);
	}
	
	/**
	 * 접속된 혈맹원 목록에서 동일한 이름을 가진 사용자 찾아서 리턴.
	 * @param name
	 * @return
	 */
	public PcInstance find(String name){
		for(PcInstance pc : list){
			if(pc.getName().equalsIgnoreCase(name))
				return pc;
		}
		return null;
	}
	
	/**
	 * 접속된 혈맹원 목록에서 군주를 찾아 리턴하는 함수.
	 * @return
	 */
	public PcInstance getRoyal(){
		for(PcInstance pc : list){
			if(pc.getName().equalsIgnoreCase(lord))
				return pc;
		}
		return null;
	}
	
	/**
	 * 접속중인 혈맹원들 이름 정리<br/>
	 *  : 접속된 혈맹원들 목록보기 패킷 처리에 사용.
	 * @return
	 */
	public String getMemberNameListConnect(){
		StringBuffer sb = new StringBuffer();
		for(PcInstance pc : list){
			sb.append(pc.getName());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * 전체 혈맹원들 이름 추출
	 * @return
	 */
	public String getMemberNameList(){
		StringBuffer sb = new StringBuffer();
		for(String name : member_list){
			sb.append(name);
			sb.append(" ");
		}
		return sb.toString();
	}
}
