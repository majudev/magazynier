#!/bin/bash
DB="magazynier"
USER="magazynier"
PASSWORD="magazynier"

echo "Installing packages"
apt install -y mariadb-server-10.5 mariadb-client-10.5 openjdk-11-jre nginx-light redis-server wget exim4 unzip

./download.sh

echo "Securing MySQL instance..."
mysql << EOF
	UPDATE mysql.global_priv SET priv=json_set(priv, '$.password_last_changed', UNIX_TIMESTAMP(), '$.plugin', 'mysql_native_password', '$.authentication_string', 'invalid', '$.auth_or', json_array(json_object(), json_object('plugin', 'unix_socket'))) WHERE User='root';
	DELETE FROM mysql.global_priv WHERE User='';
	DELETE FROM mysql.global_priv WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
	DROP DATABASE IF EXISTS test;
	DELETE FROM mysql.db WHERE Db='test' OR Db='test\_%';
	FLUSH PRIVILEGES;
EOF
echo "Creating database & user"
mysql << EOF
	DROP DATABASE IF EXISTS $DB;
	CREATE DATABASE $DB;
	CREATE USER '$USER'@'localhost' IDENTIFIED BY '$PASSWORD';
	GRANT ALL PRIVILEGES ON $DB.* TO '$USER'@'localhost';
	FLUSH PRIVILEGES;
EOF
echo "Creating database schema..."
cat schema.sql | sed 's/InnoDB/InnoDB;/g' | sed 's/increment by 1/increment by 1;/g' | sed -E 's/(unique \(.+\))/\1;/g' | sed -E 's/(references .+ \(.+\))/\1;/g' > schema.mariadb
mysql -D magazynier --user=$USER --password=$PASSWORD < schema.mariadb

echo "Updating exim4 config"
sed -i "s/dc_eximconfig_configtype='local'/dc_eximconfig_configtype='internet'/g" /etc/exim4/update-exim4.conf.conf
service exim4 restart

echo "Updating nginx config"
echo "Enter domain you will access your instance on: "
read "domain"
tee /etc/nginx/sites-available/default << EOF
server {
        listen 80 default_server;
        listen [::]:80 default_server;

        root /var/www/html;

        # Add index.php to the list if you are using PHP
        index index.html;

        server_name _;

        location / {
                # First attempt to serve request as file, then
                # as directory, then fall back to displaying a 404.
                try_files \$uri \$uri/ =404;
        }

        location /api {
                proxy_pass http://127.0.0.1:8080;
        }
}
EOF
cp -r magazynier /var/www/html
sed -E -i 's/(var baseUrl = "http:\/\/).+(\/magazynier";)/\1'$domain'\2/g' /var/www/html/magazynier/js/config.js
sed -E -i 's/(var apiUrl = "http:\/\/).+(\/api";)/\1'$domain'\2/g' /var/www/html/magazynier/js/config.js
sed -i 's/localhost/'$domain'/g' start.sh
service nginx restart
echo "Done!"
exit 0
