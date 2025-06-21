const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/spring/wschat'
});


stompClient.debug = function(str) {
    console.log(str)
};

stompClient.onConnect = (frame) => {

    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/salaDeEspera', (m) => {
        const estadoJugador = JSON.parse(m.body);
        modificarBotonDeEstadoJugador(estadoJugador);
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

function modificarBotonDeEstadoJugador(estadoJugador) {
    const button = document.getElementById(`ready-button-${estadoJugador.username}`);
    if (estadoJugador.estaListo) {
        button.classList.remove('btn-danger');
        button.classList.add('btn-success');
        button.textContent = 'SI ESTOY LISTO';
    } else {
        button.classList.remove('btn-success');
        button.classList.add('btn-danger');
        button.textContent = 'NO ESTOY LISTO';
    }
}


function agregarJugador(usuario) {
    const contenedor = document.getElementById("jugadores-container");

    const jugadorDiv = document.createElement("div");
    jugadorDiv.id = `jugador-${usuario}`;
    jugadorDiv.className = "d-flex justify-content-between align-items-center bg-light text-dark p-3 rounded-3 mb-3 shadow-sm";

    const nombreSpan = document.createElement("span");
    nombreSpan.className = "fw-bold fs-5";
    nombreSpan.textContent = usuario;

    // Usamos un <div> en lugar de <button>
    const boton = document.createElement("div");
    boton.id = `ready-button-${usuario}`;
    boton.className = "btn btn-danger";
    boton.textContent = "NO ESTOY LISTO";
    boton.style.cursor = "pointer"; // Opcional: muestra el cursor de "click"

    // Estado local: no está listo al principio
    let estaListo = false;

    boton.addEventListener("click", () => {
        estaListo = !estaListo;
        const usuarioActual = sessionStorage.getItem("usuario");
        toggleReady(usuarioActual, estaListo);
    });

    jugadorDiv.appendChild(nombreSpan);
    jugadorDiv.appendChild(boton);
    contenedor.appendChild(jugadorDiv);
}


