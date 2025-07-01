const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/spring/wschat'
});

const idPartida = Number(sessionStorage.getItem("idPartida"));
const usuario = sessionStorage.getItem("usuario");
const usuarioId = Number(sessionStorage.getItem("usuarioId"));
console.log("GUARDADO EN SESION EL USUARIO ID: " + usuarioId);
console.log("GUARDADO EN SESION EL USAURIO: " + usuario);
console.log("GUARDADO EN SESION EL ID DE LA PARTIDA: " + idPartida);


stompClient.debug = function(str) {
    console.log(str)
};

stompClient.onConnect = (frame) => {

    console.log('Connected: ' + frame);


    stompClient.subscribe('/topic/salaDeEspera/' + idPartida, (m) => { //aca iria el id de partida una vez que este

        const estadoJugador = JSON.parse(m.body);
        modificarBotonDeEstadoJugador(estadoJugador);
    });


    stompClient.subscribe('/topic/cuandoUsuarioSeUneASalaDeEspera/' + idPartida, (m) => { //aca iria el id de partida una vez que este

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

    stompClient.subscribe('/user/queue/mensajeAlIntentarCambiarEstadoDeOtroJugador', (m) => {
        const data = JSON.parse(m.body);
        mostrarError(data.message);
    });

    stompClient.subscribe('/user/queue/irAPartida', (m) => {
        const data = JSON.parse(m.body);
        window.location.href = data.message;
    });

    const message = "acabo de unirme";
    stompClient.publish({
        destination: "/app/usuarioSeUneASalaDeEspera",
        body: JSON.stringify({message: message,number : idPartida}) //aca iria el id de partida una vez que este
    });

};

function toggleReady(username, estaListo) {
    stompClient.publish({
        destination: '/app/salaDeEspera',
        body: JSON.stringify({ idPartida : idPartida,username: username, estaListo: estaListo }) //aca iria el id de partida una vez que este
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

function iniciarPartida(){
    stompClient.publish({
        destination: '/app/inicioPartida',

        body: JSON.stringify({ message: "", number: idPartida })  //aca iria id de partida una vez que este

    });
}

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


function agregarJugador(usuarioQueLlegaDePrincipal) {
    console.log("USUARIO DEL PRINCIPAL: " + usuarioQueLlegaDePrincipal);
    const contenedor = document.getElementById("jugadores-container");

    const jugadorDiv = document.createElement("div");
    jugadorDiv.id = `jugador-${usuarioQueLlegaDePrincipal}`;
    jugadorDiv.className = "d-flex justify-content-between align-items-center bg-light text-dark p-3 rounded-3 mb-3 shadow-sm";

    const nombreSpan = document.createElement("span");
    nombreSpan.className = "fw-bold fs-5";
    nombreSpan.textContent = usuarioQueLlegaDePrincipal;

    const boton = document.createElement("div");
    boton.id = `ready-button-${usuarioQueLlegaDePrincipal}`;
    boton.className = "btn btn-danger";
    boton.textContent = "NO ESTOY LISTO";

    if (usuarioQueLlegaDePrincipal === usuario) {
        let estaListo = false;

        boton.style.cursor = "pointer";
        boton.addEventListener("click", () => {
            estaListo = !estaListo;
            toggleReady(usuario, estaListo);
        });
    } else {
        boton.style.pointerEvents = "none";
        boton.style.opacity = "0.6";
        boton.style.cursor = "not-allowed";
    }

    jugadorDiv.appendChild(nombreSpan);
    jugadorDiv.appendChild(boton);
    contenedor.appendChild(jugadorDiv);
}


function mostrarError(message) {
    const errorDiv = document.createElement("div");

    errorDiv.textContent = message;
    errorDiv.style.position = "fixed";
    errorDiv.style.top = "20px";
    errorDiv.style.left = "50%";
    errorDiv.style.transform = "translateX(-50%)";
    errorDiv.style.padding = "12px 24px";
    errorDiv.style.backgroundColor = "rgba(220, 53, 69, 0.9)";
    errorDiv.style.color = "#fff";
    errorDiv.style.borderRadius = "8px";
    errorDiv.style.boxShadow = "0 4px 8px rgba(0, 0, 0, 0.2)";
    errorDiv.style.zIndex = "9999";
    errorDiv.style.opacity = "0";
    errorDiv.style.transition = "opacity 0.5s ease";

    document.body.appendChild(errorDiv);

    // Fade in
    setTimeout(() => {
        errorDiv.style.opacity = "1";
    }, 10);

    // Fade out y remove
    setTimeout(() => {
        errorDiv.style.opacity = "0";
        setTimeout(() => {
            document.body.removeChild(errorDiv);
        }, 500);
    }, 3000);
}


