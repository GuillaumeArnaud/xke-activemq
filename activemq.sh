export ACTIVEMQ_HOME= LE PATH VERS ACTIVEMQ
export HOME_PROJECT= LE PATH DU PROJET xke-activemq

for id in "${@:2}"
do

	export ACTIVEMQ_BASE=$HOME_PROJECT/brokers/broker${id}/
	export ACTIVEMQ_DATA=$HOME_PROJECT/brokers/broker${id}/data/

	case $1 in
		"start")
			$ACTIVEMQ_HOME/bin/activemq start -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE xbean:file:$ACTIVEMQ_BASE/conf/broker.xml;;
		"stop")
			$ACTIVEMQ_HOME/bin/activemq stop --jmxurl service:jmx:rmi:///jndi/rmi://localhost:300${id}/jmxrmi "broker${id}";;
		"restart")
			$ACTIVEMQ_HOME/bin/activemq stop --jmxurl service:jmx:rmi:///jndi/rmi://localhost:300${id}/jmxrmi "broker${id}";
			$ACTIVEMQ_HOME/bin/activemq start -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE xbean:file:$ACTIVEMQ_BASE/conf/broker.xml;;
	esac

done
