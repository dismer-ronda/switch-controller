kill -9 `cat /opt/switch-controller/run/switch-controller.pid`
rm /opt/switch-controller/run/switch-controller.pid
rm -rf /opt/switch-controller/bin/target/tomcat.5050
