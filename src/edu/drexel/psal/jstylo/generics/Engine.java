package edu.drexel.psal.jstylo.generics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.jgaap.generics.Document;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventHistogram;
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
		
		///CONSTRUCTION

		//FIXME this line is incorrect, but is what we are currently using elsewhere.
		//essentially, it uses the first List<EventSet> to determine the size of the relevant EventSet list
		//this shouldn't always be the case: the first list might be missing EventSets which other documents have
		//which are more popular ie a specific word or bigram. Change this once everything else is looking good.
		int numOfFeatureClasses = culledEventSets.get(0).size();
		
		int numOfVectors = culledEventSets.size();
		List<EventSet> list;
		List<EventHistogram> histograms;
		
		// initialize list of lists of histograms
		List<List<EventHistogram>> knownEventHists = new ArrayList<List<EventHistogram>>(numOfVectors);
		for (int i=0; i<numOfVectors; i++)
			knownEventHists.add(new ArrayList<EventHistogram>(numOfFeatureClasses));
		
		// initialize list of sets of events, which will eventually become the attributes
		List<Set<Event>> allEvents = new ArrayList<Set<Event>>(numOfFeatureClasses);
		
		for (int currEventSet=0; currEventSet<numOfFeatureClasses; currEventSet++) {
			// initialize relevant list of event sets and histograms

			list = new ArrayList<EventSet>(numOfVectors);
			for (int i=0; i<numOfVectors; i++)
				list.add(culledEventSets.get(i).get(currEventSet));
			histograms = new ArrayList<EventHistogram>();
			
			Set<Event> events = new HashSet<Event>();
			
			if (cumulativeFeatureDriver.featureDriverAt(currEventSet).isCalcHist()) {	// calculate histogram
			
				// generate event histograms and unique event list
				for (EventSet eventSet : list) {
					EventHistogram currHist = new EventHistogram();
					for (Event event : eventSet) {
						events.add(event);
						currHist.add(event);
					}
					histograms.add(currHist);
					allEvents.add(currEventSet,events);
				}
				
				// update histograms
				for (int i=0; i<numOfVectors; i++)
					knownEventHists.get(i).add(currEventSet,histograms.get(i));
				
			} else {	// one unique numeric event
				
				// generate sole event (extract full event name and remove value)
				Event event = new Event(list.get(0).eventAt(0).getEvent().replaceAll("\\{.*\\}", "{-}"));
				events.add(event);
				allEvents.add(currEventSet,events);
				
				// update histogram to null at current position
				for (int i=0; i<numOfVectors; i++)
					knownEventHists.get(i).add(currEventSet,null);
			}
		}
		
		//END CONSTRUCTION
		
		return null; //TODO not finished
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
