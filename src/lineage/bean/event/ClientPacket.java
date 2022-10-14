package lineage.bean.event;

import lineage.network.Client;
import lineage.network.Server;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.Opcodes;
import lineage.network.packet.client.C_AccountChanger;
import lineage.network.packet.client.C_Ask;
import lineage.network.packet.client.C_BlockName;
import lineage.network.packet.client.C_BoardClick;
import lineage.network.packet.client.C_BoardDelete;
import lineage.network.packet.client.C_BoardPaging;
import lineage.network.packet.client.C_BoardView;
import lineage.network.packet.client.C_BoardWrite;
import lineage.network.packet.client.C_BookAppend;
import lineage.network.packet.client.C_BookRemove;
import lineage.network.packet.client.C_CharacterCreate;
import lineage.network.packet.client.C_CharacterDelete;
import lineage.network.packet.client.C_ClanCreate;
import lineage.network.packet.client.C_ClanInfo;
import lineage.network.packet.client.C_ClanJoin;
import lineage.network.packet.client.C_ClanKin;
import lineage.network.packet.client.C_ClanMarkDownload;
import lineage.network.packet.client.C_ClanMarkUpload;
import lineage.network.packet.client.C_ClanOut;
import lineage.network.packet.client.C_ClanWar;
import lineage.network.packet.client.C_Door;
import lineage.network.packet.client.C_Dungeon;
import lineage.network.packet.client.C_Dustbin;
import lineage.network.packet.client.C_DwarfAndShop;
import lineage.network.packet.client.C_FriendAdd;
import lineage.network.packet.client.C_FriendList;
import lineage.network.packet.client.C_FriendRemove;
import lineage.network.packet.client.C_HyperText;
import lineage.network.packet.client.C_InterfaceSave;
import lineage.network.packet.client.C_ItemClick;
import lineage.network.packet.client.C_ItemDrop;
import lineage.network.packet.client.C_ItemPickup;
import lineage.network.packet.client.C_KingdomWarTimeSelect;
import lineage.network.packet.client.C_KingdomWarTimeSelectFinal;
import lineage.network.packet.client.C_Letter;
import lineage.network.packet.client.C_Login;
import lineage.network.packet.client.C_NoticeOk;
import lineage.network.packet.client.C_ObjectAttack;
import lineage.network.packet.client.C_ObjectAttackBow;
import lineage.network.packet.client.C_ObjectChatting;
import lineage.network.packet.client.C_ObjectChattingOption;
import lineage.network.packet.client.C_ObjectChattingWhisper;
import lineage.network.packet.client.C_ObjectGiveItem;
import lineage.network.packet.client.C_ObjectHeading;
import lineage.network.packet.client.C_ObjectMoving;
import lineage.network.packet.client.C_ObjectParty;
import lineage.network.packet.client.C_ObjectPartyList;
import lineage.network.packet.client.C_ObjectPartyOut;
import lineage.network.packet.client.C_ObjectRestart;
import lineage.network.packet.client.C_ObjectSkill;
import lineage.network.packet.client.C_ObjectTalk;
import lineage.network.packet.client.C_ObjectTalkAction;
import lineage.network.packet.client.C_ObjectTitle;
import lineage.network.packet.client.C_ObjectWho;
import lineage.network.packet.client.C_PkCounter;
import lineage.network.packet.client.C_Potal;
import lineage.network.packet.client.C_ServerVersion;
import lineage.network.packet.client.C_SkillBuy;
import lineage.network.packet.client.C_SkillBuyOk;
import lineage.network.packet.client.C_Smith;
import lineage.network.packet.client.C_SmithFinal;
import lineage.network.packet.client.C_StatDice;
import lineage.network.packet.client.C_SummonTargetSelect;
import lineage.network.packet.client.C_TaxGet;
import lineage.network.packet.client.C_TaxPut;
import lineage.network.packet.client.C_TaxSetting;
import lineage.network.packet.client.C_TimeLeft;
import lineage.network.packet.client.C_Trade;
import lineage.network.packet.client.C_TradeCancel;
import lineage.network.packet.client.C_TradeItem;
import lineage.network.packet.client.C_TradeOk;
import lineage.network.packet.client.C_Unknow;
import lineage.network.packet.client.C_UserShop;
import lineage.network.packet.client.C_UserShopList;
import lineage.network.packet.client.C_WorldtoJoin;
import lineage.network.packet.client.C_WorldtoOut;
import lineage.share.Common;
import lineage.util.Util;

