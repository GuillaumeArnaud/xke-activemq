final ACTIVEMQ_HOME = "/Users/garnaud25/applications/apache-activemq-5.8.0"
final ACTIVEMQ_CONF = "/Users/garnaud25/workspace/xke-activemq/conf"


println "ACTIVEMQ_HOME=$ACTIVEMQ_HOME"
println "ACTIVEMQ_CONF=$ACTIVEMQ_CONF"

def usage = "groovy activemq.groovy [start|stop] [[1-6] ]+.\nFor instance: \ngroovy activemq.groovy start 1 3 6\n"
if (!args || args.length < 2) println "missing parameters :\n $usage"

/*
def status="$ACTIVEMQ_HOME/bin/activemq -Dactivemq.conf=$ACTIVEMQ_CONF status xbean:file:$ACTIVEMQ_CONF/broker.xml&".execute()
status.waitFor()
println status.text
println status.exitValue()
println "status"
println status.text
  */

if ("start".equals(args[0])) {
    (1..(args.length - 1)).each { brokerId ->
        def commandline = "$ACTIVEMQ_HOME/bin/activemq -Dactivemq.conf=$ACTIVEMQ_CONF/brokers start xbean:file:$ACTIVEMQ_CONF/brokers/broker${brokerId}.xml       &"
        println commandline
        def start = commandline.execute()
        start.waitFor()
        println start.exitValue()
        println start.text
        println "standard out: ${start.err.text}"
        println "standard in: ${start.in.text}"
    }
} else if ("stop".equals(args[0])) {
    (1..(args.length - 1)).each { brokerId ->
        def stop = "nohup $ACTIVEMQ_HOME/bin/activemq stop --jmxurl service:jmx:rmi:///jndi/rmi://localhost:300${brokerId}/jmxrmi".execute()
        stop.waitFor()
        println stop.text
    }

} else {
    println "command ${args[0]} doesn't exist.\n$usage"
}