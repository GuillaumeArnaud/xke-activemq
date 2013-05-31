@Grab(group = "org.apache.activemq", module = "activemq-all", version = "5.8.0")
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.Connection
import javax.jms.QueueSession
import javax.jms.Session
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

class JMSUtils {

    public static void purgeQueue(String host = "localhost", int port = 1099, String brokerName = "localhost", String queueName) {
        def server = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://$host:$port/jmxrmi")).MBeanServerConnection
        def queueObjName = "org.apache.activemq:type=Broker,brokerName=$brokerName,destinationType=Queue,destinationName=$queueName"
        if (server.isRegistered(ObjectName.getInstance(queueObjName))) {
            def mbean = new GroovyMBean(server, queueObjName)
            println "purge queue $queueName:"
            println "current topic size = ${mbean.QueueSize}"
            mbean.purge()
            println "current topic size (after purge) = ${mbean.QueueSize}"
        }
    }

    public static QueueSession createQueueSession(String host = "localhost", int port = 61616) {
        // create the connection
        def connection = new ActiveMQConnectionFactory("tcp://$host:$port").createQueueConnection()
        connection.start()
        // add closeAll mixin
        Session.mixin CloseableSession
        // create the session
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        // init the mixins CloseableSession
        session.session = session
        session.connection = connection
        session
    }

    static class CloseableSession {
        Session session
        Connection connection

        public void closeAll() {
            try {
                if (session) {
                    session.close()
                }
                if (connection) {
                    connection.close()
                }
            } catch (Throwable t) {
                println "can't close connection"
                t.printStackTrace()
            }
        }
    }
}
