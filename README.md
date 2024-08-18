# 021_kafka_with_springboot3
# Apache Kafka With Springboot 3

### First get Ubantu ip address in wsl2
sudo apt install net-tools

ifconfig

Then open windows command prompt in admin mode and paste

netsh interface portproxy add v4tov4 listenport=9092 listenaddress=0.0.0.0 connectport=9092 connectaddress=xxx.xx.xxx.xxx

### Project 1: order-service-using-kafka
### Project 2: tracking-service-using-kafka

