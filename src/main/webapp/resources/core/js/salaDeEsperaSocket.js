const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
const host = window.location.host;
const stompClient = new StompJs.Client({
    brokerURL: `${protocol}://${host}/spring/wschat`,
    reconnectDelay: 5000,
    onConnect: () => {
        console.log("✅ Conectado correctamente al WebSocket");
        // tus suscripciones...
    },
    onWebSocketError: (error) => {
        console.error("❌ Error con WebSocket:", error);
    }
});

const idPartida = Number(sessionStorage.getItem("idPartida"));
const usuario = sessionStorage.getItem("usuario");
const idUsuario = Number(sessionStorage.getItem("idUsuario"));
const esCreador = sessionStorage.getItem("esCreador");




stompClient.debug = function(str) {
    console.log(str)
};

stompClient.onConnect = (frame) => {

    console.log('Connected: ' + frame);

    stompClient.subscribe('/user/queue/fuisteExpulsado', (m) => {

        console.log("--- Verificando valores de sesión CUANDO SOY EXPULSADO ---");
        console.log("ID de Partida:", sessionStorage.getItem("idPartida"));
        console.log("Usuario:", sessionStorage.getItem("usuario"));
        console.log("ID de Usuario:", sessionStorage.getItem("idUsuario"));
        console.log("Es Creador:", sessionStorage.getItem("esCreador"));
        console.log("------------------------------------");
        // (NUEVO) Limpiamos las variables de sesión de la partida
        sessionStorage.removeItem("idPartida");
        sessionStorage.removeItem("esCreador");

        alert("Has sido expulsado de la partida por el creador.");
        const protocol = window.location.protocol;
        const host = window.location.host;
        const data = JSON.parse(m.body); // Ahora data.message será "/spring/juego"

        // Esto construirá la URL correcta dinámicamente
        // Local: "http://localhost:8080" + "/spring/juego"
        // Cloudflare: "https://<tu-dominio>.com" + "/spring/juego"
        window.location.href = `${protocol}//${host}` + data.message; // Redirige al lobby
    });

    stompClient.subscribe('/topic/jugadorExpulsado/' + idPartida, (m) => {
        const data = JSON.parse(m.body);
        const nombreUsuarioExpulsado = data.message;
        const jugadorDiv = document.getElementById("jugador-" + nombreUsuarioExpulsado);
        if (jugadorDiv) {
            jugadorDiv.remove();
        }
    });

    stompClient.subscribe('/topic/salaDeEspera/' + idPartida, (m) => {

        const estadoJugador = JSON.parse(m.body);
        modificarBotonDeEstadoJugador(estadoJugador);
    });


    stompClient.subscribe('/topic/cuandoUsuarioSeUneASalaDeEspera/' + idPartida, (m) => {

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
        const protocol = window.location.protocol;
        const host = window.location.host;
        const data = JSON.parse(m.body); // Ahora data.message será "/spring/juego"

        // Esto construirá la URL correcta dinámicamente
        // Local: "http://localhost:8080" + "/spring/juego"
        // Cloudflare: "https://<tu-dominio>.com" + "/spring/juego"
        window.location.href = `${protocol}//${host}` + data.message;
    });

    stompClient.subscribe('/user/queue/alAbandonarSala', (m) => {
        const protocol = window.location.protocol;
        const host = window.location.host;
        const data = JSON.parse(m.body); // Ahora data.message será "/spring/juego"

        // Esto construirá la URL correcta dinámicamente
        // Local: "http://localhost:8080" + "/spring/juego"
        // Cloudflare: "https://<tu-dominio>.com" + "/spring/juego"
        window.location.href = `${protocol}//${host}` + data.message;
    });

    stompClient.subscribe('/topic/noSePuedeIrALaPartida', (m) => {
        const data = JSON.parse(m.body);
        const message = data.message;
        cantidadInsuficienteDeUsuariosParaIniciarPartida(message);
    });

    const message = "acabo de unirme";
    stompClient.publish({
        destination: "/app/usuarioSeUneASalaDeEspera",
        body: JSON.stringify({message: message,number : idPartida})
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

        body: JSON.stringify({ message: "", number: idPartida })

    });
}

