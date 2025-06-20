const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/spring/wschat'
});


stompClient.debug = function(str) {
    console.log(str)
};

stompClient.onConnect = (frame) => {

    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/salaDeEspera', (m) => {
        const data = JSON.parse(m.body);
        const button = document.getElementById(`ready-button-${data.username}`);
        if (data.estaListo) {
            button.classList.remove('btn-danger');
            button.classList.add('btn-success');
            button.textContent = 'SI ESTOY LISTO';
        } else {
            button.classList.remove('btn-success');
            button.classList.add('btn-danger');
            button.textContent = 'NO ESTOY LISTO';
        }
    });

    stompClient.subscribe('/topic/cuandoUsuarioSeUneASalaDeEspera', (m) => {
        const data = JSON.parse(m.body);
        const usuario = data.message;
        if (!document.getElementById(`jugador-`+usuario)) {
            agregarJugador(usuario);
        }
    });

    stompClient.subscribe('/user/queue/jugadoresExistentes', (m) => {
        const data = JSON.parse(m.body);

        data.usuarios.forEach((usuario) => {
            if (!document.getElementById(`jugador-`+usuario)) {
                agregarJugador(usuario);
            }
        });
    });
    const message = "acabo de unirme";
    stompClient.publish({
        destination: "/app/usuarioSeUneASalaDeEspera",
        body: JSON.stringify({message: message})
    });

};


function toggleReady(username, estaListo) {
    stompClient.publish({
        destination: '/app/salaDeEspera',
        body: JSON.stringify({ username: username, estaListo: estaListo })
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


function agregarJugador(usuario) {
    console.log("ENTRE AL METODO AGREGARJUGADOR CON EL NOMBRE: " + usuario);
    const contenedor = document.getElementById("jugadores-container");

    const jugadorDiv = document.createElement("div");
    jugadorDiv.id = `jugador-` + usuario;
    jugadorDiv.className = "d-flex justify-content-between align-items-center bg-light text-dark p-3 rounded-3 mb-3 shadow-sm";

    const nombreSpan = document.createElement("span");
    nombreSpan.className = "fw-bold fs-5";
    nombreSpan.textContent = usuario;

    const boton = document.createElement("button");
    boton.id = `ready-button-` + usuario;
    boton.className = "btn btn-danger";
    boton.textContent = "NO ESTOY LISTO";

    jugadorDiv.appendChild(nombreSpan);
    jugadorDiv.appendChild(boton);
    contenedor.appendChild(jugadorDiv);
}
