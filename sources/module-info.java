/**
 * 
 */
/**
 * 
 */
module com.s8.core.db.copper {
	
	
	exports com.s8.core.db.copper;
	exports com.s8.core.db.copper.branch;
	exports com.s8.core.db.copper.entry;
	exports com.s8.core.db.copper.io;
	

	exports com.s8.core.db.copper.demos;
	
	
	requires transitive com.s8.api;
	requires transitive com.s8.core.arch.silicon;
	requires transitive com.s8.core.arch.magnesium;
	requires transitive com.s8.core.bohr.neodymium;
	requires transitive com.s8.core.io.xml;
	requires transitive com.s8.core.io.json;
	
	
}