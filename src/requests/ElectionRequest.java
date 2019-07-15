package requests;

public class ElectionRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1204441228278637948L;
	public Integer nodeId;
	
	public ElectionRequest(Integer nodeId) {
		this.nodeId = nodeId;
	}
}
