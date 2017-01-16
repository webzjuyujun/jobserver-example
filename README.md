# jobserver-example
namedrdd,namedobject examples

curl -X "DELETE" 'http://localhost:8090/contexts/147dafa8-com.jd.statistics.EnvironmentConstruct'

curl -X "DELETE" 'http://localhost:8090/jobs/ef4c0c8f-0b6c-41e3-9852-6461e90f6af6'

namedRdd
curl --data-binary @jobserver-test-1.0-SNAPSHOT.jar http://localhost:8090/jars/test01
curl -d "" 'http:/localhost:8090/contexts/test_context_01?num-cpu-cores=1&memory-per-node=512m'
curl -d "command=create" 'http://localhost:8090/jobs?appName=test01&classPath=com.jd.local.test.jobserver.EnvironmentConstruct&context=test_context_01&sync=true'
curl -d "command=create" 'http://localhost:8090/jobs?appName=test01&classPath=com.jd.local.test.jobserver.EnvironmentConstructTest&context=test_context_01'


namedDataFrame 
curl --data-binary @jobserver-test-1.0-SNAPSHOT.jar http://localhost:8090/jars/test02
curl -d "" 'http://localhost:8090/contexts/test_context_03?num-cpu-cores=1&memory-per-node=512m'
curl -d "" 'http://localhost:8090/jobs?appName=test02&classPath=com.jd.local.test.jobserver.NamedObjectTest&context=test_context_03&sync=true'
curl -d "" 'http://localhost:8090/jobs?appName=test02&classPath=com.jd.local.test.jobserver.NamedObjectTestGet&context=test_context_03'
