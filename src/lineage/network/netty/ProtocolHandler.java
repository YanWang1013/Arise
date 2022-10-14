package lineage.network.netty;

import java.util.HashMap;

import lineage.Main;
import lineage.bean.Ip;
import lineage.bean.event.ClientClose;
import lineage.bean.event.ClientConnect;
import lineage.database.BadIpDatabase;
import lineage.network.Client;
import lineage.network.Server;
import lineage.share.Common;
import lineage.thread.EventThread;
import lineage.util.Util;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public final class ProtocolHandler extends SimpleChannelUpstreamHandler {

	// 외부 공격으로부터 방어하기위한 ip목록들
	private HashMap<String, Ip> _list;
	
	public ProtocolHandler(){
		_list = new HashMap<String, Ip>();
	}
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		String[] address = e.getChannel().getRemoteAddress().toString().substring(1).split(":");
		String ip = address[0];
		int port = Integer.valueOf(address[1]);
		long time = System.currentTimeMillis();
		
		// 블럭된 아이피인지 확인.
		if(BadIpDatabase.find(ip) != null){
			e.getChannel().close();
			return;
		}
		// ddos 공격 방어
		if(port<=0){
			e.getChannel().close();
			return;
		}
		// 무한접속형 섭폭 필터링 (dos공격)
		Ip IP = _list.get(ip);
		if(IP == null){
			IP = new Ip();
			IP.ip = ip;
			IP.time = time;
			_list.put(IP.ip, IP);
			return;
		}
		// 블럭된 놈은 무시.
		if(IP.block){
			e.getChannel().close();
			return;
		}
		// 1초내에 5번이상 접속하는 놈들 무시.
		if(time < IP.time+1000){
			if(IP.count>5){
				IP.block = true;
				e.getChannel().close();
				return;
			}else{
				IP.count++;
			}
		}else{
			IP.count = 0;
		}
		IP.time = time;
	}
	
	/**
	 * 클라 접속처리 함수.
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// 서버가 닫힐때는 소켓접속요청 안받기.
		if(!Main.running){
			e.getChannel().close();
			return;
		}
		
		// 접속 허용된 클라만 처리.
		if(e.getChannel().isConnected()){
			// 키 생성
			long key = Util.random(0, Long.MAX_VALUE);
			// 클라이언트 객체 풀목록에서 가져오기. 없으면 생성해서 가져옴.
			Client c = Server.getPool( e.getChannel(), key );
			// 이벤트 쓰레드에게 넘기기.
			EventThread.append( ClientConnect.clone(EventThread.getPool(ClientConnect.class), c, key) );
		}else{
			lineage.share.System.printf("접속 차단 => %s\r\n", e.getChannel().getRemoteAddress().toString());
		}
	}

	/**
	 * 클라 종료처리 함수.
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Client c = Server.find(e.getChannel());
		// 이벤트 쓰레드에게 넘기기.
		if(c != null){
			EventThread.append( ClientClose.clone(EventThread.getPool(ClientClose.class), c) );
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		// TODO Auto-generated method stub
//		lineage.share.System.println(e.toString());
	}
	
	
}
