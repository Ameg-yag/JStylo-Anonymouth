/*
 * JGAAP -- a graphical program for stylometric authorship attribution
 * Copyright (C) 2009,2011 by Patrick Juola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jgaap.eventCullers;

import com.jgaap.generics.EventCuller;
import com.jgaap.generics.EventCullingException;
import com.jgaap.generics.EventSet;

import java.util.List;

/**
 * Sort out the N most common events (by average frequency) across all event sets
 */
public class LeastCommonEvents extends EventCuller {

    @Override
    public List<EventSet> cull(List<EventSet> eventSets) throws EventCullingException {

        EventCuller underlyingCuller = new FrequencyRangeCuller();
        underlyingCuller.setParameter("minPos", -1);

        if(getParameter("numEvents").equals("")) {
            underlyingCuller.setParameter("minPos", -50);
            underlyingCuller.setParameter("numEvents", 50);
        }
        else {
            underlyingCuller.setParameter("minPos", -Integer.parseInt(getParameter("numEvents")));
            underlyingCuller.setParameter("numEvents", getParameter("numEvents"));
        }

        return underlyingCuller.cull(eventSets);
    }

    public LeastCommonEvents() {
        super();
        addParams("numEvents", "N", "50", new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "15", "20", "25", "30", "40", "45", "50", "75", "100", "150", "200" }, true);
    }

    @Override
    public String displayName() {
        return "Least Common Events";
    }

    @Override
    public String tooltipText() {
        return "Analyze only the N least common events across all documents";
    }

    @Override
    public String longDescription() {
        return "Analyze only the N rarest events across all documents; " +
               "the value of N is passed as a parameter (numEvents). ";
    }

    @Override
    public boolean showInGUI() {
        return true;
    }
/*
    JLabel NLabel = new JLabel();
    JComboBox NBox = new JComboBox();

    @Override
    public GroupLayout getGUILayout(JPanel panel){

    	NLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    	NLabel.setText("N");

    	NBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "15", "20", "25", "30", "40", "45", "50", "75", "100", "150", "200" }));
        NBox.setEditable(true);
        String temp = this.getParameter("numEvents");
        if (temp.equals(""))
        {
            this.setParameter("numEvents", 50);
            this.setParameter("minPos", -50);
        }
        NBox.setSelectedItem(this.getParameter("numEvents"));
        NBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        layout.setHorizontalGroup(
        		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(NLabel)
                        .addComponent(NBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(275, Short.MAX_VALUE))
            );

        layout.setVerticalGroup(
        		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(NLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(NBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(255, Short.MAX_VALUE))
            );
        return layout;
    }

    private void NBoxActionPerformed(java.awt.event.ActionEvent evt) {
        this.setParameter("numEvents", (String)NBox.getSelectedItem());
        this.setParameter("minPos", -Integer.parseInt((String)NBox.getSelectedItem()));
    }
*/
    @Override
    protected void changeParam(java.awt.event.ActionEvent evt) {
        super.changeParam(evt);
        Integer minPos = -Integer.parseInt(getParameter("numEvents"));
        setParameter("minPos", minPos.toString());
    }
}
