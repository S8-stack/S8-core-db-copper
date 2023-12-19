package com.s8.core.db.copper.branch;

import java.io.IOException;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;

/**
 * 
 * @author pierreconvert
 *
 */
class ForkRepositoryOp extends RequestDbMgOperation<NdBranch> {


	public final MgBranchHandler originBranchHandler;
	
	public final MgBranchHandler targetBranchHandler;

	public final ForkRepositoryS8Request request;


	/**
	 * 
	 * @param originBranchHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkRepositoryOp(long timestamp, S8User initiator, SiliconChainCallback callback, 
			MgBranchHandler originBranchHandler, 
			MgBranchHandler targetBranchHandler,
			ForkRepositoryS8Request request) {
		super(timestamp, initiator, callback);
		this.originBranchHandler = originBranchHandler;
		this.targetBranchHandler = targetBranchHandler;
		this.request = request;
	}


	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return originBranchHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(originBranchHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+originBranchHandler.getIdentifier()+" branch of "+originBranchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {

				/* standard cases */
				RepoS8Object[] objects = null;
				if(request.originBranchVersion >= 0L) {
					objects = branch.cloneVersion(request.originBranchVersion).exposure;
				}
				/* special cases */
				else if(request.originBranchVersion == S8AsyncFlow.HEAD_VERSION){
					objects = branch.cloneHead().exposure;
				}
				
				

				if(objects != null) {
					NdCodebase codebase = originBranchHandler.getStore().getCodebase();
					NdBranch targetBranch = new NdBranch(codebase, targetBranchHandler.getIdentifier());
					
					/* <commit> */
					long version = targetBranch.commit(objects, 
							getTimestamp(), 
							getInitiator().getUsername(), 
							"Initial commit from FORK of "+originBranchHandler.getName());
					
					/* initialize handler with newly created branch */
					targetBranchHandler.initializeResource(targetBranch);
					
					request.onResponded(Status.SUCCESSFULLY_FORKED, version);
					callback.call();
				}
				else {
					request.onFailed(new IOException("Failed to retrieve objects"));
					callback.call();
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
			}
		};
	}
	

}
