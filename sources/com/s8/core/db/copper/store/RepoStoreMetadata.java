package com.s8.core.db.copper.store;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;



@JSON_Type(name = "RepositoryStore")
public class RepoStoreMetadata {


	@JSON_Field(name = "rootPathname") 
	public String rootPathname;


}
