const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/spring/wschat'
});

stompClient.debug = function(str) {
    console.log(str)
 };

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', (m) => {
        console.log(JSON.parse(m.body).content);
        const messagesContainer = document.getElementById("chat-messages");
        const newMessage = document.createElement("p")
        newMessage.textContent = JSON.parse(m.body).content;
        messagesContainer.appendChild(newMessage);
    });
};

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);

    // Suscribirse al canal de la sala de espera
    stompClient.subscribe('/topic/salaDeEspera', (m) => {
        const data = JSON.parse(m.body);
        const button = document.getElementById(`ready-button-${data.jugadorId}`);
        if (data.estaListo) {
            button.classList.remove('btn-danger');
            button.classList.add('btn-success');
            button.textContent = 'ESTOY LISTO!';
        } else {
            button.classList.remove('btn-success');
            button.classList.add('btn-danger');
            button.textContent = 'NO ESTOY LISTO!';
        }
    });
};

function toggleReady(jugadorId, estaListo) {
    stompClient.publish({
        destination: '/app/salaDeEspera',
        body: JSON.stringify({ jugadorId: jugadorId, estaListo: estaListo })
    });
}

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

stompClient.activate();

// Take the value in the ‘message-input’ text field and send it to the server with empty headers.
function sendMessage(){

    let input = document.getElementById("message");
    let message = input.value;

    stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify({message: message})
    });
}

