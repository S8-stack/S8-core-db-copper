package com.s8.core.db.copper.operators;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CloneBranchS8Request.Status;
import com.s8.core.arch.magnesium.db.MgResourceStatus;
import com.s8.core.arch.magnesium.db.requests.AccessMgRequest;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 * @author pierreconvert
 *
 */
public class CloneBranchOp extends CuDbOperation {


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
	public CloneBranchOp(CuRepoDB db, long timestamp, S8User initator, SiliconChainCallback callback, 
			CloneBranchS8Request request) {
		super(db, timestamp, initator, callback);
		this.request = request;
	}
	

	
	public void process() {
		db.processRequest(new AccessMgRequest<NdRepository>(t, request.repositoryAddress, false) {

			@Override
			public MthProfile profile() { 
				return MthProfile.IO_SSD; 
			}

			
			@Override
			public String describe() {
				return "CLONE-HEAD on "+request.branchId+" branch of "+request.repositoryAddress+ " repository";
			}


			@Override
			public boolean onResourceAccessed(Path path, MgResourceStatus status, NdRepository repository) {
				if(status.isAvailable()) {
					if(repository.metadata.branches.containsKey(request.branchId)) {
						try {	
							NdBranch branch = db.ioModule.getBranch(path, repository, request.branchId);
							

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
							
						} catch (IOException | S8ShellStructureException e) {
							request.onError(e);
						}
					}
					else {
						request.onResponse(Status.NO_SUCH_BRANCH, null);
					}
				}
				else {
					request.onResponse(Status.REPOSITORY_NOT_FOUND, null);
				}
				
				
				/* chaining */
				callback.call();
				
				/* no modifications occured on resource */
				return false;
			}
		});
	}
	
	
}
