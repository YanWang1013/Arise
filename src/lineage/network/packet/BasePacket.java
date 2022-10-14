package lineage.network.packet;

import lineage.network.Client;
import lineage.world.object.instance.PcInstance;

public interface BasePacket {

	/**
	 * 패킷 처리 함수.
	 */
	public BasePacket init(Client c);
	public BasePacket init(PcInstance pc);
	
}
