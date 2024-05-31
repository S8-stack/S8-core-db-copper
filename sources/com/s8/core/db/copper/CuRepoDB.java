package com.s8.core.db.copper;

import java.nio.file.Path;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.titanium.db.MgDbSwitcher;
import com.s8.core.arch.titanium.db.MgIOModule;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.io.IOModule;
import com.s8.core.db.copper.io.PathComposer;
import com.s8.core.db.copper.operators.CloneBranchOp;
import com.s8.core.db.copper.operators.CommitBranchOp;
import com.s8.core.db.copper.operators.CreateRepositoryOp;
import com.s8.core.db.copper.operators.ForkBranchOp;
import com.s8.core.db.copper.operators.ForkRepositoryOp;
import com.s8.core.db.copper.operators.GetBranchMetadataOp;
import com.s8.core.db.copper.operators.GetRepoMetadataOp;
import com.s8.core.io.json.types.JSON_CompilingException;


/**
 * 
 * @author pc
 *
 */
public class CuRepoDB extends MgDbSwitcher<NdRepository> {


	public final IOModule ioModule;
	
	
	/**
	 * 
	 * @param ng
	 * @param codebase
	 * @param rootFolderPath
	 * @param isSaved
	 * @throws JSON_CompilingException
	 */
	public CuRepoDB(SiliconEngine ng, NdCodebase codebase, Path rootFolderPath) throws JSON_CompilingException {
		super(ng, new PathComposer(rootFolderPath));
		this.ioModule = new IOModule(codebase);
	}






	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void createRepository(long t, S8User initiator, SiliconChainCallback callback, CreateRepositoryS8Request request) {
		new CreateRepositoryOp(this, t, initiator, callback, request).process();
	}



	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepository(long t, S8User initiator, SiliconChainCallback callback, ForkRepositoryS8Request request) {
		new ForkRepositoryOp(this, t, initiator, callback, request).process();
	}



	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, SiliconChainCallback callback, ForkBranchS8Request request) {
		new ForkBranchOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, SiliconChainCallback callback, CommitBranchS8Request request) {
		new CommitBranchOp(this, t, initiator, callback, request).process();
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, SiliconChainCallback callback, CloneBranchS8Request request) {
		new CloneBranchOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetRepositoryMetadataS8Request request) {
		new GetRepoMetadataOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getBranchMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetBranchMetadataS8Request request) {
		new GetBranchMetadataOp(this, t, initiator, callback, request).process();
	}






	@Override
	public MgIOModule<NdRepository> getIOModule() {
		return ioModule;
	}


}
