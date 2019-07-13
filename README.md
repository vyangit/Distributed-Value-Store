# Distributed-Value-Store
Implementation of the paxos algorithm used to manage a copy of a distributed table on each node. This implementation is purely made for learning purposes.

#### Concepts/ideas utilized in this educational project:
* paxos & leader election
* heartbeat signals
* TCP socket programming & failure management
* synchronized and multi-threading concepts
* logger using a priority queue and threshold log dumping
* design patterns: Factory and singleton patterns

## Quick introduction to the Paxos concept:
Lamport's simplified write up: https://lamport.azurewebsites.net/pubs/paxos-simple.pdf  
Video simplifying paxos: https://www.youtube.com/watch?v=SRsK-ZXTeZ0  

## Extension of Paxos concept and other related consensus models:
Multi-paxos: http://www.beyondthelines.net/algorithm/multi-paxos/  
Raft: https://raft.github.io/  
