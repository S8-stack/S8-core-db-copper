package com.s8.core.db.copper.demos.paths;

import java.nio.file.Path;

import com.s8.core.db.copper.store.MgPathComposer;

public class PathComposerDemo00 {

	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		MgPathComposer composer = new MgPathComposer(Path.of("output"));
		
		
		String repo = "https://toto.com/orc/project-sinchuan~023_Rf234.fld#1";
		System.out.println(composer.composePath(repo));
		
		
		repo = "https:/toto.com/orc/project-sinchuan~023_Rf234.fld#1";
		System.out.println(composer.composePath(repo));
		
	}

}
