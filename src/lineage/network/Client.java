package lineage.network;

import java.sql.Connection;

import lineage.Main;
import lineage.bean.lineage.StatDice;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.util.Encryption;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

import org.jboss.netty.channel.Channel;

public final class Client {

	// 핑 타임 기록용.
	private long ping_time;
	// 패킷처리에 사용되는 소켓객체
	private Channel socket;
	// 패킷 암호화 및 복호화에 쓰이는 객체
	private Encryption cryption;
	public long packet_C_total_size;
	public long packet_S_total_size;
	public int decoder_pos = 0;
	public byte[] decoder_data;
	public byte[] decoder_temp_data;
	public byte[] decoder_data_temp;
	public byte[] decoder_data_header;
	public byte[] encoder_test;
	public byte[] encoder_data;
	public byte[] encoder_data_temp;
	public byte[] encoder_data_header;
	
	// 리니지와 연관있는 변수들
	private int account_uid;
	private String account_id;
	private int account_time;
	private String account_ip;
	private int account_notice_uid;
	private int account_notice_static_uid;
	private PcInstance pc;
	private StatDice stat_dice;
	// 홈페이지와 연관된 변수들
	private int web_member_uid;
	// 로그 기록용
	private int recv_length;		// 해당 클라가 받은 패킷 량
	private int send_length;		// 해당 클라가 보낸 패킷 량
	
	public Client(){
		pc = new PcInstance(this);
		stat_dice = new StatDice();
		cryption = new Encryption();
		
		decoder_data = new byte[Common.BUFSIZE*100];
		decoder_temp_data = new byte[Common.BUFSIZE*10];
		decoder_data_temp = new byte[Common.BUFSIZE*10];
		decoder_data_header = new byte[4];
		encoder_test = new byte[8];
		encoder_data = new byte[Common.BUFSIZE*100];
		encoder_data_temp = new byte[Common.BUFSIZE*10];
		encoder_data_header = new byte[4];
		
		pc.close();
	}
	
	public void clone(Channel socket, long key){
		this.socket = socket;
		ping_time = System.currentTimeMillis();
		cryption.initKeys(key);
		account_time = account_uid = web_member_uid = account_notice_uid = recv_length = send_length = 
		account_notice_static_uid = 0;
		account_id = null;
		packet_C_total_size = packet_S_total_size = 0;
		account_ip = socket.getRemoteAddress().toString();
		account_ip = account_ip.substring(1, account_ip.indexOf(":"));
		decoder_pos = 0;
	}
	
	/**
	 * 클라이언트 객체를 지워도 되는지 확인해주는 함수.
	 *  : ping이 4분이상 오바됫을경우 true 리턴.
	 * @return
	 */
	public boolean isDelete(long time){
		// 핑 오바되엇는지 확인. 1분10초
		boolean is_ping = time-ping_time >= 1000*Lineage.client_ping_time;
		// 계정시간이 다됫는지 확인.
		boolean is_account_time = Lineage.flat_rate && account_uid>0 ? --account_time<=0 : false;
		// 둘중 하나라도 참이라면 true.
		return is_ping || is_account_time;
	}
	
	/**
	 * 클라가 C_OPCODE_PING 던지면 호출됨.
	 */
	public void setPing(){
		ping_time = System.currentTimeMillis();
	}
	
	/**
	 * 접속 해제 처리 함수.
	 */
	public void close(Connection con, boolean fast){
		try {
			// 접속된 상태라면 끊기.
			if(!fast && socket!=null && socket.isConnected())
				socket.close();
		} catch (Exception e) { }
		try {
			// 월드에 남은 상태라면 뒷처리 하기.
			if(!fast && !pc.isWorldDelete()){
				// 월드에서 나갓다는거 알리기.
				pc.toWorldOut();
			}
		} catch (Exception e) { }
		try {
			if(con == null)
				con = DatabaseConnection.getLineage();
			// 로그인된 상태라면 계정정보 갱신.
			if(Lineage.flat_rate && account_uid>0)
				AccountDatabase.updateTime(con, account_uid, account_time);
		} catch (Exception e) {}
		if(!fast)
			DatabaseConnection.close(con);
		socket = null;
	}
	
	/**
	 * 패킷 전송처리 요청 함수.
	 * @param o
	 */
	public void toSender(Object o){
		// 서버 종료 요청된 상태라면 무시.
		if(!Main.running)
			return;
		
		if(socket!=null && socket.isConnected()){
			socket.write(o);
		}else{
			if(o instanceof BasePacket)
				BasePacketPooling.setPool((BasePacket)o);
		}
	}
	
	public Encryption getEncryption(){
		return cryption;
	}
	
	public Channel getSocket(){
		return socket;
	}
	
	public void setAccountUid(int account_uid){
		this.account_uid = account_uid;
	}
	
	public int getAccountUid(){
		return account_uid;
	}

	public String getAccountId() {
		return account_id;
	}

	public void setAccountId(String account_id) {
		this.account_id = account_id;
	}
	
	public int getAccountTime() {
		return account_time;
	}

	public void setAccountTime(int account_time) {
		this.account_time = account_time;
	}
	
	public int getAccountNoticeUid() {
		return account_notice_uid;
	}
	
	public void setAccountNoticeUid(int account_notice_uid) {
		this.account_notice_uid = account_notice_uid;
	}
	
	public int getAccountNoticeStaticUid() {
		return account_notice_static_uid;
	}

	public void setAccountNoticeStaticUid(int account_notice_static_uid) {
		this.account_notice_static_uid = account_notice_static_uid;
	}

	public PcInstance getPc(){
		return pc;
	}
	
	public StatDice getStatDice(){
		return stat_dice;
	}
	
	public int getWebMemberUid(){
		return web_member_uid;
	}
	
	public void setWebMemberUid(int web_member_uid){
		this.web_member_uid = web_member_uid;
	}

	public String getAccountIp() {
		return account_ip;
	}

	public void setAccountIp(String account_ip) {
		this.account_ip = account_ip;
	}

	public int getRecvLength() {
		return recv_length;
	}

	public void setRecvLength(int recv_length) {
		this.recv_length = recv_length;
	}

	public int getSendLength() {
		return send_length;
	}

	public void setSendLength(int send_length) {
		this.send_length = send_length;
	}
}
