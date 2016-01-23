package com.twoblock.blockdesigner.modulewizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Dialog_ParameterInput extends Dialog {
	private static String pa_name;
	private static String pa_value;
	private static String pa_type;
	Double value;
	/**
	 * @param parent
	 */
	public Dialog_ParameterInput(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public Dialog_ParameterInput(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public Double open() {
		final Text name_text;
		final Text value_text;
		Button create_btn;
		Button cancel_btn;
		final Combo type_cmb;
		Label label;
		Label seperator;
		
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment=GridData.BEGINNING;
		gridData.horizontalAlignment=GridData.FILL;
		gridData.verticalAlignment=GridData.FILL;
		gridData.grabExcessHorizontalSpace=true;
		gridData.grabExcessVerticalSpace=true;

		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("Enter Parameter Information");
		shell.setLayout(new GridLayout(1, true));
		shell.setBounds(400, 400, 400, 400);
		
		label = new Label(shell, SWT.LEFT);
		label.setText("1. Enter the Parameter Name.        ");
		label.setLayoutData(gridData);


		name_text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		name_text.setLayoutData(gridData);
		
		seperator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		seperator.setLayoutData(gridData);
		
		label = new Label(shell, SWT.LEFT);
		label.setText("2. Select the Parameter Type.        ");
		type_cmb= new Combo(shell, SWT.DROP_DOWN |SWT.READ_ONLY );
		type_cmb.setItems(new String[]
				{"bool","int","char","bit"});
		type_cmb.setText("bool");
		type_cmb.setLayoutData(gridData);
		
		seperator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		seperator.setLayoutData(gridData);
				
		label = new Label(shell, SWT.LEFT);
		label.setText("3. Enter the Default Value.        ");
		value_text= new Text(shell, SWT.SINGLE | SWT.BORDER);
		value_text.setLayoutData(gridData);
		value_text.setText("true");
		
		// detect register type.
		type_cmb.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (type_cmb.getSelectionIndex() == 0)
					value_text.setText("true");
				else if (type_cmb.getSelectionIndex() == 1)
					value_text.setText("0");
				else if (type_cmb.getSelectionIndex() == 2)
					value_text.setText("0");
				else if (type_cmb.getSelectionIndex() == 3)
					value_text.setText("16");
			}
		});

		create_btn = new Button(shell, SWT.PUSH);
		create_btn.setText("Ok");
		create_btn.setLayoutData(gridData);
		
		cancel_btn = new Button(shell, SWT.PUSH);
		cancel_btn.setText("Cancel");
		cancel_btn.setLayoutData(gridData);

		create_btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				pa_name=name_text.getText();
				pa_value=value_text.getText();
				pa_type=type_cmb.getText();
				shell.dispose();
			}
		});

		cancel_btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				//value = null;
				pa_type="canceled";
				shell.dispose();
			}
		});

		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});

		
		shell.pack();
		shell.getShell().open();
//		shell;
		
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return value;
	}

	public static String main(String[] args) {
		
		String result;
		Shell shell = new Shell();
		Dialog_ParameterInput dialog = new Dialog_ParameterInput(shell);
		System.out.println("dialog.open()="+dialog.open());
		result=(pa_name+","+pa_value+","+pa_type);
		return result;
		
	}
}