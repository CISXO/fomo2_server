
docker run -d --name fomoDB \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e MYSQL_DATABASE=fomoDB \
  -e MYSQL_USER=fomo \
  -e MYSQL_PASSWORD=fomo1234 \
  -p 3306:3306 \
  -v fomoDB_data:/var/lib/mysql \
  -v "$(pwd)/src/main/resources/static/initdb:/docker-entrypoint-initdb.d" \
  mariadb:latest