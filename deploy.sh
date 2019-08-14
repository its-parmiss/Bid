scp build/libs/bid-backend-service.jar team5@192.168.11.191:/home
ssh team5@192.168.11.191 'java -jar ../bid-backend-service.jar'
