package com.s8.core.db.copper.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.db.copper.branch.MgBranchHandler;
import com.s8.core.db.copper.entry.MgBranchMetadata;
import com.s8.core.db.copper.entry.MgRepository;
import com.s8.core.db.copper.entry.MgRepositoryHandler;
import com.s8.core.db.copper.entry.MgRepositoryMetadata;
import com.s8.core.io.json.types.JSON_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class CreateRepoOp extends RequestDbMgOperation<RepoStore> {


	public final CuRepoDB storeHandler;

	public final CreateRepositoryS8Request request;
	



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateRepoOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			CuRepoDB handler, CreateRepositoryS8Request request) {
		super(timestamp, initiator, callback);
		this.storeHandler = handler;
		this.request = request;
	}


	@Override
	public H3MgHandler<RepoStore> getHandler() {
		return storeHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<RepoStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<RepoStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoStore store) throws JSON_CompilingException, IOException, S8ShellStructureException {


				MgRepositoryHandler repoHandler = store.createRepositoryHandler(request.repositoryAddress);

				if(repoHandler != null) {
					
					/* <metadata> */
					MgRepositoryMetadata metadata = new MgRepositoryMetadata();
					metadata.name = request.repositoryName;
					metadata.address = request.repositoryAddress;
					metadata.info = request.repositoryInfo;
					metadata.owner = initiator.getUsername();
					metadata.creationDate = timeStamp;
					metadata.branches = new HashMap<>();
					
					/* define a new (main) branch */
					MgBranchMetadata mainBranchMetadata = new MgBranchMetadata();
					mainBranchMetadata.name = request.mainBranchName;
					mainBranchMetadata.info = "Created as MAIN branch";
					mainBranchMetadata.headVersion = 0L;
					mainBranchMetadata.forkedBranchId = null;
					mainBranchMetadata.forkedBranchVersion = 0L;
					mainBranchMetadata.owner = initiator.getUsername();
					metadata.branches.put(request.mainBranchName, mainBranchMetadata);
					
					/* </metadata> */
					
					SiliconEngine ng = handler.ng;

					Path path = store.composeRepositoryPath(metadata.address);
					
					
					
					MgRepository repository = new MgRepository(metadata, path);
					
					/* <nd-branch> */
					MgBranchHandler branchHandler = new MgBranchHandler(ng, store, repository, mainBranchMetadata, false);
					NdBranch ndBranch = new NdBranch(store.getCodebase(), request.mainBranchName);
					long version = ndBranch.commit(request.objects, getTimestamp(), initiator.getUsername(), request.initialCommitComment);
					branchHandler.initializeResource(ndBranch);
					/* </nd-branch> */
					
					repository.branchHandlers.put(request.mainBranchName, branchHandler);
					repoHandler.initializeResource(repository);
					
					request.onResponse(Status.OK, version);
					callback.call();
					return true;
				}
				else {
					/* if repoHandler is null => implies collision for repository address */
					request.onResponse(Status.IS_ADDRESS_CONFLICTING, 0x0L);
					callback.call();
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}


}
