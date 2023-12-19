package com.s8.core.db.copper.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class GetBranchMetadataOp extends RequestDbMgOperation<MgRepository> {


	public final MgRepositoryHandler repoHandler;
	
	public final GetBranchMetadataS8Request request;



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetBranchMetadataOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			MgRepositoryHandler repoHandler, GetBranchMetadataS8Request request) {
		super(timestamp, initiator, callback);
		this.repoHandler = repoHandler;
		this.request = request;
	}
	

	@Override
	public H3MgHandler<MgRepository> getHandler() {
		return repoHandler;
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
				return "METADATA of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {
				MgBranchMetadata branchMetadata = repository.metadata.branches.get(request.branchId);
				if(branchMetadata != null) {
					request.onSucceed(Status.OK, branchMetadata);	
				}
				else {
					request.onSucceed(Status.NO_SUCH_BRANCH, branchMetadata);
				}
				callback.call();
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
