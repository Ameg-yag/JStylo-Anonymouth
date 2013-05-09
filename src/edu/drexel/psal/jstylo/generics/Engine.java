package edu.drexel.psal.jstylo.generics;

import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.jgaap.generics.Document;
import com.jgaap.generics.EventSet;

public class Engine implements API {

	@Override
	public List<EventSet> extractEventSets(Document document,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception {
		
		return cumulativeFeatureDriver.createEventSets(document);
	}

	@Override
	public List<List<EventSet>> cull(List<List<EventSet>> eventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception {
		
		return CumulativeEventCuller.cull(eventSets,cumulativeFeatureDriver);
	}

	@Override
	public List<EventSet> getRelevantEvents(
			List<List<EventSet>> culledEventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Attribute> getAttributeList(List<List<EventSet>> culledEventSets)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance createInstance(List<Attribute> attributes,
			CumulativeFeatureDriver cumulativeFeatureDriver,
			List<EventSet> documentData, boolean isSparse) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void normInstance(CumulativeFeatureDriver cumulativeFeatureDriver,
			Instance instance) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Integer> calcInfoGain(Instances insts, int N) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyInfoGain(List<Integer> chosenFeatures, Instances insts)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<EventSet> cullWithRespectToTraining(
			List<EventSet> relevantEvents, List<EventSet> eventSetsToCull)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
