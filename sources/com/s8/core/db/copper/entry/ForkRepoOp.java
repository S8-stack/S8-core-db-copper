package com.s8.core.db.copper.entry;

import java.util.HashMap;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.branch.MgBranchHandler;
import com.s8.core.db.copper.store.RepoMgStore;


/**
 * 
 * @author pierreconvert
 *
 */
class ForkRepoOp extends RequestDbMgOperation<MgRepository> {


	/**
	 * origin repo handler
	 */
	public final MgRepositoryHandler repoHandler;
	
	public final MgRepositoryHandler targetRepositoryHandler;


	public final ForkRepositoryS8Request request;

	public final RepoMgStore store;

	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkRepoOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			MgRepositoryHandler repoHandler, 
			MgRepositoryHandler targetRepositoryHandler,
			ForkRepositoryS8Request request) {
		super(timestamp, initiator, callback);
		this.store = repoHandler.store;
		this.repoHandler = repoHandler;
		this.targetRepositoryHandler = targetRepositoryHandler;
		this.request = request;
	}


	@Override
	public H3MgHandler<MgRepository> getHandler() {
		return repoHandler;
	}


	public String getOriginBranchId() {
		return repoHandler.address;
	}

	@Override
	public ConsumeResourceMgAsyncTask<MgRepository> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<MgRepository>(repoHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "COMMIT-HEAD on "+getOriginBranchId()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {

				MgRepositoryMetadata repoMetadata = repository.metadata.shallowClone();
				repoMetadata.name = request.targetRepositoryName;
				repoMetadata.info = request.targetRepositoryInfo;
				repoMetadata.branches = new HashMap<>();
				
				MgRepository targetRepository = new MgRepository(repoMetadata, targetRepositoryHandler.folderPath);



				MgBranchHandler originBranchHandler = repository.branchHandlers.get(request.originBranchId);
				if(originBranchHandler != null) {

					/* define a new (main) branch */
					MgBranchMetadata targetBranchMetadata = new MgBranchMetadata();
					targetBranchMetadata.name = request.originBranchId;
					targetBranchMetadata.info = "FORK from "+request.originBranchId+"["+request.originBranchVersion+"]";
					targetBranchMetadata.headVersion = 0L;
					targetBranchMetadata.forkedBranchId = request.originBranchId;
					targetBranchMetadata.forkedBranchVersion = request.originBranchVersion;
					
					repoMetadata.branches.put(request.originBranchId, targetBranchMetadata);


					MgBranchHandler targetBranchHandler = new MgBranchHandler(
							handler.ng, 
							store, 
							repository, 
							targetBranchMetadata, 
							false); /* just created */
					
					targetRepository.branchHandlers.put(request.originBranchId, targetBranchHandler);

					originBranchHandler.forkRepo(timeStamp, initiator, callback, targetBranchHandler, request);

					return true;

				}
				else {
					request.onResponded(Status.NO_SUCH_ORIGIN_BRANCH, 0x0L);
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
