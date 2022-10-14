package lineage.network.codec;

import lineage.database.ServerDatabase;
import lineage.network.Client;
import lineage.network.Server;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Cryptkey;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.util.Util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.LittleEndianHeapChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public final class Encoder extends OneToOneEncoder {
	
	// 초당 전송된 패킷에 양 기록용. 로그에 사용됨.
	static public int send_length;
	
	static {
		send_length = 0;
	}
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if(msg instanceof ServerBasePacket){
			Client c = Server.find(channel);
			ServerBasePacket sbp = (ServerBasePacket)msg;
			ChannelBuffer buffer = null;
			
			if(c != null){
				synchronized (c.encoder_data) {
					// 초기화
					byte[] temp = sbp.getBytes();
					int length = temp.length + 2;
					
					// 사이즈값 넣기.
					c.encoder_data[0] = (byte)(length & 0xff);
					c.encoder_data[1] = (byte)(length >> 8 &0xff);
					
					// 패킷 출력
					if(Socket.PRINTPACKET)
						lineage.share.System.printf("[server]\r\n%s\r\n", Util.printData(temp, length-2) );

					if(Lineage.server_version>200){
						if( !(sbp instanceof S_Cryptkey) ){
							char[] buf = c.getEncryption().getUChar8().fromArray(temp, length-2);
							buf = c.getEncryption().encrypt(buf);
							temp = c.getEncryption().getUByte8().fromArray(buf);
							buf = null;
						}
						// 데이타 넣기
						System.arraycopy(temp, 0, c.encoder_data, 2, length-2);
						// 처리
						buffer = new LittleEndianHeapChannelBuffer(length);
						
					}else if(Lineage.server_version>138){
						// 패킷 부가처리 확인.
						boolean test = test(temp[0], c.packet_S_total_size);
						if(test){
							c.encoder_test[0] = (byte)Opcodes.S_OPCODE_GAMETIME;
							c.encoder_test[1] = (byte)(ServerDatabase.LineageWorldTime &0xff);
							c.encoder_test[2] = (byte)(ServerDatabase.LineageWorldTime >> 8 &0xff);
							c.encoder_test[3] = (byte)(ServerDatabase.LineageWorldTime >> 16 &0xff);
							c.encoder_test[4] = (byte)(ServerDatabase.LineageWorldTime >> 24 &0xff);
							// 암호화
							encrypt(c, c.encoder_test, 8, c.packet_S_total_size);
							c.packet_S_total_size += 8;
							encrypt(c, temp, length-2, c.packet_S_total_size);
							c.packet_S_total_size += length-2;
							// 사이즈값 넣기.
							c.encoder_data[0] = (byte)(10 & 0xff);
							c.encoder_data[1] = (byte)(10 >> 8 &0xff);
							c.encoder_data[10] = (byte)(length & 0xff);
							c.encoder_data[11] = (byte)(length >> 8 &0xff);
							// 데이타 넣기
							System.arraycopy(c.encoder_test, 0, c.encoder_data, 2, 8);
							System.arraycopy(temp, 0, c.encoder_data, 12, length-2);
							// 사이즈 재정의
							length += 10;
						}else{
							// 암호화
							encrypt(c, temp, length-2, c.packet_S_total_size);
							c.packet_S_total_size += length-2;
							// 데이타 넣기
							System.arraycopy(temp, 0, c.encoder_data, 2, length-2);
						}
						// 처리
						buffer = new LittleEndianHeapChannelBuffer(length);
						
					}else{
						// 데이타 넣기
						System.arraycopy(temp, 0, c.encoder_data, 2, length-2);
						// 처리
						buffer = new LittleEndianHeapChannelBuffer(length);
						
					}
					buffer.writeBytes(c.encoder_data, 0, length);

					temp = null;
					// 로그 기록을위해 패킷량 갱신.
					send_length += length;
					c.setRecvLength( c.getRecvLength() +  length);
				}
			}
			
			// 풀에 다시 넣기
			BasePacketPooling.setPool(sbp);
			
			// 처리를 위해 리턴.
			return buffer;
		}
		return msg;
	}
	
	private void encrypt(Client c, byte[] data, int size, long total_size){
		getByte(c, total_size);
		System.arraycopy(data, 0, c.encoder_data_temp, 0, size);
		int idx = c.encoder_data_header[0];
		for(int i=0 ; i<size ; ++i){
			if(i>0 && i%8 == 0){
				for(int j=0 ; j<i ; ++j)
					data[i] ^= c.encoder_data_temp[j];
				if(i%16==0){
					for(byte st : c.encoder_data_header)
						data[i] ^= st;
				}
				for(int j=1 ; j<c.encoder_data_header.length ; ++j)
					data[i] ^= c.encoder_data_header[j];
				try {
					// 세번째 인코딩 처리
					for(int j=1 ; j<c.encoder_data_header.length ; ++j)
						data[i+j] ^= c.encoder_data_header[j];
				} catch (Exception e) { }
			}else{
				data[i] ^= idx;
				if(i==0){
					try {
						for(int j=1 ; j<c.encoder_data_header.length ; ++j)
							data[i+j] ^= c.encoder_data_header[j];
					} catch (Exception e) { }
				}
			}
			idx = data[i];
		}
	}
	
	private void getByte(Client c, long c_size){
		c.encoder_data_header[0] = (byte)(c_size & 0xff);
		c.encoder_data_header[1] = (byte)(c_size >> 8 & 0xff);
		c.encoder_data_header[2] = (byte)(c_size >> 16 & 0xff);
		c.encoder_data_header[3] = (byte)(c_size >> 24 & 0xff);
	}
	
	private boolean test(byte op, long total_size) {
//		System.out.println( total_size%256 );
		
		int o = op&0xff;
		if(o == Opcodes.S_OPCODE_PoisonAndLock)
			return total_size%256 == 8;
		else if(o == Opcodes.S_OPCODE_SHOWHTML)
			return total_size%256 == 16;
		else if(o == Opcodes.S_OPOCDE_ATTRIBUTE)
			return total_size%256 == 24;
		else if(o==Opcodes.S_OPCODE_ITEMSTATUS || o==Opcodes.S_OPCODE_ITEMCOUNT)
			return total_size%256 == 32;
		else if(o == Opcodes.S_OPCODE_CHANGEHEADING)
			return total_size%256 == 38;
		else if(o == Opcodes.S_OPCODE_MOVEOBJECT)
			return total_size%256 == 40;
		else if(o == Opcodes.S_OPCODE_BlindPotion)
			return total_size%256 == 48;
		else if(o == Opcodes.S_OPCODE_TRUETARGET)
			return total_size%256 == 64;
		else if(o == Opcodes.S_OPCODE_CRIMINAL)
			return total_size%256 == 80;
		else if(o == Opcodes.S_OPCODE_SKILLBRAVE)
			return total_size%256 == 88;
		else if(o == Opcodes.S_OPCODE_SOUNDEFFECT)
			return total_size%256 == 112;
		return false;
	}
	
}
