package com.s8.core.db.copper.demos;

import com.s8.api.annotations.S8Field;
import com.s8.api.annotations.S8ObjectType;
import com.s8.api.flow.repository.objects.RepoS8Object;


@S8ObjectType(name = "StubProject")
public class StubProject extends RepoS8Object {

	
	public @S8Field(name = "name") String name;
	
	public @S8Field(name = "index") int index;

	public @S8Field(name = "factor") double a;

	
}
