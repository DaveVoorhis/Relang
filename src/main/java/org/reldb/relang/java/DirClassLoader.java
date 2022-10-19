/*
 * DirClassLoader.java
 *
 * Created on 21 August 2004, 21:02
 */

package org.reldb.relang.java;

import java.io.*;
import java.util.*;

import org.reldb.relang.exceptions.ExceptionFatal;

/**
 * A class loader to load named classes from a specified directory.  With
 * class unload and class caching.
 */
public class DirClassLoader extends ClassLoader {

	private static final HashMap<String, Class<?>> loaded = new HashMap<>();

	private final String dir;

	public DirClassLoader(String dir) {
		this.dir = dir;
	}
	
	/** Unload a given Class. */
	public void unload(String name) {
		loaded.remove(name);
	}

	public Class<?> findClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException cnfe) {
			var clazz = (Class<?>) loaded.get(name);
			if (clazz == null) {
				var bytes = loadClassData(name);
				clazz = defineClass(name, bytes, 0, bytes.length);
				loaded.put(name, clazz);
			}
			return clazz;
		}
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) {
		var clazz = findClass(name);
		if (resolve)
			resolveClass(clazz);
		return clazz;
	}

	private File getClassFileName(String name) {
		name = name.replace('.', File.separatorChar);
		if (dir.endsWith(File.separator))
			return new File(dir + name + ".class");
		else
			return new File(dir + File.separator + name + ".class");
	}
	
	private byte[] loadClassData(String name) {
		var file = getClassFileName(name);
		var bytes = new BytestreamOutputArray();
		try {
			var reader = new FileInputStream(file);
			var buffer = new byte[65535];
			while (true) {
				var read = reader.read(buffer);
				if (read < 0)
					break;
				bytes.put(buffer, 0, read);
			}
			reader.close();
		} catch (FileNotFoundException fnfe) {
			throw new ExceptionFatal("RS0290: File " + file + " not found for " + name);
		} catch (IOException ioe) {
			throw new ExceptionFatal("RS0291: Error reading " + file + ": " + ioe);
		}
		return bytes.getBytes();
	}		
	
	/** Get Class for given name.  Will check the system loader first, then the specified directory. */
	public Class<?> forName(final String name) throws ClassNotFoundException {
		// Creation of new ClassLoader allows same class name to be reloaded, as when user
		// drops and then re-creates a given user-defined Java-based type.
		return new DirClassLoader(dir).loadClass(name);
	}

}
