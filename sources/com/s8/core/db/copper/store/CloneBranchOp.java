package com.s8.core.db.copper.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CloneBranchS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.entry.MgRepositoryHandler;
import com.s8.core.io.json.types.JSON_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneBranchOp extends RequestDbMgOperation<RepoStore> {


	public final CuRepoDB storeHandler;

	public final CloneBranchS8Request request;
	


	/**
	 * 
	 * @param storeHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			CuRepoDB storeHandler, CloneBranchS8Request request) {
		super(timestamp, initiator, callback);
		this.storeHandler = storeHandler;
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
				return "CLONE-HEAD on "+request.branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoStore store) throws JSON_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(request.repositoryAddress);

				if(repoHandler != null) {
					repoHandler.cloneBranch(timeStamp, initiator, callback, request);
				}
				else {
					request.onResponse(Status.REPOSITORY_NOT_FOUND, null);
					callback.call();
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onError(exception);
				callback.call();
			}			
		};
	}

}
