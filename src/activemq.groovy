final ACTIVEMQ_HOME = "/Users/garnaud25/applications/apache-activemq-5.8.0"
final ACTIVEMQ_CONF = "/Users/garnaud25/workspace/messenger/conf"


def start="nohup $ACTIVEMQ_HOME/bin/activemq -Dactivemq.conf=$ACTIVEMQ_CONF start xbean:file:$ACTIVEMQ_CONF/broker1.xml".execute()
start.waitFor()
println start.exitValue()
println start.text
println "standard in: ${start.in.text}"
println "standard out: ${start.err.text}"
