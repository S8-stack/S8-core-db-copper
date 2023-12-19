package com.s8.core.db.copper.entry;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CloneBranchS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.db.copper.branch.MgBranchHandler;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneBranchOp extends RequestDbMgOperation<MgRepository> {


	/**
	 * 
	 */
	public final MgRepositoryHandler repoHandler;

	
	/**
	 * 
	 */
	public final CloneBranchS8Request request;
	



	/**
	 * 
	 * @param storeHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initator, SiliconChainCallback callback, 
			MgRepositoryHandler repoHandler, 
			CloneBranchS8Request request) {
		super(timestamp, initator, callback);
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
				return "CLONE-HEAD on "+request.branchId+" branch of "+handler.getName()+ " repository";
			}

			
			@Override
			public boolean consumeResource(MgRepository repository) throws IOException {

				MgBranchHandler branchHandler = repository.branchHandlers.get(request.branchId);

				if(branchHandler != null) { 
					branchHandler.cloneBranch(timeStamp, initiator, callback, request);
				}
				else {
					/* throw new IOException("No branch "+branchId+" on repo "+repository.getAddress());  */
					request.onResponse(Status.NO_SUCH_BRANCH, null);
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
