package com.s8.core.db.copper.operators;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request.Status;
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
public class CommitBranchOp extends CuDbOperation {
	
	public final CommitBranchS8Request request;
	
	


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitBranchOp(CuRepoDB db, long timestamp, S8User initiator, SiliconChainCallback callback, 
			CommitBranchS8Request request) {
		super(db, timestamp, initiator, callback);
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
							NdBranch branch = repository.getBranch(db.codebase, path, request.branchId);
							
							long version = branch.commit(request.objects, t, initiator.getUsername(), request.comment);
							request.onResponse(Status.OK, version);
							callback.call(); /* chaining */
							return true; /* modifications DID occured on resource */
							
							
						} catch (IOException | S8ShellStructureException e) {
							request.onFailed(e);
							callback.call(); /* chaining */
							return false; /* no modifications occured on resource */
						}
					}
					else {
						request.onResponse(Status.NO_SUCH_BRANCH, 0x00);
						callback.call(); /* chaining */
						return false; /* no modifications occured on resource */
					}
				}
				else {
					request.onResponse(Status.REPOSITORY_NOT_FOUND, 0x00);
					callback.call(); /* chaining */
					return false; /* no modifications occured on resource */
				}	
			}
			
		});
	}
	

}
