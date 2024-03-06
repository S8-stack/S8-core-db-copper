package com.s8.core.db.copper.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request.Status;
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
class GetBranchMetadataOp extends RequestDbMgOperation<RepoStore> {


	/**
	 * 
	 */
	public final CuRepoDB storeHandler;

	/**
	 * 
	 */
	public final GetBranchMetadataS8Request request;

	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetBranchMetadataOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			CuRepoDB handler, GetBranchMetadataS8Request request) {
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
				return "GET-META on "+request.repositoryAddress+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoStore store) throws JSON_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(request.repositoryAddress);
				if(repoHandler != null) {
					repoHandler.getBranchMetadata(timeStamp, initiator, callback, request);
				}
				else {
					request.onSucceed(Status.UNKNOWN_REPOSITORY, null);
					callback.call();
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}			
		};
	}

}
