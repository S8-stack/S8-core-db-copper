package com.s8.core.db.copper.entry;

import com.s8.api.flow.repository.objects.S8BranchMetadata;
import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JSON_Type(name = "MgBranchMetadata")
public class MgBranchMetadata implements S8BranchMetadata {


	@JSON_Field(name = "id")
	public String name;


	@JSON_Field(name = "info")
	public String info;


	@JSON_Field(name = "headVersion")
	public long headVersion;


	@JSON_Field(name = "forkId")
	public String forkedBranchId;


	@JSON_Field(name = "forkVersion")
	public long forkedBranchVersion;


	@JSON_Field(name = "owner")
	public String owner;

	/*
	@JOOS_Field(name = "commits")
	public List<MgBranchCommitInfo> commits;
	 */



	/**
	 * 
	 * @return
	 */
	public MgBranchMetadata deepClone() {
		MgBranchMetadata clone = new MgBranchMetadata();
		clone.name = name;
		clone.info = info;
		clone.headVersion = headVersion;
		clone.forkedBranchId = forkedBranchId;
		clone.forkedBranchVersion = forkedBranchVersion;
		clone.owner = owner;
		return clone;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public long getHeadVersion() {
		return headVersion;
	}

	@Override
	public String getForkedBranchId() {
		return forkedBranchId;
	}

	@Override
	public long getForkedBranchVersion() {
		return forkedBranchVersion;
	}

	@Override
	public String getOwner() {
		return owner;
	}


}
