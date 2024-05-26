package com.s8.core.db.copper.operators;

import com.s8.api.flow.S8User;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.db.copper.CuRepoDB;

public class CuDbOperation {


	public final CuRepoDB db;
	
	public final long t;
	
	public S8User initiator;
	
	public SiliconChainCallback callback;
	
	
	/**
	 * 
	 * @param db
	 */
	public CuDbOperation(CuRepoDB db, long t, S8User initiator, SiliconChainCallback callback) {
		super();
		this.db = db;
		this.t = t;
		this.initiator = initiator;
		this.callback = callback;
	}
	
}
