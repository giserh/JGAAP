package com.jgaap.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jgaap.backend.Utils;
import com.jgaap.generics.AnalysisDriver;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventHistogram;
import com.jgaap.generics.EventSet;
import com.jgaap.generics.Pair;

/**
 * NOTE: The N-1 factor in stddev makes the results here different than most 
 * online examples.
 * @author jnoecker
 *
 */
public class PearsonFactorAnalysis extends AnalysisDriver {

	private List<Pair<String, Double>> correlations;
	
	public PearsonFactorAnalysis() {
		correlations = new ArrayList<Pair<String, Double>>();
	}

	@Override
	public String displayName() {
		return "Personality Pearson Factor Analysis";
	}

	@Override
	public String tooltipText() {
		return "Personality Pearson Factor Analysis";
	}

	@Override
	public boolean showInGUI() {
		return true;
	}

	public void train(List<EventSet> knowns) {

		List<Double> scores = new ArrayList<Double>();
		Set<Event> allEvents = new HashSet<Event>();
		List<EventHistogram> histograms = new ArrayList<EventHistogram>();
		
		
		for(EventSet es : knowns) {
			// Construct a list of all events being considered
			for(Event e : es) {
				allEvents.add(e);
			}
			
			String author = es.getAuthor();
			scores.add(Double.parseDouble(author));
			histograms.add(es.getHistogram());
		}
		
		double scoresStdDev = com.jgaap.backend.Utils.stddev(scores);
		double scoresMean = com.jgaap.backend.Utils.mean(scores);
		
		for(Event e : allEvents) {
			List<Double> eventUsage = new ArrayList<Double>();
			for(EventHistogram h : histograms) {
				eventUsage.add(new Double(h.getNormalizedFrequency(e)));
			}
			
			double usageMean = com.jgaap.backend.Utils.mean(eventUsage);
			double usageStdDev = com.jgaap.backend.Utils.stddev(eventUsage);
			List<Double> numerators = new ArrayList<Double>();
			
			for(int i = 0; i < knowns.size(); i++) {
				double first = histograms.get(i).getNormalizedFrequency(e) - usageMean;
				double second = scores.get(i) - scoresMean;
				numerators.add(first * second);
			}
			
			double corr = com.jgaap.backend.Utils.mean(numerators);
			corr = corr / (usageStdDev * scoresStdDev);
			correlations.add(new Pair<String, Double>(e.getEvent(), corr, new absSort()));
			
		}
		

		Collections.sort(correlations);
		Collections.reverse(correlations);
		
		for(Pair<String, Double> p : correlations) {
			System.out.println(p.getFirst() + "\t" + p.getSecond());
		}
		
	}

	/**
	 * Return nothing, since everything is done in the train phase
	 */
	public List<Pair<String, Double>> analyze(EventSet unknown) {
		return null;
	
	}
	
    private class absSort implements Comparator<Pair<String,Double> >  {

        public int compare(Pair<String, Double> x, Pair<String, Double> y) {
            if(x.getSecond() != null) {

                return ((Double)Math.abs(x.getSecond())).compareTo(Math.abs(y.getSecond()));
            }
            System.err.println("Null pointer in pair second element.\n");
            return Integer.MAX_VALUE;
        }
    }
}
