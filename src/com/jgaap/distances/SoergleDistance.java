package com.jgaap.distances;

import java.util.HashSet;
import java.util.Set;

import com.jgaap.generics.DistanceCalculationException;
import com.jgaap.generics.DistanceFunction;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventHistogram;
import com.jgaap.generics.EventSet;

/**
 * Soergle Distance
 * d = sum( abs(xi - yi) ) / sum( max(xi, yi) )
 * 
 * @author Adam Sargent
 * @version 1.0
 */

public class SoergleDistance extends DistanceFunction {

	@Override
	public String displayName() {
		return "Soergle Distance";
	}

	@Override
	public String tooltipText() {
		return "Soergle Distance";
	}

	@Override
	public boolean showInGUI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distance(EventSet unknownEventSet, EventSet knownEventSet)
			throws DistanceCalculationException {
		EventHistogram unknownHistogram = new EventHistogram(unknownEventSet);
		EventHistogram knownHistogram = new EventHistogram(knownEventSet);

		Set<Event> events = new HashSet<Event>();
		events.addAll(unknownHistogram.events());
		events.addAll(knownHistogram.events());

		double distance = 0.0, sumNumer = 0.0, sumDenom = 0.0;
		
		for(Event event : events){
			sumNumer += Math.abs(unknownHistogram.getRelativeFrequency(event) - knownHistogram.getRelativeFrequency(event));
			sumDenom += Math.max(unknownHistogram.getRelativeFrequency(event), knownHistogram.getRelativeFrequency(event));
		}
		distance = sumNumer / sumDenom;
		
		return distance;
	}

}
