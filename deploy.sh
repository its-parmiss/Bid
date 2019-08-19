./gradlew clean
./gradlew build
scp build/libs/bid-backend-service.jar team5@192.168.11.191:/home
ssh team5@192.168.11.191 'cd .. ; java -Dspring.config.location=./application.properties -jar bid-backend-service.jar'