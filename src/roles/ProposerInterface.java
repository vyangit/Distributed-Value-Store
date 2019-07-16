package roles;

import commands.AbstractDistributedCommand;
import requests.ProposalRequest;
import responses.PromiseResponse;

/**
 * @author Victor
 *
 * Proposers send proposals to acceptors and upon majority promises
 * proposers send a corresponding acceptance request which is then 
 * accepted or rejected. An accepted request is then broadcasted to all nodes. 
 * 
 */
public interface ProposerInterface {
	void proposeTransaction(AbstractDistributedCommand command);
	void prepareTransaction(ProposalRequest proposal);
	void requestAccept(PromiseResponse promiseRes);
}
