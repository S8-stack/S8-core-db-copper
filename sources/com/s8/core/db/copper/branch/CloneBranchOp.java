package com.s8.core.db.copper.branch;

import java.io.IOException;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CloneBranchS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneBranchOp extends RequestDbMgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	public final CloneBranchS8Request request;
	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			MgBranchHandler handler, CloneBranchS8Request request) {
		super(timestamp, initiator, callback);
		this.branchHandler = handler;
		this.request = request;
	}


	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return branchHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(branchHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+branchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {

				/* standard cases */
				long version = request.version;
				if(version >= 0L) {
					RepoS8Object[] objects = branch.cloneVersion(version).exposure;
					request.onResponse(Status.OK, objects);
				}
				/* special cases */
				else if(version == S8AsyncFlow.HEAD_VERSION){
					RepoS8Object[] objects = branch.cloneHead().exposure;
					request.onResponse(Status.OK, objects);
				}
				else {
					request.onResponse(Status.INVALID_VERSION, null);
				}
				
				callback.call();
				
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