function abandonarSala(){
    stompClient.publish({
        destination: '/app/abandonarSala',
        body: JSON.stringify({
            idUsuario: idUsuario,
            idPartida: idPartida,
            texto: "abandono no los banco mas"
        })
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

function expulsarJugador(nombreUsuarioAExpulsar) {
    if (confirm("¿Estás seguro de que quieres expulsar a este jugador?")) {
        stompClient.publish({
            destination: "/app/expulsarDeSala",
            body: JSON.stringify({
                nombreUsuario: nombreUsuarioAExpulsar,
                idPartida: idPartida
            })
        });
    }
}



function agregarJugador(usuarioQueLlegaDePrincipal) {
    const contenedor = document.getElementById("jugadores-container");

    // Contenedor principal para el jugador
    const jugadorDiv = document.createElement("div");
    jugadorDiv.id = `jugador-${usuarioQueLlegaDePrincipal}`;
    jugadorDiv.className = "d-flex justify-content-between align-items-center bg-light text-dark p-3 rounded-3 mb-3 shadow-sm";

    // Nombre del jugador
    const nombreSpan = document.createElement("span");
    nombreSpan.className = "fw-bold fs-5";
    nombreSpan.textContent = usuarioQueLlegaDePrincipal;

    // Botón de estado (listo/no listo)
    const botonEstado = document.createElement("div");
    botonEstado.id = `ready-button-${usuarioQueLlegaDePrincipal}`;
    botonEstado.className = "btn btn-danger";
    botonEstado.textContent = "NO ESTOY LISTO";

    // Si el jugador es el usuario actual, se le da funcionalidad al botón
    if (usuarioQueLlegaDePrincipal === usuario) {
        let estaListo = false;
        botonEstado.style.cursor = "pointer";
        botonEstado.addEventListener("click", () => {
            estaListo = !estaListo;
            toggleReady(usuario, estaListo);
        });
    } else {
        // Si es otro jugador, el botón está deshabilitado
        botonEstado.style.pointerEvents = "none";
        botonEstado.style.opacity = "0.6";
        botonEstado.style.cursor = "not-allowed";
    }

    // Contenedor para los botones (estado y expulsar)
    const wrapperDiv = document.createElement("div");
    wrapperDiv.className = "d-flex align-items-center";

    // Siempre se añade el botón de estado
    wrapperDiv.appendChild(botonEstado);

    // Si el usuario es creador y no es él mismo, se añade el botón de expulsar
    if (esCreador === 'true' && usuarioQueLlegaDePrincipal !== usuario) {
        const botonExpulsar = document.createElement("button");
        botonExpulsar.textContent = "✖";
        botonExpulsar.className = "btn btn-outline-danger btn-sm ms-2";
        botonExpulsar.title = "Expulsar jugador";
        botonExpulsar.onclick = () => expulsarJugador(usuarioQueLlegaDePrincipal);
        wrapperDiv.appendChild(botonExpulsar);
    }

    // Se ensambla la estructura final del DOM
    jugadorDiv.appendChild(nombreSpan);
    jugadorDiv.appendChild(wrapperDiv);
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
function cantidadInsuficienteDeUsuariosParaIniciarPartida(message) {
    let alerta = document.getElementById('alerta-insuficiente-usuarios');
    if (!alerta) {
        alerta = document.createElement('div');
        alerta.id = 'alerta-insuficiente-usuarios';

        alerta.style.position = 'fixed';
        alerta.style.bottom = '30px';
        alerta.style.right = '30px';
        alerta.style.backgroundColor = '#888888'; // gris medio
        alerta.style.color = 'white';
        alerta.style.padding = '20px 40px';
        alerta.style.borderRadius = '15px';
        alerta.style.boxShadow = '0 4px 12px rgba(0,0,0,0.2)';
        alerta.style.zIndex = '9999';
        alerta.style.fontFamily = 'Arial, sans-serif';
        alerta.style.fontSize = '16px';
        alerta.style.fontWeight = '600';
        alerta.style.textAlign = 'center';
        alerta.style.cursor = 'default';
        alerta.style.userSelect = 'none';
        alerta.style.zIndex = '9999';

        document.body.appendChild(alerta);
    }

    alerta.textContent = message;
    alerta.style.opacity = '1';
    alerta.style.transition = 'opacity 0.5s ease';
    alerta.style.display = 'block';

    setTimeout(() => {
        alerta.style.opacity = '0';
        setTimeout(() => {
            alerta.style.display = 'none';
        }, 500);
    }, 5000);
}

