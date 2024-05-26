package com.s8.core.db.copper.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.neodymium.branch.NdBranchMetadata;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.branch.MgBranchHandler;
import com.s8.core.db.copper.io.RepoStore;


/**
 * 
 * @author pierreconvert
 *
 */
class ForkBranchOp extends RequestDbMgOperation<NdRepository> {


	public final MgRepositoryHandler repoHandler;

	public final ForkBranchS8Request request;
	



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkBranchOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			MgRepositoryHandler repoHandler, ForkBranchS8Request request) {
		super(timestamp, initiator, callback);
		this.repoHandler = repoHandler;
		this.request = request;
	}


	@Override
	public H3MgHandler<NdRepository> getHandler() {
		return repoHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<NdRepository> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdRepository>(repoHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "COMMIT-HEAD on "+request.originBranchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdRepository repository) {

				MgBranchHandler originBranchHandler = repository.branchHandlers.get(request.originBranchId);
				if(originBranchHandler != null) {

					if(!repository.branchHandlers.containsKey(request.targetBranchId)) {

						/* define a new (main) branch */
						NdBranchMetadata targetBranchMetadata = new NdBranchMetadata();
						targetBranchMetadata.name = request.targetBranchId;
						targetBranchMetadata.owner = initiator.getUsername();
						targetBranchMetadata.info = "FORK from "+request.originBranchId+"["+request.originBranchVersion+"]";
						targetBranchMetadata.headVersion = 0L;
						targetBranchMetadata.forkedBranchId = request.originBranchId;
						targetBranchMetadata.forkedBranchVersion = request.originBranchVersion;

						/* add branch metadata to repo meta*/
						repository.metadata.branches.put(request.targetBranchId, targetBranchMetadata);

						RepoStore store = repoHandler.store;
						MgBranchHandler targetBranchHandler = new MgBranchHandler(
								handler.ng, 
								store, 
								repository, 
								targetBranchMetadata, 
								false); /* just created */
						
						repository.branchHandlers.put(request.targetBranchId, targetBranchHandler);

						originBranchHandler.forkBranch(timeStamp, initiator, callback, targetBranchHandler, request);
						return true;

					}
					else { /* conflict on branch id */
						request.onResponded(Status.TARGET_BRANCH_ID_CONFLICT, 0x0L);
						callback.call();
						return false;
					}
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
