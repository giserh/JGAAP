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
/**
 **/
package com.jgaap.backend;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.jgaap.generics.*;
import com.jgaap.JGAAPConstants;

/**
 * This class dynamically locates subclasses of a given named superclass within
 * a specific directory. You can use it, for example, to find all (e.g.)
 * Preprocessors and populate the GUI with them automatically, eliminating the
 * need to recompile JGAAP every time you add a new canonicizer.
 * 
 * @author Juola/Noecker
 * @version 4.0
 */
public class AutoPopulate {

	private static final List<Canonicizer> CANONICIZERS = Collections.unmodifiableList(loadCanonicizers());
	private static final List<EventDriver> EVENT_DRIVERS = Collections.unmodifiableList(loadEventDrivers());
	private static final List<EventCuller> EVENT_CULLERS = Collections.unmodifiableList(loadEventCullers());
	private static final List<DistanceFunction> DISTANCE_FUNCTIONS = Collections.unmodifiableList(loadDistanceFunctions());
	private static final List<AnalysisDriver> ANALYSIS_DRIVERS = Collections.unmodifiableList(loadAnalysisDrivers());
	private static final List<Language> LANGUAGES = Collections.unmodifiableList(loadLanguages());

	/**
	 * Search named directory for all instantiations of the type named.
	 * 
	 * NOTE: This only works if the classes are part of a package beginning with
	 * com.jgaap
	 * 
	 * @param directory
	 *            The directory to search for the implementing classes of the super class
	 * @param theclass
	 *            The (super)class for finding all subclasses of
	 * @return A List containing instantiations of all classes that are
	 *         subclasses of 'theclass'.
	 */
	private static List<Object> findAll(String directory, String theclass) {
		List<Object> list = new ArrayList<Object>();
		Class<?> thingy = null;
		try {
			thingy = Class.forName(theclass);
		} catch (Exception e) {
			if (JGAAPConstants.JGAAP_DEBUG_VERBOSITY)
				System.out.println("Error: problem instantiating " + theclass
						+ " (" + e.getClass().getName() + ")");
		}

		String[] children = null;
		if (JGAAPConstants.JGAAP_PACKAGE_JAR) {
			try {
				children = getResourceListing(directory+"/");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			File dir = new File(JGAAPConstants.JGAAP_BINDIR + directory);
			children = dir.list();
		}
		if (children == null) {
			System.err.println("Cannot open " + directory + " for reading");
			return list;
		} else {
			String fulQualName = directory.replace("/", ".");
			for (int i = 0; i < children.length; i++) {
				if (children[i].endsWith(".class")) {
					String s = children[i].substring(0,
							children[i].length() - 6);

					try {
						Object o = Class.forName(fulQualName + "." + s)
								.newInstance();
						if (thingy != null && thingy.isInstance(o)) {
							list.add(o);
						}

					} catch (Exception ex) {
						if (JGAAPConstants.JGAAP_DEBUG_VERBOSITY)
							System.out.println("Error: problem instantiating "
									+ s + " (" + ex.getClass().getName() + ")");
					}
				}
			}
		}
		return list;
	}

	/**
	 * A read-only list of the Canonicizers
	 */
	public static List<Canonicizer> getCanonicizers() {
		return CANONICIZERS;
	}

	private static List<Canonicizer> loadCanonicizers() {
		List<Canonicizer> canonicizers = new ArrayList<Canonicizer>();
		for (Object tmpC : findAll("com/jgaap/canonicizers",
				"com.jgaap.generics.Canonicizer")) {
			Canonicizer canon = (Canonicizer) tmpC;
			canonicizers.add(canon);
		}
		Collections.sort(canonicizers);
		return canonicizers;
	}

	/**
	 * A read-only list of the EventDrivers
	 */
	public static List<EventDriver> getEventDrivers() {
		return EVENT_DRIVERS;
	}

	private static List<EventDriver> loadEventDrivers() {
		List<EventDriver> eventDrivers = new ArrayList<EventDriver>();
		for (Object tmpE : findAll("com/jgaap/eventDrivers",
				"com.jgaap.generics.EventDriver")) {
			EventDriver event = (EventDriver) tmpE;
			eventDrivers.add(event);
		}
		Collections.sort(eventDrivers);
		return eventDrivers;
	}

	/**
	 * A read-only list of the DistanceFunctions
	 */
	public static List<DistanceFunction> getDistanceFunctions() {
		return DISTANCE_FUNCTIONS;
	}

	private static List<DistanceFunction> loadDistanceFunctions() {
		List<DistanceFunction> distances = new ArrayList<DistanceFunction>();
		for (Object tmpD : findAll("com/jgaap/distances",
				"com.jgaap.generics.DistanceFunction")) {
			DistanceFunction method = (DistanceFunction) tmpD;
			distances.add(method);
		}
		Collections.sort(distances);

		return distances;
	}

	/**
	 * A read-only list of the AnalysisDrivers
	 */
	public static List<AnalysisDriver> getAnalysisDrivers() {
		return ANALYSIS_DRIVERS;
	}

	private static List<AnalysisDriver> loadAnalysisDrivers() {
		List<AnalysisDriver> analysisDrivers = new ArrayList<AnalysisDriver>();
		for (Object tmpA : findAll("com/jgaap/classifiers",
				"com.jgaap.generics.AnalysisDriver")) {
			AnalysisDriver method = (AnalysisDriver) tmpA;
			analysisDrivers.add(method);
		}
		Collections.sort(analysisDrivers);
		return analysisDrivers;
	}

	/**
	 * A read-only list of the Languages
	 */
	public static List<Language> getLanguages() {
		return LANGUAGES;
	}

	private static List<Language> loadLanguages() {
		List<Language> languages = new ArrayList<Language>();
		for (Object tmpA : findAll("com/jgaap/languages",
				"com.jgaap.generics.Language")) {
			Language lang = (Language) tmpA;
			languages.add(lang);
		}
		Collections.sort(languages);
		return languages;
	}

	/**
	 * A read-only list of the EventCullers
	 */
	public static List<EventCuller> getEventCullers() {
		return EVENT_CULLERS;
	}

	private static List<EventCuller> loadEventCullers() {
		List<EventCuller> cullers = new ArrayList<EventCuller>();
		for (Object tmpA : findAll("com/jgaap/eventCullers",
				"com.jgaap.generics.EventCuller")) {
			EventCuller lang = (EventCuller) tmpA;
			cullers.add(lang);
		}
		Collections.sort(cullers);
		return cullers;
	}

	/**
	 * A modified version of the following
	 * 
	 * List directory contents for a resource folder. Not recursive. This is
	 * basically a brute-force implementation. Works for regular files and also
	 * JARs.
	 * 
	 * @author Greg Briggs
	 * @param path
	 *            Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private static String[] getResourceListing(String path)
			throws URISyntaxException, IOException {
		Class<?> clazz = com.jgaap.JGAAP.class;
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && (dirURL.getProtocol().equals("file"))) {
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")
				|| dirURL.getProtocol().equals("rsrc")) {
			String jarPath;
			if (dirURL.getProtocol().equals("rsrc")) {
				jarPath = "jgaap.jar";
			} else {
				jarPath = dirURL.getPath().substring(5,
						dirURL.getPath().indexOf("!"));
			}
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries();
			Set<String> result = new HashSet<String>();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						entry = entry.substring(0, checkSubdir);
					}
					if (entry.length() > 0)
						result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}
		throw new UnsupportedOperationException("Cannot list files for URL "
				+ dirURL);
	}
}
