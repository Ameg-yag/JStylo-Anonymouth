package edu.drexel.psal.jstylo.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class ClassWizardDriver {

	public static void initListeners(final ClassWizard cw){
		
		//Apply changes
		cw.applyJButton.addActionListener(new ActionListener(){

			String[] newOptions = new String [cw.optionFields.size()*2];
			String[] oldOptions = cw.tmpAnalyzer.getOptions(); //used to get flags
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i=0;
				for (JTextField input : cw.optionFields){
					newOptions[i]=oldOptions[i];
					i++;
					newOptions[i]=input.getText();
					i++;
				}
				
				cw.tmpAnalyzer.setOptions(newOptions);
				cw.parent.classAvClassArgsJTextField.setText(edu.drexel.psal.jstylo.GUI.ClassTabDriver.getOptionsStr(cw.tmpAnalyzer.getOptions()));
				cw.dispose();
			}		
		});
		
		//Cancel changes
		cw.cancelJButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cw.dispose();
			}
		});
	}
	
}
