package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Message extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, int number){
		if(bp == null)
			bp = new S_Message(number, null);
		else
			((S_Message)bp).clone(number, null);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, int number, String msg){
		if(bp == null)
			bp = new S_Message(number, msg);
		else
			((S_Message)bp).clone(number, msg);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, int number, String msg1, String msg2){
		if(bp == null)
			bp = new S_Message(number, msg1, msg2);
		else
			((S_Message)bp).clone(number, msg1, msg2);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, int number, String msg1, String msg2, String msg3){
		if(bp == null)
			bp = new S_Message(number, msg1, msg2, msg3);
		else
			((S_Message)bp).clone(number, msg1, msg2, msg3);
		return bp;
	}
	
	public S_Message(int number, String msg){
		clone(number, msg);
	}
	
	public S_Message(int number, String msg1, String msg2){
		clone(number, msg1, msg2);
	}
	
	public S_Message(int number, String msg1, String msg2, String msg3){
		clone(number, msg1, msg2, msg3);
	}
	
	public void clone(int number, String msg){
		clear();
		writeC(Opcodes.S_OPCODE_SERVERMSG);
		writeH(number);
		if(msg!=null){
			writeC(0x01);
			writeS(msg);
		}
	}
	
	public void clone(int number, String msg1, String msg2){
		clear();
		writeC(Opcodes.S_OPCODE_SERVERMSG);
		writeH(number);
		writeC(0x02);
		writeS(msg1);
		writeS(msg2);
	}
	
	public void clone(int number, String msg1, String msg2, String msg3){
		clear();
		writeC(Opcodes.S_OPCODE_SERVERMSG);
		writeH(number);
		writeC(0x03);
		writeS(msg1);
		writeS(msg2);
		writeS(msg3);
	}
}
