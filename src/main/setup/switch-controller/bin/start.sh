export JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -Xms2048m -Xmx2048m -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:PermSize=2048m -XX:+TraceClassUnloading"
export MEMCACHIER_SERVERS="localhost:11211"
export MEMCACHIER_USERNAME=""
export MEMCACHIER_PASSWORD=""
export SWITCH_CONTROLLER_PROPERTIES="switch-controller.properties"
export SWITCH_CONTROLLER_PHANTOMJS="/opt/switch-controller/phantomjs/phantomjs"
export SWITCH_CONTROLLER_LOGFILE="/var/log/switch-controller.log"

cd /opt/switch-controller/bin
java $JAVA_OPTS -jar /opt/switch-controller/deploy/dependency/webapp-runner.jar --port 5050 /opt/switch-controller/deploy/*.war &
echo $! > /opt/switch-controller/run/switch-controller.pid

