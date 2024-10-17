外部客户的连接示例：
wscat -c "ws://localhost:8080/ws?vins=VIN3"

gotify消息发送示例：
curl -X POST "http://10.128.29.13:8082/message" \
     -H "X-Gotify-Key: AaW8e_QbMuC.Xk-" \
     -H "Content-Type: application/json" \
     -d '{
                        "title": "title1",
           "priority": 5,
           "message": "Hello, this is a test message!",
           "clientId": "C06k0uk.dHqkNd0",
           "extras": {
            "AIChat::vins": ["VIN1","VIN2","VIN3"]
             }
         }'

服务启动方式：
java -jar target/websocket-middleware-0.0.1-SNAPSHOT.jar --spring.config.location=file:./src/main/resources/application.properties