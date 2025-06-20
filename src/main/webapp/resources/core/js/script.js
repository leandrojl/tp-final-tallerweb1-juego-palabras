/*if (typeof stompClient === 'undefined') {
    const stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/spring/wschat'
    });

    console.log("SUSCRIPTOR A")

    stompClient.debug = function(str) {
        console.log(str);
    };

    stompClient.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', (m) => {
            const data = JSON.parse(m.body);
            console.log("Mensaje del subscribe:", data.content);

            const messagesContainer = document.getElementById("palabras-mencionadas");
            const newMessage = document.createElement("p");
            newMessage.textContent = data.username + ": " + data.content;
            messagesContainer.appendChild(newMessage);
        });
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    stompClient.activate();

    // función para enviar mensajes
    window.sendMessage = function () {
        let input = document.getElementById("input-intento");
        let message = input.value;

        stompClient.publish({
            destination: "/app/chat",
            body: JSON.stringify({ message: message })
        });
    };

}

 */
