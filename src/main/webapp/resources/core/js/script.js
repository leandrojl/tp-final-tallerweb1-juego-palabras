const stompClient = new StompJs.Client({
    // En lugar de brokerURL, usamos webSocketFactory para SockJS
    webSocketFactory: () => new SockJS("/spring/wschat"),

    debug: (str) => {
        console.log(str);
    },

    onConnect: (frame) => {
        console.log('Connected: ', frame);
        stompClient.subscribe('/topic/messages', (m) => {
            console.log("Mensaje del subscribe:", JSON.parse(m.body));
            const data = JSON.parse(m.body);
            console.log(data.content);

            const messagesContainer = document.getElementById("palabras-mencionadas");
            const newMessage = document.createElement("p");
            newMessage.textContent = data.username + ": " + data.content;
            messagesContainer.appendChild(newMessage);
        });
    },

    onWebSocketError: (error) => {
        console.error('Error with websocket', error);
    },

    onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    }
});

stompClient.activate();

function toggleReady(jugadorId, estaListo) {
    stompClient.publish({
        destination: '/app/salaDeEspera',
        body: JSON.stringify({ jugadorId: jugadorId, estaListo: estaListo })
    });
}

function sendMessage() {
    let input = document.getElementById("input-intento");
    let message = input.value;

    stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify({ message: message })
    });
}
