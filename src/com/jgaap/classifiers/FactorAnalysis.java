package com.jgaap.classifiers;

import java.util.ArrayList;
import java.util.Collections;
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

public class FactorAnalysis extends AnalysisDriver {

	public FactorAnalysis() {
	}

	@Override
	public String displayName() {
		return "Personality Factor Analysis";
	}

	@Override
	public String tooltipText() {
		return "Personality Factor Analysis";
	}

	@Override
	public boolean showInGUI() {
		return true;
	}

	public void train(List<EventSet> knowns) {

	}

	public List<Pair<String, Double>> analyze(EventSet unknown) {

	}
}
