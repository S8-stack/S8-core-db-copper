package com.s8.core.db.copper.branch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.db.copper.entry.MgBranchMetadata;
import com.s8.core.db.copper.entry.MgRepository;
import com.s8.core.db.copper.store.RepoMgStore;

/**
 * 
 * @author pierreconvert
 *
 */

public class MgBranchHandler extends H3MgHandler<NdBranch> {

	public final static String BRANCH_DATA_FILENAME = "branch-data.nd";

	public MgBranchMetadata metadata;
	
	
	

	
	
	public String getIdentifier() {
		return metadata.name;
	}
	
	
	public long getVersion() {
		return metadata.headVersion;
	}
	

	@Override
	public String getName() {
		return metadata.name;
	}


	
	public final static String DEFAULT_BRANCH_NAME = "prime";

	
	/*
	public static MgBranchHandler create(SiliconEngine ng, MgRepoStore store, MgRepository repository, String name) {

		String id = DEFAULT_BRANCH_NAME;
		
		MgBranchHandler branchHandler = new MgBranchHandler(ng, store, repository);
	
		NdCodebase codebase = store.getCodebase();
		
		branchHandler.id = id;
		branchHandler.name = name;

		branchHandler.setLoaded(new NdBranch(codebase, id));
		branchHandler.save();

		return branchHandler;
	}
	*/
	


	public final RepoMgStore store;

	public final MgRepository repository;


	private final H3MgIOModule<NdBranch> ioModule = new IOModule(this);

	public MgBranchHandler(SiliconEngine ng, RepoMgStore store, MgRepository repository, MgBranchMetadata metadata, boolean isSaved) {
		super(ng, isSaved);
		this.store = store;
		this.repository = repository;
		this.metadata = metadata;
	}



	/**
	 * 
	 * @return
	 */
	public RepoMgStore getStore() {
		return store;
	}

	

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public <T extends RepoS8Object> void commitBranch(long t, S8User initiator, SiliconChainCallback callback, 
			CommitBranchS8Request request) {
		pushOpLast(new CommitBranchOp(t, initiator, callback, this, request));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, SiliconChainCallback callback, CloneBranchS8Request request) {
		pushOpLast(new CloneBranchOp(t, initiator, callback, this, request));
	}
	
	
	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, SiliconChainCallback callback,  MgBranchHandler target, ForkBranchS8Request request) {
		pushOpLast(new ForkBranchOp(t, initiator, callback, this, target, request));
	}
	
	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepo(long t, S8User initiator, SiliconChainCallback callback, 
			MgBranchHandler target, ForkRepositoryS8Request request) {
		pushOpLast(new ForkRepositoryOp(t, initiator, callback, this, target, request));
	}
	



	/**
	 * 
	 * @return path to repository branch sequence
	 */
	Path getFolderPath() {
		return repository.getFolderPath().resolve(metadata.name);
	}
	
	Path getDataFilePath() {
		return getFolderPath().resolve(BRANCH_DATA_FILENAME);
	}



	@Override
	public H3MgIOModule<NdBranch> getIOModule() {
		return ioModule;
	}


	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		return new ArrayList<>();
	}

}
