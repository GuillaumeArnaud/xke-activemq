export ACTIVEMQ_HOME=/Users/garnaud25/applications/apache-activemq-5.8.0

for id in "${@:2}"
do

	export ACTIVEMQ_BASE=/Users/garnaud25/workspace/xke-activemq/brokers/broker${id}/
	export ACTIVEMQ_DATA=/Users/garnaud25/workspace/xke-activemq/brokers/broker${id}/data/

	case $1 in
		"start")
			$ACTIVEMQ_HOME/bin/activemq start -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE xbean:file:$ACTIVEMQ_BASE/conf/broker${id}.xml;;
		"stop")
			$ACTIVEMQ_HOME/bin/activemq stop  -Dactivemq.data=$ACTIVEMQ_DATA -Dactivemq.home=$ACTIVEMQ_HOME -Dactivemq.base=$ACTIVEMQ_BASE "broker${id}";;
	esac

done
