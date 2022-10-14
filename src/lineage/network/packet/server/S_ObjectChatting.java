package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class S_ObjectChatting extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o, int mode, String msg){
		if(bp == null)
			bp = new S_ObjectChatting(o, mode, msg);
		else
			((S_ObjectChatting)bp).clone(o, mode, msg);
		return bp;
	}
	
	static public BasePacket clone(BasePacket bp, String msg){
		if(bp == null)
			bp = new S_ObjectChatting(null, 0x14, msg);
		else
			((S_ObjectChatting)bp).clone(null, 0x14, msg);
		return bp;
	}
	
	public S_ObjectChatting(object o, int mode, String msg){
		clone(o, mode, msg);
	}
	
	public void clone(object o, int mode, String msg){
		clear();
		
		StringBuffer text = new StringBuffer();
		switch(mode){
			case Lineage.CHATTING_MODE_NORMAL:
				text.append(o.getName());
				text.append(": ");
				text.append(msg);
				normal(o, mode, text.toString());
				break;
			case Lineage.CHATTING_MODE_SHOUT:
				if(o != null){
					text.append(o.getName());
					text.append(": ");
				}
				text.append(msg);
				shotsay(o, text.toString());
				break;
			case Lineage.CHATTING_MODE_GLOBAL:
				if(o==null || o.getGm()>=Lineage.GMCODE) {
					text.append("[******] ");
				} else {
					text.append("[");
					text.append(o.getName());
					text.append("] ");
				}
				text.append(msg);
				global(o, mode, text.toString());
				break;
			case Lineage.CHATTING_MODE_CLAN:
				text.append("{");
				text.append(o.getName());
				text.append("} ");
				text.append(msg);
				clan(o, mode, text.toString());
				break;
			case 0x05:	// 수호기사채팅 : 원래타입은 0x04인데 위에랑 증복되어 따로만듬
//				writeC(Opcodes.S_OPCODE_GLOBALCHAT);
//				writeC(0x04);
//				writeS("{{"+cha.get_name()+"}} "+chat);
				break;
			case 0x08: // 귓속말 - 받는사람
				whisperReceiver(o, mode, msg);
				break;
			case 0x09:	// 귓속말 - 보낸사람
				text.append("-> (");
				text.append(o==null ? "******" : o.getName());
				text.append(") ");
				text.append(msg);
				whisperSender(o, mode, text.toString());
				break;
			case Lineage.CHATTING_MODE_PARTY:
				text.append("(");
				text.append(o.getName());
				text.append(") ");
				text.append(msg);
				party(o, mode, text.toString());
				break;
			case Lineage.CHATTING_MODE_TRADE:
				if(Lineage.server_version <= 200)
					text.append("\\fR");
				text.append("[");
				text.append(o.getName());
				text.append("] ");
				text.append(msg);
				trade(mode, text.toString());
				break;
			case 0x0D: // 혈맹 수호기사 채팅 %
				break;
			case 0x0E: // 채팅파티 채팅 *
				break;
			case 0x14:
				message(0x09, msg);
				break;
		}
	}
	
	private void trade(int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}
	
	/**
	 * 일반 채팅
	 */
	private void normal(object o, int mode, String msg){
		if(o instanceof PcInstance)
			writeC(Opcodes.S_OPCODE_NORMALCHAT);
		else
			writeC(Opcodes.S_OPCODE_SHOTSAY);
		writeC(mode);
		writeD(o.getObjectId());
		writeS(msg);
		writeH(o.getX());
		writeH(o.getY());
	}

	/**
	 * 전체 채팅
	 */
	private void global(object o, int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 혈맹 채팅
	 */
	private void clan(object o, int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 귓속말
	 */
	private void whisperReceiver(object o, int mode, String msg){
		writeC(Opcodes.S_OPCODE_WHISPERCHAT);
		writeS(o==null ? "******" : o.getName());
		writeS(msg);
	}

	/**
	 * 파티 채팅
	 */
	private void party(object o, int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 귓속말
	 */
	private void whisperSender(object o, int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 일반 텍스트 표현
	 */
	private void message(int mode, String msg){
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	private void shotsay(object o, String msg){
		writeC(Opcodes.S_OPCODE_SHOTSAY);
		writeC( 0x02 );		// 0x00:일반채팅색상, 0x02:외치기색상
		if(o != null)
			writeD(o.getObjectId());
		else
			writeD(0);
		writeS(msg);
	}
}
