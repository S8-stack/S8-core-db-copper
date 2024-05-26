package com.s8.core.db.copper.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.io.RepoStore;
import com.s8.core.db.copper.operators.CloneBranchOp;
import com.s8.core.db.copper.operators.GetBranchMetadataOp;
import com.s8.core.db.copper.operators.GetRepoMetadataOp;
import com.s8.core.io.json.types.JSON_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends H3MgHandler<NdRepository> {
	
	public final static String METADATA_FILENAME = "repo-meta.js";
	
	
	
	private final IOModule ioModule;
	
	public final RepoStore store;
	
	public final String address;
	
	public final Path folderPath;

	
	public MgRepositoryHandler(SiliconEngine ng, RepoStore store, String address, boolean isSaved) throws JSON_CompilingException {
		super(ng, isSaved);
		this.store = store;
		this.address = address;
		this.folderPath = store.composeRepositoryPath(address);
		ioModule = new IOModule(this);
	}

	
	/**
	 * 
	 * @return
	 */
	public RepoStore getStore() {
		return store;
	}


	@Override
	public String getName() {
		return "repository handler of: "+address;
	}

	@Override
	public H3MgIOModule<NdRepository> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		NdRepository repository = getResource();
		if(repository != null) { 
			return repository.crawl();
		}
		else {
			return new ArrayList<>();
		}
	}


	public Path getFolderPath() {
		return folderPath;
	}
	
	
	public Path getMetadataFilePath() {
		return folderPath.resolve(METADATA_FILENAME);
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepo(long t, S8User initiator, SiliconChainCallback callback, 
			MgRepositoryHandler targetRepositoryHandler, ForkRepositoryS8Request request) {
		pushOpLast(new ForkRepoOp(t, initiator, callback, this, targetRepositoryHandler, request));
	}
	

	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, SiliconChainCallback callback, ForkBranchS8Request request) {
		pushOpLast(new ForkBranchOp(t, initiator, callback, this, request));
	}
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, SiliconChainCallback callback, CommitBranchS8Request request) {
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
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, SiliconChainCallback callback, GetRepositoryMetadataS8Request request) {
		pushOpLast(new GetRepoMetadataOp(t, initiator, callback, this, request));
	}


	public void getBranchMetadata(long t, S8User initiator, SiliconChainCallback callback, GetBranchMetadataS8Request request) {
		pushOpLast(new GetBranchMetadataOp(t, initiator, callback, this, request));
	}
	

}
