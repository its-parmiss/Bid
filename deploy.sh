./gradlew clean
./gradlew build
scp build/libs/bid-backend-service.jar team5@192.168.11.191:/home
javaPid=$(ssh team5@192.168.11.191 "script/getJavaPid.sh")
ssh team5@192.168.11.191 "kill $javaPid;cd ..;java -Dspring.config.location=./application.properties -jar bid-backend-service.jar"