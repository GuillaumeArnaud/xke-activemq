export ACTIVEMQ_HOME=/home/garnaud/applications/apache-activemq-5.8.0

for id in "${@:2}"
do

	export ACTIVEMQ_BASE=/home/garnaud/workspace/xke-activemq/brokers/broker${id}
	export ACTIVEMQ_DATA=/home/garnaud/workspace/xke-activemq/brokers/broker${id}/data/
	export ACTIVEMQ_CONF=/home/garnaud/workspace/xke-activemq/brokers/broker${id}/conf/
	export ACTIVEMQ_CLASSPATH=/home/garnaud/workspace/xke-activemq/brokers/broker${id}/conf/

	case $1 in
		"start")
			$ACTIVEMQ_HOME/bin/activemq start xbean:file:$ACTIVEMQ_BASE/conf/broker${id}.xml;;
		"stop")
			$ACTIVEMQ_HOME/bin/activemq stop --jmxurl service:jmx:rmi:///jndi/rmi://localhost:300${id}/jmxrmi "broker${id}";;
	esac

done
