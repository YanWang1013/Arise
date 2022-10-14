package lineage.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;

public class SpChattingComposite extends Composite {

	private Text text;
	private StyledText chatting;
	private org.eclipse.swt.graphics.Color color_normal = SWTResourceManager.getColor(255, 251, 255);
	private List list;
	
	public SpChattingComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(16, 16, 16));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		list = new List(this, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
		gd_list.widthHint = 100;
		list.setLayoutData(gd_list);
		
		chatting = new StyledText(this, SWT.BORDER | SWT.V_SCROLL);
		chatting.setBackground(SWTResourceManager.getColor(16, 16, 16));
		chatting.setDoubleClickEnabled(false);
		chatting.setEditable(false);
		chatting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		text = new Text(this, SWT.NONE);
		text.setBackground(SWTResourceManager.getColor(16, 16, 16));
		text.setForeground(color_normal);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
		});
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.horizontalIndent = 10;
		text.setLayoutData(gd_text);
		
		Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 
				text.setText("");
			}
		});
		GridData gd_button = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_button.horizontalIndent = 10;
		gd_button.heightHint = 40;
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("보내기");
	}
}
