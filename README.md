# Queue Monitor Service

Este proyecto es un servicio de monitoreo en tiempo real de una cola FIFO en **SQS** usando **LocalStack** y transmite los mensajes a un cliente **WebSocket**. El proyecto está desarrollado en **Spring Boot** y utiliza **WebSocket** para enviar actualizaciones al frontend en tiempo real.

## Dependencias

Este proyecto depende de otro repositorio que debes levantar antes para crear la cola **SQS** en **LocalStack**. Asegúrate de haber clonado y ejecutado el siguiente proyecto:

- [LocalStack SQS Docker Terraform](https://github.com/malbarracin/localstack-sqs-docker-terraform)

### Instrucciones para levantar el proyecto de LocalStack SQS:

1. Clona el repositorio de [LocalStack SQS Docker Terraform](https://github.com/malbarracin/localstack-sqs-docker-terraform).
2. Sigue las instrucciones del README de ese proyecto para levantar LocalStack y crear la cola SQS.

---

## Cómo levantar este proyecto

### Requisitos previos

| Requisito              | Versión Requerida         |
|------------------------|---------------------------|
| ![Java](https://img.shields.io/badge/Java-21-blue) | 21 o superior               |
| ![Maven](https://img.shields.io/badge/Maven-3.6.3-red) | 3.6.3 o superior           |
| ![LocalStack](https://img.shields.io/badge/LocalStack-required-purple) | Necesario para el proyecto  |
| ![Docker](https://img.shields.io/badge/Docker-required-lightblue) | Necesario para LocalStack  |
  
### Configuración del proyecto

1. Clona este repositorio:
   ```bash
   git clone https://github.com/malbarracin/queue-monitor-service.git
   ```
2. Navega al directorio del proyecto:
   ```bash
   cd queue-monitor-service
   ```
3. Construye el proyecto utilizando Maven:   
   ```bash
   mvn clean install
   ``` 

### Configuración de la cola SQS

Asegúrate de que la cola SQS ya esté creada en LocalStack desde el proyecto anterior. La cola que debe estar en funcionamiento es:   
```bash
http://localhost:4566/000000000000/message-to-sqs.fifo
```

### Ejecutar el proyecto

Una vez que la cola SQS esté levantada, puedes ejecutar el servicio Spring Boot que monitorea la cola y envía los mensajes a través de WebSocket.

Ejecuta el proyecto:
```bash
mvn spring-boot:run
```

### Conectarse al WebSocket

El WebSocket estará disponible en la URL:
```bash
ws://localhost:8080/ws
```

Opciones para conectarse al WebSocket
Conectar con Postman:

1. Abre Postman.
   - Haz clic en "New" y selecciona "WebSocket Request".
   - Introduce la URL ws://localhost:8080/ws en el campo de la dirección.
   - Haz clic en "Connect".
   - Una vez conectado, deberías ver el mensaje "Conexión establecida. Monitoreando colas...". Si llegan mensajes desde SQS, aparecerán automáticamente en la ventana de Postman.

2. Conectar con Websocat (desde la terminal):

   - Si prefieres usar un cliente WebSocket en línea de comandos, instala websocat (si no lo tienes ya instalado):  
      ```bash
      sudo apt install websocat    # Para Ubuntu/Debian
      brew install websocat        # Para macOS usando Homebrew
      ```
   - Conéctate al WebSocket ejecutando el siguiente comando:
      ```bash
      websocat ws://localhost:8080/ws
      ```
   - Al conectarte, deberías ver el mensaje "Conexión establecida. Monitoreando colas...". Cualquier mensaje que llegue desde la cola SQS se mostrará en la terminal.

### Enviar mensajes a la cola SQS

Para probar el monitoreo en tiempo real, puedes enviar mensajes a la cola SQS utilizando AWS CLI. Recuerda que al ser una cola FIFO, debes especificar un MessageGroupId obligatorio.

Ejemplo de cómo enviar un mensaje a la cola FIFO:
```bash         
   aws --endpoint-url=http://localhost:4566 sqs send-message \
      --queue-url http://localhost:4566/000000000000/message-to-sqs.fifo \
      --message-body "Mensaje de prueba FIFO" \
      --message-group-id "group1"
```

**Importante**: El envío de mensajes a la cola SQS está documentado en detalle en el README del proyecto LocalStack SQS Docker Terraform.

Ver mensajes en tiempo real
Tan pronto como un mensaje sea enviado a la cola SQS, lo verás en el cliente WebSocket conectado (Postman o websocat).


## Diagrama del flujo del proyecto
   A continuación se muestra un diagrama que describe el flujo completo del proyecto, desde la creación de la cola en LocalStack hasta la transmisión de los mensajes en tiempo real a través de WebSocket:

   1. LocalStack (emulando AWS SQS) es levantado usando Docker y Terraform.
   2. Spring Boot Service se conecta a la cola SQS de LocalStack y monitorea los mensajes.
   3. WebSocket Client recibe los mensajes en tiempo real cuando son enviados a la cola SQS.

## Tecnologías utilizadas

| Tecnología             | Versión      |
|------------------------|--------------|
| ![Java](https://img.shields.io/badge/Java-21-blue) | 21           |
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green) | 3            |
| ![WebSocket](https://img.shields.io/badge/WebSocket-gray) | N/A          |
| ![Amazon SQS](https://img.shields.io/badge/Amazon%20SQS%20(LocalStack)-purple) | LocalStack  |
| ![Docker](https://img.shields.io/badge/Docker-20.10.7-lightblue) | 20.10.7      |
| ![Terraform](https://img.shields.io/badge/Terraform-orange) | N/A          |s


## ¿Te gusta el contenido que comparto? Invítame un café para ayudarme a seguir creando. ¡Gracias por tu apoyo!
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-F7DF1E?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://buymeacoffee.com/malbarracin)    