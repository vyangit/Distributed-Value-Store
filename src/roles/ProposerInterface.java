package roles;

import requests.AcceptRequest;
import requests.ProposalRequest;

/**
 * @author Victor
 *
 * Proposers send proposals to acceptors and upon majority promises
 * proposers send a corresponding acceptance request which is then 
 * accepted or rejected. An accepted request is then broadcasted to all nodes. 
 * 
 */
public interface ProposerInterface {
	void proposeTransaction(ProposalRequest proposal);
	void prepareTransaction(ProposalRequest proposal);
	void requestAccept(AcceptRequest acceptReq);
}
