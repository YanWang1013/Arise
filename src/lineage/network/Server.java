package lineage.network;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import lineage.network.netty.CodecFactory;
import lineage.network.netty.ProtocolHandler;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.client.C_ServerVersion;
import lineage.network.packet.server.S_Cryptkey;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.share.System;
import lineage.share.TimeLine;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public final class Server {

	// netty
	static private ServerBootstrap sb;
	static private CodecFactory cf;
	// toTimer에서 사용하며, 제거가능한 목록 임시 저장용.
	static private List<Client> list_remove;
	// 접속한 클라이언트 저장용.
	static private List<Client> list;
	// 사용후 버려진 클라이언트 객체들 재사용을위해 사용.
	static private List<Client> pool;
	
	/**
	 * 서버 활성화에 사용되는 초기화 함수.
	 */
	static public void init() throws Exception {
		TimeLine.start("Server..");
		
		list_remove = new ArrayList<Client>();
		list = new ArrayList<Client>();
		pool = new ArrayList<Client>();
		
		sb = new ServerBootstrap( new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()) );
		cf = new CodecFactory(new ProtocolHandler());
		sb.setPipelineFactory(cf);
		// http에서 사용하는건데.. 기본값이 먼지 몰라서 인위적으로 false로 설정.
		// 서버 성능을 높이기위해 클라가 접속을 끊더라도 세션을 유지하는 알고리즘임..필요없으므로 false
		sb.setOption("child.keepAlive", false);
		// Naggle 비활성.
		sb.setOption("child.tcpNoDelay", true);
		// 받을 패킷의 최대양.
		sb.setOption("child.receiveBufferSize", Socket.packet_send_max);
		// 기본 ChannelBuffer를 Little Endian으로 설정
		//  : Netty는 자바 기반이라서 기본이 Big Endian이다.
		//  : C로 구성된 서버의 경우 기본이 Little Endian이다.
		//  : ascii 문자를 주고 받을 경우에는 Endian이 무관해 보이지만, integer 등을 보내려면 Endian을 반드시 고려해야 한다.
//		sb.setOption("bufferFactory", HeapChannelBufferFactory.getInstance(ChannelBuffers.LITTLE_ENDIAN));
		// Connection Timeout 설정 ddos때문에 0.3초로 설정.
		sb.setOption("connectTimeoutMillis", 300);
		// 서버 활성화.
		sb.bind(new InetSocketAddress(Socket.PORT));
		
		TimeLine.end();
	}
	
	/**
	 * 서버 소켓 닫기 처리 함수.
	 * @throws Exception
	 */
	static public void close(Connection con) throws Exception {
		synchronized (list) {
			// 접속된 모든 클라 종료 및 후처리
			for(Client c : list){
				c.close(con, true);
				c = null;
			}
			list.clear();
		}
		list_remove.clear();
		sb = null;
	}
	
	/**
	 * 소켓으로 해당하는 클라 찾기.
	 * @param socket
	 * @return
	 */
	static public Client find(Channel socket){
		if(socket!=null){
			synchronized (list) {
				for(Client c : list){
					if(c.getSocket().hashCode() == socket.hashCode())
						return c;
				}
			}
		}
		return null;
	}
	
	/**
	 * 로그인한 사용자 계정 uid값으로 클라 찾기.
	 * @param account_uid
	 * @return
	 */
	static public Client find(int account_uid){
		if(account_uid>0){
			synchronized (list) {
				for(Client c : list){
					if(c.getAccountUid() == account_uid)
						return c;
				}
			}
		}
		return null;
	}
	
	/**
	 * 서버로부터 클라이언트 접속 해제 처리 함수.
	 * @param c
	 */
	static public void close(Client c){
		if(c == null)
			return;
		
		// 목록에서 제거
		remove(c);
		// 소켓 닫기 및 뒷처리
		c.close(null, false);
		// 재사용을 위해 풀에 넣기.
		synchronized (pool) {
			pool.add(c);
		}
		
	}
	
	/**
	 * 서버로 클라 접속했을때 처리하는 함수
	 * @param c
	 */
	static public void connect(Client c, long key){
		// 목록에 추가
		append(c);
		if(Lineage.server_version>200){
			// 블로우피쉬에 사용되는키 전송.
			c.toSender( S_Cryptkey.clone(BasePacketPooling.getPool(S_Cryptkey.class), key) );
		}else{
			BasePacketPooling.setPool( C_ServerVersion.clone(BasePacketPooling.getPool(C_ServerVersion.class), null, 0).init(c) );
		}
	}
	
	/**
	 * 풀에 있는 클라이언트 추출해서 던지는 함수.
	 * @param socket
	 * @param key
	 * @return
	 */
	static public Client getPool(Channel socket, long key){
		Client c = null;
		synchronized (pool) {
			if(pool.size()>0){
				c = pool.get(0);
				pool.remove(0);
			}else{
				c = new Client();
			}
			c.clone(socket, key);
		}
		return c;
	}
	
	/**
	 * 타이머에서 주기적으로 호출되는 함수.
	 *  : Netty쪽 디코딩 및 인코딩시 Server.find를 호출함.
	 *    거기서 list를 참고하기때문에 동기화 필요.
	 */
	static public void toTimer(long time){
		synchronized (list) {
			// 제거해도 되는 객체가 존재하는지 확인.
			for(Client c : list){
				if(c.isDelete(time) && !list_remove.contains(c))
					list_remove.add(c);
			}
		}
		// 제거처리
		for(Client c : list_remove) {
			close(c);
		}
		list_remove.clear();
	}
	
	/**
	 * 클라이언트 관리목록에 추가.
	 *  : Netty쪽 디코딩 및 인코딩시 Server.find를 호출함.
	 *    거기서 list를 참고하기때문에 동기화 필요.
	 * @param c
	 */
	static private void append(Client c){
		synchronized (list) {
			if(list.add(c)){}
//				lineage.share.System.printf("append client %d\r\n", list.size());
		}
	}
	
	/**
	 * 클라이언트 관리목록에서 제거.
	 *  : Netty쪽 디코딩 및 인코딩시 Server.find를 호출함.
	 *    거기서 list를 참고하기때문에 동기화 필요.
	 * @param c
	 */
	static private void remove(Client c){
		synchronized (list) {
			if(list.remove(c)){}
//				lineage.share.System.printf("remove client %d\r\n", list.size());
		}
	}
	
	/**
	 * 로그에 기록할 정보 만들어서 리턴.
	 * 	사용된 정보 초기화 처리도 함게함.
	 * @return
	 */
	static public String getLogNetwork(){
		StringBuffer sb = new StringBuffer();
		synchronized (list) {
			for(Client c : list){
				int recv = c.getRecvLength();
				int send = c.getSendLength();
				
				// 기록.
				sb.append( String.format("%s|%d|%d\t", c.getAccountIp(), recv, send) );
				
				// 초당 전송량을 오바햇을경우 제거목록에 등록.
				if(send > Socket.packet_send_max){
					list_remove.add(c);
				}else{
					c.setRecvLength(0);
					c.setSendLength(0);
				}
			}
		}
		// 제거처리
		for(Client c : list_remove){
			System.println( String.format("%s 초당 패킷량 제한 오바로 강제 종료됨.", c.getAccountIp()) );
			System.println( String.format(" : %d => %d", Lineage.party_max, c.getSendLength()) );
			close(c);
		}
		list_remove.clear();
		return sb.toString();
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	static public int getClientSize(){
		synchronized (list) {
			return list.size();
		}
	}
	
	static public CodecFactory getCodec(){
		return cf;
	}
}
