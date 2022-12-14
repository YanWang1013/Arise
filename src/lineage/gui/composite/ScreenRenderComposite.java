package lineage.gui.composite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import lineage.bean.event.GuiToWorldAllObject;
import lineage.gui.GuiMain;
import lineage.gui.dialog.MonsterSpawn;
import lineage.gui.dialog.NpcSpawn;
import lineage.gui.dialog.PlayerInventory;
import lineage.gui.dialog.PlayerTeleport;
import lineage.gui.dialog.ShopEditor;
import lineage.share.Lineage;
import lineage.thread.EventThread;
import lineage.world.World;
import lineage.world.controller.CommandController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ScreenRenderComposite extends Composite implements Runnable {

	private Combo list_map;
	private Combo list_player;
	private Text hitObjectName;
	private Text hitObjectStr;
	private Text hitObjectDex;
	private Text hitObjectCon;
	private Text hitObjectWis;
	private Text hitObjectInt;
	private Text hitObjectCha;
	private Text hitObjectLevel;
	private Text hitObjectExp;
	private Text hitObjectHp;
	private Text hitObjectMp;
	private Text hitObjectAc;
	private Text hitObjectWeight;
	private Text hitObjectEarth;
	private Text hitObjectFire;
	private Text hitObjectWater;
	private Text hitObjectWind;
	private Text hitObjectLawfulValue;
	private Text hitObjectSp;
	private Text hitObjectMr;
	private Label hitObjectLawful;
	private Menu screen_menu;

	// ???????????? ?????????.
	private Composite screen;
	private Frame frame;
	private BufferStrategy strategy;
	// ????????? ??????
	private Font strFont;			// ???????????? ?????? ?????? ??????
	private Font npcFont;			// ????????? ?????? ??????
	private Font monsterFont;		// ????????? ?????? ??????
	private Font playerFont;		// ????????? ?????? ??????
	// ?????????
	private Color background_color;
	private Color player_color;
	private Color monster_color;
	private Color npc_color;
	// ?????? ????????? ????????? ?????????
	private Image hitObjectImage;
	// ????????? ?????? ?????? ?????? ??????.
	private object hitObject;
	// ?????? ????????????
	private static final int FRAME = 1000 / 30;
	// ???????????? ????????? ????????? ?????? ??????.
	private int cur_x;
	private int cur_y;
	// ???????????? ????????? ????????? ??????.
	private int cur_point_x;
	private int cur_point_y;
	// ??? ?????????
	private Image mapImage;
	// ??? ????????? ????????? ?????????
	private int map_x;
	private int map_y;
	private int map_dynamic_x;
	private int map_dynamic_y;
	// ????????? ?????????
	private int lineage_x;
	private int lineage_y;
	// ?????? ????????? ???
	private lineage.bean.lineage.Map nowMap;
	// ?????? ?????? ??????.
	private int mapWidth;
	private int mapHeight;
	// ?????? ?????????
	private int zoom;
	// ????????? ????????? ??????
	private int miniMap_x = 0;
	private int miniMap_y = 80;
	// ????????? ??????
	private int miniMap_width = 100;
	private int miniMap_height = 100;
	// ???????????? ??????????????? ??????
	private Color miniMap_focus_color;

	// ?????? ??????????????? ?????? ??????.
	private List<object> list_all;		// ?????? ??????????????? ????????? ?????????.
	// ????????? ????????? ????????? ?????????
	private Button screen_print_player;
	private Button screen_print_monster;
	private Button screen_print_background;
	private Button screen_print_npc;
	private Button screen_print_item;
	private Button screen_print_shop;


	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ScreenRenderComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		Group group_screen = new Group(this, SWT.NONE);
		group_screen.setText("?????????");
		group_screen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_group_screen = new GridLayout(6, false);
		gl_group_screen.verticalSpacing = 0;
		gl_group_screen.horizontalSpacing = 0;
		gl_group_screen.marginHeight = 0;
		gl_group_screen.marginWidth = 0;
		group_screen.setLayout(gl_group_screen);

		screen_print_item = new Button(group_screen, SWT.CHECK);
		screen_print_item.setText("?????????");

		screen_print_monster = new Button(group_screen, SWT.CHECK);
		screen_print_monster.setText("?????????");

		screen_print_npc = new Button(group_screen, SWT.CHECK);
		screen_print_npc.setText("?????????");

		screen_print_shop = new Button(group_screen, SWT.CHECK);
		screen_print_shop.setText("??????");

		screen_print_background = new Button(group_screen, SWT.CHECK);
		screen_print_background.setText("??????");

		screen_print_player = new Button(group_screen, SWT.CHECK);
		screen_print_player.setSelection(true);
		screen_print_player.setText("?????????");

		screen = new Composite(group_screen, SWT.NONE | SWT.EMBEDDED);
		screen.addMouseWheelListener(event_screen_wheel);
		screen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));

		screen_menu = new Menu(screen);
		screen.setMenu(screen_menu);

		frame = SWT_AWT.new_Frame(screen);


		Group group_1 = new Group(this, SWT.NONE);
		group_1.setText("?????????");
		GridLayout gl_group_1 = new GridLayout(3, false);
		gl_group_1.verticalSpacing = 1;
		gl_group_1.marginHeight = 0;
		gl_group_1.marginWidth = 0;
		group_1.setLayout(gl_group_1);
		GridData gd_group_1 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_group_1.widthHint = 200;
		group_1.setLayoutData(gd_group_1);

		Label lblMap = new Label(group_1, SWT.NONE);
		lblMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap.setText("map");

		list_map = new Combo(group_1, SWT.READ_ONLY);
		list_map.addSelectionListener(event_list_map_select);
		list_map.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblPlayer = new Label(group_1, SWT.NONE);
		lblPlayer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlayer.setText("player");

		list_player = new Combo(group_1, SWT.READ_ONLY);
		list_player.addMouseListener(event_list_player);
		list_player.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnFocus = new Button(group_1, SWT.NONE);
		btnFocus.addSelectionListener(event_list_player_focus);
		btnFocus.setText("focus");

		Group group = new Group(group_1, SWT.NONE);
		group.setText("?????? ??????");
		GridLayout gl_group = new GridLayout(5, false);
		gl_group.verticalSpacing = 1;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		Label lblName = new Label(group, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("name");

		hitObjectName = new Text(group, SWT.BORDER);
		hitObjectName.setEnabled(false);
		hitObjectName.setEditable(false);
		hitObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Label lblLevel = new Label(group, SWT.NONE);
		lblLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLevel.setText("level");

		hitObjectLevel = new Text(group, SWT.BORDER);
		hitObjectLevel.setEditable(false);
		hitObjectLevel.setEnabled(false);
		GridData gd_hitObjectLevel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_hitObjectLevel.widthHint = 22;
		hitObjectLevel.setLayoutData(gd_hitObjectLevel);

		Composite composite_1 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblExp = new Label(composite_1, SWT.NONE);
		lblExp.setText("exp");

		hitObjectExp = new Text(composite_1, SWT.BORDER);
		hitObjectExp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		hitObjectExp.setEditable(false);
		hitObjectExp.setEnabled(false);

		Label lblHp = new Label(group, SWT.NONE);
		lblHp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHp.setText("hp");

		hitObjectHp = new Text(group, SWT.BORDER);
		hitObjectHp.setEditable(false);
		hitObjectHp.setEnabled(false);
		hitObjectHp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Label lblMp = new Label(group, SWT.NONE);
		lblMp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMp.setText("mp");

		hitObjectMp = new Text(group, SWT.BORDER);
		hitObjectMp.setEditable(false);
		hitObjectMp.setEnabled(false);
		hitObjectMp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		hitObjectLawful = new Label(group, SWT.NONE);
		hitObjectLawful.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hitObjectLawful.setText("Neutral");

		hitObjectLawfulValue = new Text(group, SWT.BORDER);
		hitObjectLawfulValue.setEnabled(false);
		hitObjectLawfulValue.setEditable(false);
		hitObjectLawfulValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Composite composite_2 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(4, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.verticalSpacing = 1;
		composite_2.setLayout(gl_composite_2);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1);
		gd_composite_2.horizontalIndent = 12;
		composite_2.setLayoutData(gd_composite_2);

		Label lblStr = new Label(composite_2, SWT.NONE);
		lblStr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStr.setText("str");

		hitObjectStr = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectStr = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectStr.widthHint = 39;
		hitObjectStr.setLayoutData(gd_hitObjectStr);
		hitObjectStr.setEditable(false);
		hitObjectStr.setEnabled(false);

		Label lblWis = new Label(composite_2, SWT.NONE);
		lblWis.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWis.setText("wis");

		hitObjectWis = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWis = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWis.widthHint = 28;
		hitObjectWis.setLayoutData(gd_hitObjectWis);
		hitObjectWis.setEditable(false);
		hitObjectWis.setEnabled(false);

		Label lblDex = new Label(composite_2, SWT.NONE);
		lblDex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDex.setText("dex");

		hitObjectDex = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectDex = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectDex.widthHint = 26;
		hitObjectDex.setLayoutData(gd_hitObjectDex);
		hitObjectDex.setEditable(false);
		hitObjectDex.setEnabled(false);

		Label lblInt = new Label(composite_2, SWT.NONE);
		lblInt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInt.setText("int");

		hitObjectInt = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectInt = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectInt.widthHint = 33;
		hitObjectInt.setLayoutData(gd_hitObjectInt);
		hitObjectInt.setEditable(false);
		hitObjectInt.setEnabled(false);

		Label lblCon = new Label(composite_2, SWT.NONE);
		lblCon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCon.setText("con");

		hitObjectCon = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectCon = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectCon.widthHint = 35;
		hitObjectCon.setLayoutData(gd_hitObjectCon);
		hitObjectCon.setEditable(false);
		hitObjectCon.setEnabled(false);

		Label lblCha = new Label(composite_2, SWT.NONE);
		lblCha.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCha.setText("cha");

		hitObjectCha = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectCha = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectCha.widthHint = 32;
		hitObjectCha.setLayoutData(gd_hitObjectCha);
		hitObjectCha.setEditable(false);
		hitObjectCha.setEnabled(false);

		Label lblAc = new Label(composite_2, SWT.NONE);
		lblAc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAc.setText("ac");

		hitObjectAc = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectAc = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectAc.widthHint = 36;
		hitObjectAc.setLayoutData(gd_hitObjectAc);
		hitObjectAc.setEnabled(false);
		hitObjectAc.setEditable(false);

		Label lblWeight = new Label(composite_2, SWT.NONE);
		lblWeight.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWeight.setText("weight");

		hitObjectWeight = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWeight = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWeight.widthHint = 29;
		hitObjectWeight.setLayoutData(gd_hitObjectWeight);
		hitObjectWeight.setEnabled(false);
		hitObjectWeight.setEditable(false);

		Label lblEarth = new Label(composite_2, SWT.NONE);
		lblEarth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEarth.setText("earth");

		hitObjectEarth = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectEarth = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectEarth.widthHint = 33;
		hitObjectEarth.setLayoutData(gd_hitObjectEarth);
		hitObjectEarth.setEnabled(false);
		hitObjectEarth.setEditable(false);

		Label lblWater = new Label(composite_2, SWT.NONE);
		lblWater.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWater.setText("water");

		hitObjectWater = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWater = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWater.widthHint = 34;
		hitObjectWater.setLayoutData(gd_hitObjectWater);
		hitObjectWater.setEnabled(false);
		hitObjectWater.setEditable(false);

		Label lblFire = new Label(composite_2, SWT.NONE);
		lblFire.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFire.setText("fire");

		hitObjectFire = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectFire = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectFire.widthHint = 28;
		hitObjectFire.setLayoutData(gd_hitObjectFire);
		hitObjectFire.setEnabled(false);
		hitObjectFire.setEditable(false);

		Label lblWind = new Label(composite_2, SWT.NONE);
		lblWind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWind.setText("wind");

		hitObjectWind = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWind = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWind.widthHint = 32;
		hitObjectWind.setLayoutData(gd_hitObjectWind);
		hitObjectWind.setEnabled(false);
		hitObjectWind.setEditable(false);

		Label lblSp = new Label(composite_2, SWT.NONE);
		lblSp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSp.setText("sp");

		hitObjectSp = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectSp = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectSp.widthHint = 26;
		hitObjectSp.setLayoutData(gd_hitObjectSp);
		hitObjectSp.setEnabled(false);
		hitObjectSp.setEditable(false);

		Label lblMr = new Label(composite_2, SWT.NONE);
		lblMr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMr.setText("mr");

		hitObjectMr = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectMr = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectMr.widthHint = 33;
		hitObjectMr.setLayoutData(gd_hitObjectMr);
		hitObjectMr.setEnabled(false);
		hitObjectMr.setEditable(false);
		// ???????????? ??????.
		frame.createBufferStrategy( 2 );
		strategy = frame.getBufferStrategy();
		// ?????? ??????.
		background_color = new Color(30, 60, 50);
		player_color = new Color(0, 162, 232);
		monster_color = new Color(237, 28, 36);
		npc_color = new Color(34, 177, 76);
		// ????????? ????????? ?????? ??????.
		miniMap_focus_color = Color.PINK;
		// ?????? ????????????.
		strFont = new Font( "Dialog", Font.PLAIN, 25 );
		playerFont = new Font( "Player", Font.BOLD, 12 );
		monsterFont = new Font( "Monster", Font.PLAIN, 12 );
		npcFont = new Font( "Npc", Font.PLAIN, 12 );

		// ????????? ?????? ??????.
		frame.addMouseListener(event_screen_click);
		frame.addMouseMotionListener(event_screen_move);

		// 
		list_all = new ArrayList<object>();

		//
		hitObjectImage = new ImageIcon( "images/029-Emotion01.png" ).getImage();
	}

	/**
	 * ????????? ?????????.
	 *  : GuiMain?????? ?????? ??????????????? ?????? ViewComposite??? ?????? ?????? ?????????.
	 */
	public void start(){
		// ????????? ?????????.
		new Thread(this).start();
	}

	/**
	 * ???????????? ???????????? ?????? ?????? ??????.
	 * @param list_all
	 */
	public void toUpdate(List<object> list_all){
		synchronized (this.list_all) {
			this.list_all.clear();
			this.list_all.addAll(list_all);
		}
	}

	/**
	 * ?????? ?????? ?????? ??????.
	 */
	public void toUpdate(){
		try {
			// ?????? ??????.
			if(list_map.getItemCount() != World.getMapSize()){
				// ?????? ???????????? ?????? ????????????.
				String[] mapl = World.toStringArrayMap();
				// ??????
				for(int i=mapl.length-1 ; i>=0 ; --i){
					for(int j=mapl.length-1 ; j>=0 ; --j){
						if(Integer.valueOf(mapl[i]) > Integer.valueOf(mapl[j])){
							String temp = mapl[i];
							mapl[i] = mapl[j];
							mapl[j] = temp;
						}
					}
				}
				list_map.setItems( mapl );
			}
			// ?????? ????????? ????????? ?????????.
			if(mapImage != null){
				// ?????? ?????? ??????
				EventThread.append( GuiToWorldAllObject.clone(
						EventThread.getPool(GuiToWorldAllObject.class), 
						nowMap, 
						screen_print_item.getSelection(), 
						screen_print_npc.getSelection(), 
						screen_print_monster.getSelection(), 
						screen_print_background.getSelection(), 
						screen_print_player.getSelection(), 
						screen_print_shop.getSelection())
				);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toUpdate()\r\n", ScreenRenderComposite.class.toString());
			lineage.share.System.println(e);
		}
	}

	@Override
	public void run(){
		try {
			Graphics g = null;
			while(true){
				if(GuiMain.getViewComposite().getTabSelectIdx() == 1){
					g = strategy.getDrawGraphics();
	
					// ????????? ???????????? ?????????.
					g.setColor(background_color);
					g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
	
					if(mapImage != null){
						// ??? ?????????
						synchronized (mapImage) {
							g.drawImage(mapImage, map_x, map_y, mapWidth*zoom, mapHeight*zoom, null);
						}
						try {
							// ????????? ????????? ??????.
							toLineageLocation();
							// ?????? ?????? ?????????.
							toStringLocation(g);
							// ?????? ?????????.
							toObject(g);
							// ????????? ???????????? ????????? ??????.
							toHitObjectImage(g);
							// ????????? ????????? ???????????? ??? ?????? ????????????.
							toHitObjectFocus(g);
							// ????????? ??????.
							toMiniMap(g);
						} catch (Exception e) { }
					}
	
					// ????????? ??????.
					g.dispose();
	
					// ????????? ?????????.
					if ( !strategy.contentsLost() )
						strategy.show();
					Toolkit.getDefaultToolkit().sync();
				}
				
				Thread.sleep(FRAME);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : run()\r\n", ScreenRenderComposite.class.toString());
			lineage.share.System.println(e);
		}
	}

	/**
	 * ??? ?????????.
	 * @param map
	 */
	public void printMap(int map, int zoom, int map_x, int map_y){
		try {

			nowMap = World.get_map(map);
			int x = nowMap.locX1;
			int y = nowMap.locY1;
			mapWidth = nowMap.locX2 - x;
			mapHeight = nowMap.locY2 - y;
			mapImage = frame.createImage(mapWidth, mapHeight);
			this.map_x = map_x;
			this.map_y = map_y;
			this.zoom = zoom;

			synchronized (mapImage) {
				Graphics g = mapImage.getGraphics();
				g.setColor(background_color);
				int c = 0;
				for(int i=0 ; i<nowMap.data_size ; ++i){
					if(++c>nowMap.size){
						y+=1;
						x = nowMap.locX1;
						c = 0;
					}
					if(!World.isThroughAttack(x, y+1, nowMap.mapid, 0))
						g.fillRect(x-nowMap.locX1, y-nowMap.locY1, 1, 1);
					x += 1;
				}
				g.dispose();
			}
		} catch (Exception e) { }
	}

	/**
	 * ????????? ?????? ??????.
	 * @param g
	 */
	private void toStringLocation(Graphics g){
		g.setFont( strFont );
		g.setColor( Color.GREEN );
		g.drawString(String.format("x:%d y:%d", lineage_x, lineage_y), 10, 25);
	}

	/**
	 * ??????????????? ????????? ????????? ?????? ??????.
	 */
	private void toLineageLocation(){
		if(nowMap != null){
			lineage_x = nowMap.locX1 + ((cur_x-map_x)/zoom);
			lineage_y = nowMap.locY1 + ((cur_y-map_y)/zoom);
		}
	}

	/**
	 * ????????? ?????????.
	 * @param g
	 */
	private void toMiniMap(Graphics g){
		g.drawImage(mapImage, miniMap_x, miniMap_y, miniMap_width, miniMap_height, null);
		// ???????????? ?????? ?????? ????????? ?????????.
		int focus_x = miniMap_x;
		int focus_y = miniMap_y;
		int focus_width = miniMap_width/zoom;
		int focus_height = miniMap_height/zoom;
		// ??????
		if(zoom > 1){
			// x??? ??????
			focus_x += (map_x/zoom)/(mapWidth/miniMap_width);
			if(focus_x<0){
				// ?????? ?????? ???????????? abs??? ????????? ????????? ??????.
				focus_x = Math.abs(focus_x);
				// ???????????? ?????? ?????????????????? ???????????? ????????????.
				if(focus_x+focus_width > miniMap_x+miniMap_width)
					focus_x = miniMap_x + miniMap_width-focus_width;
			}else{
				// ???????????? ?????????????????? ???????????? ??????.
				focus_x = miniMap_x;
			}
			// y??? ??????.
			focus_y -= (map_y/zoom)/(mapHeight/miniMap_height);
			if(focus_y<miniMap_y){
				// ?????? ?????????????????? ?????? ????????? ??????.
				focus_y = miniMap_y;
			}else{
				// ????????? ?????????????????? ?????? ????????? ??????.
				if(focus_y+focus_height > miniMap_y+miniMap_height)
					focus_y = miniMap_y + miniMap_height-focus_height;
			}
		}
		// ??????.
		g.setColor(miniMap_focus_color);
		g.drawRect(focus_x, focus_y, focus_width, focus_height);
		g.drawRect(focus_x+1, focus_y+1, focus_width-2, focus_height-2);
	}

	/**
	 * ????????? ?????? ?????? ?????????.
	 * @param g
	 */
	private void toObject(Graphics g){
		synchronized (list_all) {
			for(object o : list_all){
				if(o.isWorldDelete()==false){
					String name = o.getName();
					int draw_x = ((o.getX()-nowMap.locX1)*zoom) + map_x;
					int draw_y = ((o.getY()-nowMap.locY1)*zoom) + map_y;
					if(o instanceof PcInstance){
						g.setFont(playerFont);
						g.setColor(player_color);
						draw_x -= (name.length()*playerFont.getSize())/2;
						draw_y += playerFont.getSize()/2;
					}else if(o instanceof MonsterInstance){
						MonsterInstance mon = (MonsterInstance)o;
						g.setFont(monsterFont);
						g.setColor(monster_color);
						if(mon.getMonster() != null)
							name = mon.getMonster().getName();
						draw_x -= (name.length()*monsterFont.getSize())/2;
						draw_y += monsterFont.getSize()/2;
					}else if(o instanceof NpcInstance){
						NpcInstance npc = (NpcInstance)o;
						g.setFont(npcFont);
						g.setColor(npc_color);
						if(npc.getNpc() != null)
							name = npc.getNpc().getName();
						draw_x -= (name.length()*npcFont.getSize())/2;
						draw_y += npcFont.getSize()/2;
					}else if(o instanceof ShopInstance){
						ShopInstance shop = (ShopInstance)o;
						g.setFont(npcFont);
						g.setColor(npc_color);
						if(shop.getNpc() != null)
							name = shop.getNpc().getName();
						draw_x -= (name.length()*npcFont.getSize())/2;
						draw_y += npcFont.getSize()/2;
					}
					g.drawString(name, draw_x, draw_y);
				}
			}
		}
	}

	/**
	 * ????????? ?????????????????? ????????? ??????.
	 * @param g
	 */
	private void toHitObjectImage(Graphics g){
		if(hitObject!=null){
			// ????????? ??????
			int width = 29;
			int height = 35;
			int sx = 462;
			int sy = 255;
			// ?????? ??????
			int draw_x = ((hitObject.getX()-nowMap.locX1)*zoom) + map_x - (width/2);
			int draw_y = ((hitObject.getY()-nowMap.locY1)*zoom) + map_y - height;
			g.drawImage(hitObjectImage, draw_x, draw_y, draw_x+width, draw_y+height, sx, sy, sx+width, sy+height, null);

			// ????????? ????????? ??????????????? ?????? ???????????? ??? ????????????.
			if(hitObject.getMap()!=nowMap.mapid){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						if(hitObject != null){
							try {
								list_map.select( list_map.indexOf(String.valueOf(hitObject.getMap())) );
								printMap(hitObject.getMap(), zoom, map_x, map_y);
							} catch (Exception e) { }
						}
					}
				});
			}
			// ???????????? ???????????????????????? ?????? ????????????.
			if(hitObject.isWorldDelete())
				hitObject = null;
		}
	}

	/**
	 * ????????? ????????? ???????????? ??? ???????????? ?????????.
	 * @param g
	 */
	private void toHitObjectFocus(Graphics g){
		if(hitObject != null){
			// ?????? ??????
			int x = hitObject.getX() - nowMap.locX1;
			int y = hitObject.getY() - nowMap.locY1;
			// ?????? ?????? ????????? ??????
			int screen_x = ((frame.getWidth()/2)-map_x)/zoom;
			int screen_y = ((frame.getHeight()/2)-map_y)/zoom;
			// ????????? ????????? ?????????.
			x = (screen_x-x) * zoom;
			y = (screen_y-y) * zoom;
			// ????????? ?????? ??? ??????.
			map_x = x+map_x;
			map_y = y+map_y;
		}
	}

	/**
	 * ?????? ??????????????? ????????? ?????????????????? ??????????????? ??????.
	 * @return
	 */
	private object searchOverObject(){
		for(object o : list_all){
			if(o.isWorldDelete() || o.getName()==null)
				continue;

			int dynamicX = o.getX() - nowMap.locX1;
			int dynamicY = o.getY() - nowMap.locY1;
			int draw_x = (dynamicX*zoom) - ((o.getName().length()*12)/2) + map_x;
			int draw_y = (dynamicY*zoom) + (12/2) + map_y;
			int x1 = draw_x - zoom;
			int x2 = draw_x + o.getName().length()*12 + zoom;
			int y1 = draw_y - 12 - zoom;
			int y2 = draw_y + zoom;
			if(cur_x>=x1 && cur_x<=x2 && cur_y>=y1 && cur_y<=y2)
				return o;
		}
		return null;
	}

	// ????????? ????????? ????????? ??????????????? ??????????????? ???????????? ???????????? ??????.
	private MouseAdapter event_screen_click = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(mapImage == null)
				return;

			// ????????? ???????????? ?????? ??????
			if(e.getButton() == MouseEvent.BUTTON3){
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						// ?????? ?????? ??? ??????.
						for(MenuItem mi : screen_menu.getItems())
							mi.dispose();
						// ?????? ??????.
						if(hitObject != null){
							if(hitObject instanceof PcInstance){
								new MenuItem(screen_menu, SWT.NONE).setText("??????");
								new MenuItem(screen_menu, SWT.NONE).setText("?????? ??????");
								new MenuItem(screen_menu, SWT.NONE).setText("??????");
								new MenuItem(screen_menu, SWT.SEPARATOR);
								// ????????????
								new MenuItem(screen_menu, SWT.NONE).setText("????????????");

								screen_menu.getItem(0).addSelectionListener( screen_menu_pc_1 );
								screen_menu.getItem(1).addSelectionListener( screen_menu_pc_2 );
								screen_menu.getItem(2).addSelectionListener( screen_menu_pc_3 );
								screen_menu.getItem(4).addSelectionListener( screen_menu_pc_4 );
							}
							if(hitObject instanceof MonsterInstance){
								// ???????????? ??????
								// ???????????? ??????
							}
							if(hitObject instanceof ShopInstance){
								new MenuItem(screen_menu, SWT.NONE).setText("?????? ??????");

								screen_menu.getItem(0).addSelectionListener( screen_menu_shop_1 );
							}
						}else{
							new MenuItem(screen_menu, SWT.NONE).setText("????????? ????????????");
							new MenuItem(screen_menu, SWT.SEPARATOR);
							new MenuItem(screen_menu, SWT.NONE).setText("????????? ??????");
							new MenuItem(screen_menu, SWT.NONE).setText("????????? ??????");

							screen_menu.getItem(0).addSelectionListener( screen_menu_1 );
							screen_menu.getItem(2).addSelectionListener( screen_menu_2 );
							screen_menu.getItem(3).addSelectionListener( screen_menu_3 );
						}
						// ?????? ??????.
						screen_menu.setVisible(true);
					}
				});
				return;
			}

			//
			cur_point_x = e.getX();
			cur_point_y = e.getY();
			map_dynamic_x = map_x;
			map_dynamic_y = map_y;

			// ?????? ?????? ????????? ?????? ??????.
			hitObject = searchOverObject();
			if(hitObject != null){
				// ??????.
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						String name = hitObject.getName();
						double exp = 0;
						int nowHp=0;
						int nowMp=0;
						int totalHp=0;
						int totalMp=0;
						int _str=0;
						int _dex=0;
						int _con=0;
						int _wis=0;
						int _int=0;
						int _cha = 0;
						int ac = 0;
						int weight = 0;
						int earth = 0;
						int water = 0;
						int wind = 0;
						int fire = 0;
						int sp = 0;
						int mr = 0;
						String lawful = "Lawful";
						// ??????.
						if(hitObject instanceof NpcInstance){
							NpcInstance npc = (NpcInstance)hitObject;
							if(npc.getNpc() != null)
								name = npc.getNpc().getName();
							else
								name = npc.getName();
						}else if(hitObject instanceof MonsterInstance){
							MonsterInstance mon = (MonsterInstance)hitObject;
							if(mon.getMonster() != null)
								name = mon.getMonster().getName();
							else
								name = mon.getName();
						}else if(hitObject instanceof PcInstance){
							PcInstance pc = (PcInstance)hitObject;
						}
						if(hitObject instanceof Character){
							Character cha = (Character)hitObject;
							exp = cha.getExp();
							nowHp = cha.getNowHp();
							nowMp = cha.getNowMp();
							totalHp = cha.getTotalHp();
							totalMp = cha.getTotalMp();
							_str = cha.getStr();
							_dex = cha.getDex();
							_con = cha.getCon();
							_wis = cha.getWis();
							_int = cha.getInt();
							_cha = cha.getCha();
							ac = 10-cha.getTotalAc();
							weight = (int)(((double)cha.getInventory().getWeightPercent()/30)*100);
							earth = cha.getEarthress();
							water = cha.getWaterress();
							fire = cha.getFireress();
							wind = cha.getWindress();
							sp = SkillController.getSp(cha);
							mr = SkillController.getMr(cha, false);
						}
						if(hitObject.getLawful() < Lineage.NEUTRAL)
							lawful = "Chaotic";
						else if(hitObject.getLawful()>=Lineage.NEUTRAL && hitObject.getLawful()<Lineage.NEUTRAL+500)
							lawful = "Neutral";
						// ??????.
						hitObjectName.setText(name);
						hitObjectLevel.setText(String.valueOf(hitObject.getLevel()));
						hitObjectExp.setText(String.valueOf(exp));
						hitObjectHp.setText(String.format("%d/%d", nowHp, totalHp));
						hitObjectMp.setText(String.format("%d/%d", nowMp, totalMp));
						hitObjectStr.setText(String.valueOf(_str));
						hitObjectDex.setText(String.valueOf(_dex));
						hitObjectCon.setText(String.valueOf(_con));
						hitObjectWis.setText(String.valueOf(_wis));
						hitObjectInt.setText(String.valueOf(_int));
						hitObjectCha.setText(String.valueOf(_cha));
						hitObjectAc.setText(String.valueOf(ac));
						hitObjectWeight.setText(String.format("%d%%", weight));
						hitObjectEarth.setText(String.valueOf(earth));
						hitObjectWater.setText(String.valueOf(water));
						hitObjectFire.setText(String.valueOf(fire));
						hitObjectWind.setText(String.valueOf(wind));
						hitObjectSp.setText(String.valueOf(sp));
						hitObjectMr.setText(String.valueOf(mr));
						hitObjectLawful.setText(lawful);
						hitObjectLawfulValue.setText(String.valueOf(hitObject.getLawful()-Lineage.NEUTRAL));
					}
				});
			}
		}
	};

	// ????????? ????????? ??????????????? ???????????? ???????????? ??????.
	private MouseMotionAdapter event_screen_move = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			if(mapImage == null)
				return;
			// ????????? ?????? ?????? ??????.
			cur_x = e.getX();
			cur_y = e.getY();
			// ???????????? ?????????????????? ???????????? ???????????? ??????. ???????????? ?????? ??????.
			GuiMain.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					screen.forceFocus();
				}
			});
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// ?????? ???????????? ???????????? ??????????????????????????? ???????????? ???????????? ??????.
			if(mapImage==null || e.getModifiers()!=16)
				return;

			mouseMoved(e);

			map_x = (cur_x-cur_point_x) + map_dynamic_x;
			map_y = (cur_y-cur_point_y) + map_dynamic_y;
		}
	};

	// ??????????????? ?????? ??? ?????? ???????????? ???????????? ??????.
	private SelectionAdapter event_list_map_select = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				printMap( Integer.valueOf( list_map.getItem( list_map.getSelectionIndex() ) ), 1, 0, 0 );
			} catch (Exception e2) { }
		}
	};

	// ???????????? ????????? ??????.
	private org.eclipse.swt.events.MouseAdapter event_list_player = new org.eclipse.swt.events.MouseAdapter() {
		@Override
		public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
			if(!lineage.Main.running)
				return;

			list_player.removeAll();
			try {
				// ????????? ????????? ?????? ??????.
				int pc_size = World.getPcSize();
				if(pc_size > 0){
					String list_player_item[] = new String[pc_size];
					for(PcInstance pc : World.getPcList())
						list_player_item[--pc_size] = pc.getName();
					list_player.setItems(list_player_item);
				}
			} catch (Exception e2) {
				list_player.removeAll();
			}
		}
	};

	// ????????? focus?????? ????????? ???????????? ??????.
	private SelectionAdapter event_list_player_focus = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			int idx = list_player.getSelectionIndex();
			if(idx >= 0){
				String name = list_player.getItem( idx );
				if(name!=null){
					PcInstance pc = World.findPc(name);
					if(pc != null){
						hitObject = pc;
						if(nowMap == null)
							printMap( hitObject.getMap(), 10, 0, 0 );
					}
				}
			}
		}
	};

	// ??????????????? ???????????? ????????? ???????????? ???????????? ??????.
	private MouseWheelListener event_screen_wheel = new MouseWheelListener() {
		@Override
		public void mouseScrolled(org.eclipse.swt.events.MouseEvent e) {
			// ?????? ??? ???????????? ??????.
			final boolean zoom_in = e.count > 0;
			int new_zoom = zoom_in ? zoom+1 : zoom-1;
			if(new_zoom<=0)
				new_zoom = 1;
			if(new_zoom>20)
				new_zoom = 20;
			if(new_zoom != zoom){
				// ????????? ?????? ?????? ??????????????? ????????? ?????? ??????.
				int x = ((frame.getWidth()/2)-map_x)/zoom;
				int y = ((frame.getHeight()/2)-map_y)/zoom;
				// ??? ??????.
				zoom = new_zoom;
				// ???????????? ????????? ????????? ??????.
				int new_x = ((frame.getWidth()/2)-map_x)/zoom;
				int new_y = ((frame.getHeight()/2)-map_y)/zoom;
				// ?????? ????????? ?????? ??????
				x = (new_x-x) * zoom;
				y = (new_y-y) * zoom;
				// ????????? ????????? ????????? ?????????.
				map_x = x+map_x;
				map_y = y+map_y;
				lineage.share.System.println(zoom_in ? String.format("?????? : zoom(%d)", zoom) : String.format("?????? : zoom(%d)", zoom));
			}
		}
	};

	// ????????? ????????? ???????????? ?????? : ????????? ????????????
	private SelectionAdapter screen_menu_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			PlayerTeleport.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// ????????? ????????? ???????????? ?????? : ????????? ??????
	private SelectionAdapter screen_menu_2 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			MonsterSpawn.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// ????????? ????????? ???????????? ?????? : ????????? ??????
	private SelectionAdapter screen_menu_3 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			NpcSpawn.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// ?????? ????????? ???????????? ?????? : ?????? ??????
	private SelectionAdapter screen_menu_shop_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof ShopInstance){
				ShopInstance shop = (ShopInstance)hitObject;
				if(shop.getNpc() != null)
					ShopEditor.open(shop.getNpc());
			}
		}
	};

	private SelectionAdapter screen_menu_pc_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText( "??????" );
				messageBox.setMessage("?????? ???????????? ???????????????????");
				if(messageBox.open() == SWT.YES)
					CommandController.toBan(null, new StringTokenizer(hitObject.getName()));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_2 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText( "??????" );
				messageBox.setMessage("?????? 5?????? ???????????????????");
				if(messageBox.open() == SWT.YES)
					CommandController.toChattingClose(null, new StringTokenizer(String.format("%s %d", hitObject.getName(), 5)));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_3 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				CommandController.toBuff(null, new StringTokenizer(hitObject.getName()));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_4 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance)
				PlayerInventory.open( (PcInstance)hitObject );
		}
	};

}
