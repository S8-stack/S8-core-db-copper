package com.s8.core.db.copper.branch;

import com.s8.core.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.core.arch.silicon.async.AsyncSiTask;

public abstract class MgBranchOperation {
	
	
	public final MgBranchHandler handler;

	
	
	public final ExceptionMgCallback onFailed;
	
	public MgBranchOperation(MgBranchHandler handler, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}
	
	
	public abstract AsyncSiTask createTask();
	

	
}
