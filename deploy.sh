/etc/init.d/switch-controller stop

sleep 10

rm -rf /opt/switch-controller/deploy/*

cp -r target/dependency /opt/switch-controller/deploy/
cp -r target/*.war /opt/switch-controller/deploy

/etc/init.d/switch-controller start
