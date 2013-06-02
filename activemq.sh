export ACTIVEMQ_HOME=/Users/garnaud25/applications/apache-activemq-5.8.0

for id in "${@:2}"
do

	export ACTIVEMQ_BASE=/Users/garnaud25/workspace/xke-activemq/brokers/broker${id}/
	export ACTIVEMQ_DATA=/Users/garnaud25/workspace/xke-activemq/brokers/broker${id}/data/

	case $1 in
		"start")
			$ACTIVEMQ_HOME/bin/activemq start -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE xbean:file:$ACTIVEMQ_BASE/conf/broker.xml;;
		"stop")
			$ACTIVEMQ_HOME/bin/activemq stop --jmxurl service:jmx:rmi:///jndi/rmi://localhost:300${id}/jmxrmi "broker${id}";;
			$ACTIVEMQ_HOME/bin/activemq stop  -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE "broker${id}";;
		"restart")
			$ACTIVEMQ_HOME/bin/activemq stop  -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE "broker${id}";
			$ACTIVEMQ_HOME/bin/activemq start -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE xbean:file:$ACTIVEMQ_BASE/conf/broker.xml;;
	esac

done
