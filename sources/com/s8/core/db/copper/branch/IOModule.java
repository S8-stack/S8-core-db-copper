package com.s8.core.db.copper.branch;

import java.io.IOException;
import java.nio.file.Files;

import com.s8.api.bytes.ByteInflow;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.branch.endpoint.NdInbound;
import com.s8.core.bohr.neodymium.branch.endpoint.NdOutbound;
import com.s8.core.db.copper.io.RepoStore;
import com.s8.core.io.bytes.linked.LinkedByteInflow;
import com.s8.core.io.bytes.linked.LinkedByteOutflow;
import com.s8.core.io.bytes.linked.LinkedBytes;
import com.s8.core.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 */
public class IOModule implements H3MgIOModule<NdBranch> {

	
	public final MgBranchHandler handler;
	
	
	/**
	 * 
	 * @param handler
	 */
	public IOModule(MgBranchHandler handler) {
		super();
		this.handler = handler;
	}
	

	
}
