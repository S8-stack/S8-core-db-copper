package com.s8.core.db.copper.operators;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request.Status;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.TiResourceStatus;
import com.s8.core.arch.titanium.db.requests.AccessTiRequest;
import com.s8.core.bohr.neodymium.branch.NdBranchMetadata;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 * @author pierreconvert
 *
 */
public class GetBranchMetadataOp extends CuDbOperation {


	/**
	 * 
	 */
	public final GetBranchMetadataS8Request request;



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetBranchMetadataOp(CuRepoDB db, long timestamp, S8User initiator, SiliconChainCallback callback, 
			GetBranchMetadataS8Request request) {
		super(db, timestamp, initiator, callback);
		this.request = request;
	}
	
	
	public void process(){
		db.processRequest(new AccessTiRequest<NdRepository>(t, request.repositoryAddress, false) {
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}
			
			@Override
			public String describe() {
				return "METADATA of "+request.repositoryAddress+ " repository";
			}
			

			@Override
			public boolean onResourceAccessed(Path path, TiResourceStatus status, NdRepository repository) {
				if(status.isAvailable()) {
					NdBranchMetadata branchMetadata = repository.metadata.branches.get(request.branchId);
					if(branchMetadata != null) {
						request.onSucceed(Status.OK, branchMetadata);	
					}
					else {
						request.onSucceed(Status.NO_SUCH_BRANCH, branchMetadata);
					}
					
				}
				else {
					request.onFailed(new IOException("failed to retrieve repository: "+request.repositoryAddress));
				}
				callback.call();
				return false; /* ho change happened to the resource */
			}
		});
	}

}
