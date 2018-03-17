echo Descomprimiendo archivos
apt-get install -y zip unzip ntp
unzip instaladores.zip -d /opt
echo "Press any key to continue or Ctrl+C to cancel"
read

#
# OpenJDK
#
echo Instalando Java
apt-get install -y openjdk-8-jre
echo "Press any key to continue or Ctrl+C to cancel"
read

#
# POSTGRESQL
#
echo Instalando y configurando PostgreSQL ...
apt-get install -y postgresql-9.6
sed -i 's/md5/trust/g' /etc/postgresql/9.6/main/pg_hba.conf
sed -i 's/peer/trust/g' /etc/postgresql/9.6/main/pg_hba.conf
service postgresql reload
echo "Press any key to continue or Ctrl+C to cancel"
read

#
# TOMCAT7
#
#
echo Creando servicio Tomcat7
ln -s /opt/tomcat7/bin/tomcat7 /etc/init.d/tomcat7
update-rc.d tomcat7 defaults

echo Instalando WARs de IMEDIG en Tomcat7
ln -s /opt/switch-controller/deploy/smartswitch-1.0.war /opt/tomcat7/webapps/ROOT.war

echo Iniciando servicio Tomcat7
/etc/init.d/tomcat7 start

#
# SWITCH-CONTROLLER
#
echo Creando usuario para bases de datos de switch-controller
su - postgres -c "createuser --no-superuser --createdb --no-createrole --pwprompt switch-controller < /opt/switch-controller/sql/switch-controller.pwd"

echo Creando bases de datos de switch-controller
su - postgres -c "createdb --owner switch-controller switch-controller"

echo Inicializando datos de switch-controller
su - postgres -c "psql -U switch-controller -d switch-controller < /opt/switch-controller/sql/1002_squema.sql"
su - postgres -c "psql -U switch-controller -d switch-controller < /opt/switch-controller/sql/1003_datos.sql"
echo "Press any key to continue or Ctrl+C to cancel"
read

#
# APACHE2
#
echo Configurando Apache
#apt-get install apache2
a2enmod proxy
a2enmod proxy_http
a2enmod headers
a2enmod rewrite
ln -sf /opt/apache2/switch-controller.conf /etc/apache2/sites-enabled/switch-controller.conf
rm /etc/apache2/sites-enabled/000-default.conf
service apache2 restart

#
# ETH0
#
echo Modificando configuración de red
ln -sf /opt/network/interfaces /etc/network/interfaces
# -----------------------------

#
# LOGROTATE
#
ln -s /opt/logrotate/switch-controller /etc/logrotate.d/switch-controller

(crontab -l ; echo "0 0 * * * /opt/logrotate/delete-files.sh") | crontab

echo "Press any key to finish"
read
