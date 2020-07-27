# upgrade-challenge
Volcano camping challenge

##Solution
The solution was build with all the endpoint and requirements specified on the problem statement. Two tables where used to solve the problem. The booking table to store the basic information about the reservation and reserved days to store the days that are already reserved.
 
 ##Highlighted System Requirements
 #####Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.

- Since the island has a single campsite adding a uniqueness constrain on the reserved days table will be enough to keep the consistency of the system.
- The uniqueness constrain, the transactional operations (book and update), and the optimistic concurrency control will do the magic for the concurrent request to reserve/update the campsite.
- Just Insert/Delete operations will be used to keep the consistency in the reserved days.
 
 ##### In general, the system should be able to handle large volume of requests for getting the campsite availability.

- Base on the solution provide to handle the concurrent request the system is scalable.
- An ecosystem with kubernetes is possible to create replicas to handle large volume of request. 

##Suggestions
###Cons
-Due to the amount of Insert/Delete operations on reserved days table periodic vacuum need to be performed.  
###Test
- Complete the automatic testing. 
- Create a Load test and analyze the performance using replicas. (Kubernetes and Apache JMeter).
- Database cluster could be used if need it.
  https://github.com/bitnami/bitnami-docker-mariadb.