public class ClientPacket implements Event {

	private Client c;
	private byte[] data;
	private int length;
	
	public ClientPacket(Client c, byte[] data, int length){
//		lineage.share.System.println("ClientPacket 생성..");
		
		this.data = new byte[Common.BUFSIZE*10];
		clone(c, data, length);
	}
	
	static public Event clone(Event e, Client c, byte[] data, int length){
		if(e == null)
			e = new ClientPacket(c, data, length);
		else
			((ClientPacket)e).clone(c, data, length);
		return e;
	}
	
	/**
	 * 변수 복사를 위해.
	 * @param c
	 */
	public void clone(Client c, byte[] data, int length){
		this.c = c;
		this.length = length;
		System.arraycopy(data, 0, this.data, 0, length);
		
//		lineage.share.System.println("ClientPacket data hashcode : "+this.data.hashCode());
	}
	
	@Override
	public void init() {
		int op = 0;
		try {
			if(c == null)
				return;
			
			// 패킷 처리.
			op = data[0]&0xff;
			
			if(op == Opcodes.C_OPCODE_SERVER_VERSION){
				if(c.getAccountUid()==0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ServerVersion.clone(BasePacketPooling.getPool(C_ServerVersion.class), data, length).init(c) );

			}else if(op == Opcodes.C_OPCODE_QUITGAME){
				// 서버 관리목록에서 제거
				// 클라 종료 뒷처리도 함께 함.
				Server.close(c);
			
			}else if(op == Opcodes.C_OPCODE_CHANGEACCOUNT){
				if(c.getAccountUid()==0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_AccountChanger.clone(BasePacketPooling.getPool(C_AccountChanger.class), data, length).init(c) );
		
			}else if(op == Opcodes.C_OPCODE_LOGINPACKET){
				if(c.getAccountUid()==0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Login.clone(BasePacketPooling.getPool(C_Login.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_TIMELEFTOK){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TimeLeft.clone(BasePacketPooling.getPool(C_TimeLeft.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_NOTICEOK){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_NoticeOk.clone(BasePacketPooling.getPool(C_NoticeOk.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_RETURNTOLOGIN){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete()){
					c.setAccountUid(0);
					c.setAccountId(null);
				}
			
			}else if(op==Opcodes.C_OPCODE_NEWCHAR || op==Opcodes.C_OPCODE_NEWCHAR_STAT){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_CharacterCreate.clone(BasePacketPooling.getPool(C_CharacterCreate.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_DELETECHAR){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_CharacterDelete.clone(BasePacketPooling.getPool(C_CharacterDelete.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_LOGINTOSERVER){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_WorldtoJoin.clone(BasePacketPooling.getPool(C_WorldtoJoin.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_STATDICE){
				if(c.getAccountUid()!=0 && c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_StatDice.clone(BasePacketPooling.getPool(C_StatDice.class), data, length).init(c) );
			
			}else if(op == Opcodes.C_OPCODE_REQUESTCHARSELETE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_WorldtoOut.clone(BasePacketPooling.getPool(C_WorldtoOut.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_REQUESTMOVECHAR){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectMoving.clone(BasePacketPooling.getPool(C_ObjectMoving.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_DOORS){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Door.clone(BasePacketPooling.getPool(C_Door.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOOKMARK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BookAppend.clone(BasePacketPooling.getPool(C_BookAppend.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOOKMARKDELETE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BookRemove.clone(BasePacketPooling.getPool(C_BookRemove.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_PING){
				c.setPing();
		
			}else if(op==Opcodes.C_OPCODE_REQUESTCHAT || op==Opcodes.C_OPCODE_REQUESTCHATGLOBAL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectChatting.clone(BasePacketPooling.getPool(C_ObjectChatting.class), data, length).init(c.getPc()) );
		
			}else if(op == Opcodes.C_OPCODE_REQUESTWHO){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectWho.clone(BasePacketPooling.getPool(C_ObjectWho.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_REQUESTCHATWHISPER){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectChattingWhisper.clone(BasePacketPooling.getPool(C_ObjectChattingWhisper.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CHATTINGOPTION){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectChattingOption.clone(BasePacketPooling.getPool(C_ObjectChattingOption.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANMAKE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanCreate.clone(BasePacketPooling.getPool(C_ClanCreate.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANOUT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanOut.clone(BasePacketPooling.getPool(C_ClanOut.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANKIN){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanKin.clone(BasePacketPooling.getPool(C_ClanKin.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANJOIN){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanJoin.clone(BasePacketPooling.getPool(C_ClanJoin.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ASK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Ask.clone(BasePacketPooling.getPool(C_Ask.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANMARKUPLOAD){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanMarkUpload.clone(BasePacketPooling.getPool(C_ClanMarkUpload.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANMARKDOWNLOAD){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanMarkDownload.clone(BasePacketPooling.getPool(C_ClanMarkDownload.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_REQUESTHEADING){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectHeading.clone(BasePacketPooling.getPool(C_ObjectHeading.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_USEITEM){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ItemClick.clone(BasePacketPooling.getPool(C_ItemClick.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ITEMDROP){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ItemDrop.clone(BasePacketPooling.getPool(C_ItemDrop.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ITEMPICKUP){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ItemPickup.clone(BasePacketPooling.getPool(C_ItemPickup.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ATTACK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectAttack.clone(BasePacketPooling.getPool(C_ObjectAttack.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ATTACKBOW){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectAttackBow.clone(BasePacketPooling.getPool(C_ObjectAttackBow.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_USESKILL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectSkill.clone(BasePacketPooling.getPool(C_ObjectSkill.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_NPCTALK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectTalk.clone(BasePacketPooling.getPool(C_ObjectTalk.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_NPCACTION){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectTalkAction.clone(BasePacketPooling.getPool(C_ObjectTalkAction.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_SHOP){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_DwarfAndShop.clone(BasePacketPooling.getPool(C_DwarfAndShop.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TRADE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Trade.clone(BasePacketPooling.getPool(C_Trade.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TREADCANCEL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TradeCancel.clone(BasePacketPooling.getPool(C_TradeCancel.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TREADOK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TradeOk.clone(BasePacketPooling.getPool(C_TradeOk.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TREADADDITEM){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TradeItem.clone(BasePacketPooling.getPool(C_TradeItem.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_POTALOK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Potal.clone(BasePacketPooling.getPool(C_Potal.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_LETTER){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Letter.clone(BasePacketPooling.getPool(C_Letter.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_UNKNOW){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Unknow.clone(BasePacketPooling.getPool(C_Unknow.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_REQUESTRESTART){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectRestart.clone(BasePacketPooling.getPool(C_ObjectRestart.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_PARTY){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectParty.clone(BasePacketPooling.getPool(C_ObjectParty.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_PARTYOUT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectPartyOut.clone(BasePacketPooling.getPool(C_ObjectPartyOut.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_PARTYLIST){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectPartyList.clone(BasePacketPooling.getPool(C_ObjectPartyList.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_GIVEITEM){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectGiveItem.clone(BasePacketPooling.getPool(C_ObjectGiveItem.class), data, length).init(c.getPc()) );
				
			}else if(op == Opcodes.C_OPCODE_TITLE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ObjectTitle.clone(BasePacketPooling.getPool(C_ObjectTitle.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_DUSTBIN){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Dustbin.clone(BasePacketPooling.getPool(C_Dustbin.class), data, length).init(c.getPc()) );
				
			}else if(op == Opcodes.C_OPCODE_REQUESTHYPERTEXT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_HyperText.clone(BasePacketPooling.getPool(C_HyperText.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_SKILLBUY){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_SkillBuy.clone(BasePacketPooling.getPool(C_SkillBuy.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_SKILLBUYOK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_SkillBuyOk.clone(BasePacketPooling.getPool(C_SkillBuyOk.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_CLANINFO){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanInfo.clone(BasePacketPooling.getPool(C_ClanInfo.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOARDCLICK){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BoardClick.clone(BasePacketPooling.getPool(C_BoardClick.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOARDWRITE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BoardWrite.clone(BasePacketPooling.getPool(C_BoardWrite.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOARDREAD){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BoardView.clone(BasePacketPooling.getPool(C_BoardView.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOARDDELETE){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BoardDelete.clone(BasePacketPooling.getPool(C_BoardDelete.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BOARDNEXT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BoardPaging.clone(BasePacketPooling.getPool(C_BoardPaging.class), data, length).init(c.getPc()) );
		
			}else if(op == Opcodes.C_OPCODE_TAXSETTING){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TaxSetting.clone(BasePacketPooling.getPool(C_TaxSetting.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TaxTotalPut){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TaxPut.clone(BasePacketPooling.getPool(C_TaxPut.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_TaxTotalGet){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_TaxGet.clone(BasePacketPooling.getPool(C_TaxGet.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_KINGDOMWARTIMESELECT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_KingdomWarTimeSelect.clone(BasePacketPooling.getPool(C_KingdomWarTimeSelect.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_KINGDOMWARTIMESELECTFINAL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_KingdomWarTimeSelectFinal.clone(BasePacketPooling.getPool(C_KingdomWarTimeSelectFinal.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_ClanWar){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_ClanWar.clone(BasePacketPooling.getPool(C_ClanWar.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_BLOCK_NAME){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_BlockName.clone(BasePacketPooling.getPool(C_BlockName.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_PKCOUNT){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_PkCounter.clone(BasePacketPooling.getPool(C_PkCounter.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_USERSHOP){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_UserShop.clone(BasePacketPooling.getPool(C_UserShop.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_USERSHOPLIST){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_UserShopList.clone(BasePacketPooling.getPool(C_UserShopList.class), data, length).init(c.getPc()) );
		
			}else if(op == Opcodes.C_OPCODE_SMITH){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Smith.clone(BasePacketPooling.getPool(C_Smith.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_SMITH_FINAL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_SmithFinal.clone(BasePacketPooling.getPool(C_SmithFinal.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_FRIENDLIST){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_FriendList.clone(BasePacketPooling.getPool(C_FriendList.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_FRIENDADD){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_FriendAdd.clone(BasePacketPooling.getPool(C_FriendAdd.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_FRIENDDEL){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_FriendRemove.clone(BasePacketPooling.getPool(C_FriendRemove.class), data, length).init(c.getPc()) );
			
			}else if(op == Opcodes.C_OPCODE_DUNGEON){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Dungeon.clone(BasePacketPooling.getPool(C_Dungeon.class), data, length).init(c.getPc()) );
				
			}else if(op == Opcodes.C_OPCODE_INTERFACESAVE){
				if(c.getAccountUid()!=0)
					BasePacketPooling.setPool( C_InterfaceSave.clone(BasePacketPooling.getPool(C_InterfaceSave.class), data, length).init(c.getPc()) );
				
			}else if(op == Opcodes.C_OPCODE_TargetSelect){
				if(c.getAccountUid()!=0 && !c.getPc().isWorldDelete())
					BasePacketPooling.setPool( C_SummonTargetSelect.clone(BasePacketPooling.getPool(C_SummonTargetSelect.class), data, length).init(c.getPc()) );
			}
			
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			//
		} catch (Exception e) {
			lineage.share.System.printf("%s : public void init() %d\r\n", ClientPacket.class.toString(), op);
			lineage.share.System.println(e);
			lineage.share.System.printf("[client]\r\n%s\r\n", Util.printData(data, length) );
		}
	}

	@Override
	public void close() {
		
	}
	
}
