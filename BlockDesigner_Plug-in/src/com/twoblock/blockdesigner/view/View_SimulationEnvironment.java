package com.twoblock.blockdesigner.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.twoblock.blockdesigner.command.Handler_Command;
import com.twoblock.blockdesigner.command.Handler_SimulationInitThread;
import com.twoblock.blockdesigner.command.Hanlder_CallBack;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class View_SimulationEnvironment extends ViewPart {
	protected Shell parent;
	private Text txtStep;
	public static Label lblCyclesCnt;
	private void setViewState(int state) {
		IWorkbenchPage page 
                    = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		int currentState = page.getPartState(page.getReference(this));
		if(currentState != state) {
			page.activate(this);
			page.setPartState(page.getReference(this), state);
		}
	}
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(11, false));
		
		ImageDescriptor idOpen = ImageDescriptor.createFromFile(this.getClass(), "/images/open_btn.png");
		Image imgOpen = idOpen.createImage();
		ImageDescriptor idSave = ImageDescriptor.createFromFile(this.getClass(), "/images/save_btn.png");
		Image imgSave = idSave.createImage();
		ImageDescriptor idRun = ImageDescriptor.createFromFile(this.getClass(), "/images/run_btn.png");
		Image imgRun = idRun.createImage();
		ImageDescriptor idStop = ImageDescriptor.createFromFile(this.getClass(), "/images/stop_btn.png");
		Image imgStop = idStop.createImage();
		ImageDescriptor idStep = ImageDescriptor.createFromFile(this.getClass(), "/images/step_btn.png");
		Image imgStep = idStep.createImage();
		ImageDescriptor idStep_n = ImageDescriptor.createFromFile(this.getClass(), "/images/step_n_btn.png");
		Image imgStep_n = idStep_n.createImage();
		
//		Image imgSave = new Image(null, "images/save_btn.png");
//		Image imgRun = new Image(null, "images/run_btn.png");
//		Image imgStop = new Image(null, "images/stop_btn.png");
//		Image imgStep = new Image(null, "images/step_btn.png");
//		Image imgStep_n = new Image(null, "images/step_n_btn.png");
		
		 
		
		
		Button btnOpen = new Button(parent, SWT.NONE);
		btnOpen.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		btnOpen.setImage(imgOpen);
		
		Button btnSave= new Button(parent, SWT.NONE);
		btnSave.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		btnSave.setImage(imgSave);
		
		Label sepBar1 = new Label(parent, SWT.SEPARATOR | SWT.CENTER);
		sepBar1.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 2));
		
		Button btnRun = new Button(parent, SWT.NONE);
		btnRun.setImage(imgRun);
		
		Button btnStop = new Button(parent, SWT.NONE);
		btnStop.setImage(imgStop);
		
		Button btnStep = new Button(parent, SWT.NONE);
		btnStep.setImage(imgStep);
		
		txtStep = new Text(parent, SWT.BORDER);
		GridData txtStep_n = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		txtStep_n.widthHint = 50;
		txtStep.setLayoutData(txtStep_n);
		
		Button btnStep_n = new Button(parent, SWT.NONE);
		btnStep_n.setImage(imgStep_n);
		
		Label sepBar2 = new Label(parent, SWT.SEPARATOR | SWT.CENTER);
		sepBar2.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 2));
		
		lblCyclesCnt = new Label(parent, SWT.BORDER|SWT.RIGHT);
		GridData gd_label = new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1);
		gd_label.widthHint=200;
		lblCyclesCnt.setLayoutData(gd_label);
		lblCyclesCnt.setText("110254");
//		lblCyclesCnt.setAlignment(SWT.CENTER);
		
		Label lblCycles = new Label(parent, SWT.NONE);
		lblCycles.setAlignment(SWT.CENTER);
		lblCycles.setText("cycles");

		
		Label lblOpen = new Label(parent, SWT.NONE);
		lblOpen.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblOpen.setText("Open");

		Label lblSave = new Label(parent, SWT.NONE);
		lblSave.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblSave.setText("Save");
		
		Label lblRun = new Label(parent, SWT.NONE);
		lblRun.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblRun.setText("Run");
		
		Label lblStop = new Label(parent, SWT.NONE);
		lblStop.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblStop.setText("Stop");
		
		Label lblStep = new Label(parent, SWT.NONE);
		lblStep.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblStep.setText("Step");
		
		
		Label lblStep_n = new Label(parent, SWT.NONE);
		lblStep_n.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblStep_n.setText("n Step");
		
		
		/* START--- Button Listener & Action --- */
		btnOpen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "OPEN", "NULL", "NULL", "NULL", "NULL");
				Handler_SimulationInitThread.SimInitThread_Func();
			}
		});
		btnSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "SAVE", "NULL", "NULL", "NULL", "NULL");
				Hanlder_CallBack.CallBack_Func();
			}
		});
		btnRun.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "RUN", "NULL", "NULL", "NULL", "NULL");
			}
		});
		btnStop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "STOP", "NULL", "NULL", "NULL", "NULL");
			}
		});
		btnStep.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "STEP", "1", "NULL", "NULL", "NULL");
			}
		});
		btnStep_n.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Handler_Command.Command_Func(0, 1, "STEP",txtStep.getText(), "NULL", "NULL", "NULL");
			}
		});
		/* --- Button Listener & Action ---END */
		
		
		Canvas canvas = new Canvas(parent, SWT.NONE);
		canvas.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT)); 
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 11, 1));
	}
	
	/**
	 * @param state  one of the IWorkbenchPage STATE_* values: STATE_MAXIMIZED, 
         * STATE_MINIMIZED, STATE_RESTORED
	 */
	
	public void setFocus() {
		setViewState(1);
	}
}