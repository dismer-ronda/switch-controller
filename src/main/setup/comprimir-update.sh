md5sum switch-controller/deploy/switch-controller.war > switch-controller/deploy/switch-controller.war.md5
md5sum switch-controller/sql/update/switch-controller.sql > switch-controller/sql/update/switch-controller.sql.md5

zip -Dj switch-controller-update.zip switch-controller/deploy/* switch-controller/sql/update/*

rm -rf switch-controller/deploy/*.md5
rm -rf switch-controller/sql/update/*.md5
