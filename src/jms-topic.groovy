import org.apache.activemq.ActiveMQConnectionFactory

@Grab(group = 'com.netflix.rxjava', module = 'rxjava-groovy', version = '0.8.4') @Grab(group = "org.apache.activemq", module = "activemq-all", version = "5.8.0")
import javax.jms.Session
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

import static java.util.concurrent.TimeUnit.SECONDS
import static javax.jms.Session.AUTO_ACKNOWLEDGE

def maxMsg = 5000
def nbSndr = 20
def nbRcvr = 10

AtomicLong currSndMsg = new AtomicLong(0), currRcvMsg = new AtomicLong(0)
def rcvStopped = new LinkedBlockingQueue<Boolean>()
def sndStopped = new LinkedBlockingQueue<Boolean>()
def rcvInit = new LinkedBlockingQueue<Boolean>()



nbRcvr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def stop = false
            def conn = new ActiveMQConnectionFactory("tcp://localhost:61616").createTopicConnection()
            conn.start()

            def session = conn.createTopicSession(false, AUTO_ACKNOWLEDGE)
            def topic = session.createTopic("mytopic")

            def rcv = session.createSubscriber(topic)

            rcvInit << true
            while (!stop) {
                def msg = rcv.receive()
                stop = msg.getBooleanProperty("poison.pill")
                if (stop) println "stop!!!"
                else currRcvMsg.incrementAndGet()
            }
            rcvStopped << true
        }
    }).start()
}

nbRcvr.times { rcvInit.take() }

nbSndr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def conn = new ActiveMQConnectionFactory("tcp://localhost:61616").createTopicConnection()
            conn.start()

            def session = conn.createTopicSession(false, AUTO_ACKNOWLEDGE)
            def topic = session.createTopic("mytopic")
            def producer = session.createProducer(topic)

            while (currSndMsg.get() < maxMsg) {
                def m = currSndMsg.getAndIncrement()
                producer.send(topic, session.createTextMessage("test $m"))
            }
            sndStopped << true
        }
    }).start()
}



new Thread(new Runnable() {

    @Override
    void run() {
        // waiting the end of all senders
        nbSndr.times { sndStopped.take() }
        println "end of sends, start poison pills"
        // send poison pills to receivers
        def conn = new ActiveMQConnectionFactory("tcp://localhost:61616").createTopicConnection()
        conn.start()

        def session = conn.createTopicSession(false, AUTO_ACKNOWLEDGE)
        def topic = session.createTopic("test")
        def producer = session.createPublisher(topic)

        def poisonPill = session.createTextMessage()
        poisonPill.setBooleanProperty("poison.pill", true)

        nbRcvr.times {
            producer.send(topic, poisonPill)
            rcvStopped.poll(1, SECONDS)
            println "a new receiver is stopped"
        }
        println "${currRcvMsg.get()} received messages for ${currSndMsg.get()} sent"
        System.exit(0)
    }
}).start()

