package com.s8.core.db.copper.branch;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;

@JSON_Type(name = "mg-branch-commit-info")
public class MgBranchCommitInfo {
	
	@JSON_Field(name = "user")
	public String userId;

	@JSON_Field(name = "date")
	public long timestamp;

	@JSON_Field(name = "comment")
	public String comment;

}
