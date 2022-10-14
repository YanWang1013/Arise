package lineage.network.codec;

import lineage.bean.event.ClientPacket;
import lineage.network.Client;
import lineage.network.Server;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.thread.EventThread;
import lineage.util.Util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public final class Decoder extends OneToOneDecoder {

	// 초당 받은 패킷양 기록용. 로그에 사용됨.
	static public int recv_length;
	
	static {
		recv_length = 0;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if(msg instanceof ChannelBuffer){
			Client c = Server.find(channel);
			ChannelBuffer buffer = (ChannelBuffer)msg;
			
			synchronized (c.decoder_data) {
				// 데이타 축적
				int size = buffer.readableBytes();
				buffer.readBytes(c.decoder_data, 0, size);
				c.decoder_pos += size;
				// 로그 기록을위해 패킷량 갱신.
				recv_length += size;
				
				for(;;){
					// head 사이즈 추출
					int length = c.decoder_data[0] & 0xff;
					length |=  c.decoder_data[1] << 8 & 0xff00;
					if(c.decoder_pos>=length){
						c.decoder_pos -= length;
						length -= 2;
						// 처리할 패킷에 담기
						System.arraycopy(c.decoder_data, 2, c.decoder_temp_data, 0, length);
						// 처리할 패킷 을 제거하기위에 뒤쪽에있는 패킷 앞으로 당기기
						System.arraycopy(c.decoder_data, 2+length, c.decoder_data, 0, c.decoder_pos);
						if(c != null){
							if(Lineage.server_version>200){
								// 복호화
								char[] incoming = new char[length];
								incoming = c.getEncryption().getUChar8().fromArray(c.decoder_temp_data, incoming, length);
								incoming = c.getEncryption().decrypt(incoming, length);
								c.getEncryption().getUByte8().fromArray(incoming, c.decoder_temp_data);
							}else if(Lineage.server_version>138){
								// 복호화
								decrypt(c, c.decoder_temp_data, length, c.packet_C_total_size);
								// 처리된 패킷사이즈 기록
								c.packet_C_total_size += length;
							}else{
								// 복호화
								decrypt(c.decoder_temp_data);
							}
							// 로그 기록을위해 패킷량 갱신.
							c.setSendLength( c.getSendLength() +  length + 2);
						}
						// 패킷 출력
						if(Socket.PRINTPACKET)
							lineage.share.System.printf("[client]\r\n%s\r\n", Util.printData(c.decoder_temp_data, length) );
						// 이벤트 쓰레드에게 넘기기.
						EventThread.append( ClientPacket.clone(EventThread.getPool(ClientPacket.class), c, c.decoder_temp_data, length) );
					}else{
						break;
					}
				}
			}
		}
		return msg;
	}
	
	private void decrypt(Client c, byte[] data, int size, long total_length){
		getByte(c, total_length);
		System.arraycopy(data, 0, c.decoder_data_temp, 0, size);
		int idx = c.decoder_data_header[0];
		for(int i=0 ; i<size ; ++i){
			if(i>0 && i%8 == 0){
				for(int j=0 ; j<i ; ++j)
					data[i] ^= data[j];
				if(i%16 == 0){
					for(byte st : c.decoder_data_header)
						data[i] ^= st;
				}
				for(int j=1 ; j<c.decoder_data_header.length ; ++j)
					data[i] ^= c.decoder_data_header[j];
				try {
					for(int j=1 ; j<c.decoder_data_header.length ; ++j)
						data[i+j] ^= c.decoder_data_header[j];
				} catch (Exception e) { }
			}else{
				data[i] ^= idx;
				try {
					if(i==0){
						for(int j=1 ; j<c.decoder_data_header.length ; ++j){
							data[i+j] ^= c.decoder_data_header[j];
						}
					}
				} catch (Exception e) { }
			}
			idx = c.decoder_data_temp[i];
		}
	}
	
	private void getByte(Client c, long c_size){
		c.decoder_data_header[0] = (byte)(c_size & 0xff);
		c.decoder_data_header[1] = (byte)(c_size >> 8 & 0xff);
		c.decoder_data_header[2] = (byte)(c_size >> 16 & 0xff);
		c.decoder_data_header[3] = (byte)(c_size >> 24 & 0xff);
	}
	
	/**
	 * 리니지 1.x대 클라이언트 암호화 알고리즘
	 *  : 서버->클라 로 가는 패킷은 암호화 이루어 지지 않음
	 *  : 클라->서버 패킷은 암호화가 되어있어서 복호화 필요.
	 *    op값은 복호화를 하지 않아도 됨.
	 *    이후 패킷 복호화 필요.
	 *    복호화할 1byte값을 temp로 임시저장함 다음복호화에 사용됨
	 * @param data
	 * @return
	 */
	private void decrypt(byte[] data){
		int idx = data[0];
		int idx_temp = 0;
		for(int i=1 ; i<data.length ; ++i){
			idx_temp = data[i];
			data[i] = (byte)(data[i] ^ idx);
			idx = idx_temp;
		}
	}
	
}
