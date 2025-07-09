
let stompClient = null;
let tiempoRestante = 60;
let intervaloTemporizador;
let intervaloLetras;
let finRondaEjecutada = false;

const usuarioId = Number(document.getElementById("usuarioId").value);
const idPartida = Number(document.getElementById("idPartida").value);
const palabra = document.getElementById("palabraOculta").value;
const letras = palabra.split("");
let indexLetra = 0;

// === WEBSOCKET ===
function conectarWebSocket() {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/spring/wschat',
        reconnectDelay: 5000,
        onConnect: () => {
            console.log("✅ Conectado al WebSocket");

            stompClient.subscribe(`/topic/juego/${idPartida}`, manejarMensajeServidor);
            stompClient.subscribe(`/user/queue/resultado`, mostrarResultadoIntento);
            stompClient.subscribe(`/topic/mostrarIntento/${idPartida}`, mostrarResultadoIntentoIncorrecto);
            stompClient.subscribe(`/user/queue/comodin`, manejarLetraComodin);
            stompClient.subscribe(`/user/queue/listaUsuarios`, mostrarListaJugadoresModal); // 👈 nuevo
            stompClient.subscribe("/user/queue/bloqueo", manejarBloqueo);
            stompClient.subscribe("/user/queue/desbloqueo", manejarDesbloqueo);


        },
    });

    stompClient.activate();
}

// === ENVÍA INTENTO ===
function enviarIntento(palabra) {
    stompClient.publish({
        destination: "/app/juego/intento",
        body: JSON.stringify({ intentoPalabra: palabra, usuarioId, idPartida, tiempoRestante })
    });
}

// === RECIBE MENSAJES DE EVENTOS GENERALES ===
function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);

    if (data.tipo === "actualizar-puntajes" || data.tipo === "inicio-ronda") {
        actualizarRanking(data.jugadores);
        if (data.tipo === "inicio-ronda") {
            document.getElementById("palabraOculta").value = data.palabra;
            document.getElementById("definicionActual").textContent = data.definicion;
        }
    } else if (data.tipo === "fin-ronda") {
        detenerTimers();
        window.location.href = `/juego?ronda=${data.siguienteRonda}&usuarioId=${usuarioId}`;
    }
}

// === RESULTADO DE INTENTO CORRECTO (privado) ===
function mostrarResultadoIntento(mensaje) {
    const data = JSON.parse(mensaje.body);
    mostrarMensajeChat(data.palabraCorrecta, data.esCorrecto);
}

// === RESULTADO DE INTENTO INCORRECTO (público) ===
function mostrarResultadoIntentoIncorrecto(mensaje) {
    const data = JSON.parse(mensaje.body);

    let texto = data.esCorrecto
        ? `<strong>${data.jugador}</strong> ha acertado la palabra`
        : `<strong>${data.jugador}</strong>: ${data.palabraIncorrecta}`;

    mostrarMensajeChat(texto, data.esCorrecto);
}

// === COMODÍN: REVELAR LETRA ===
function manejarLetraComodin(mensaje) {
    const data = JSON.parse(mensaje.body);
    const letraSpan = document.getElementById(`letra-${data.indice}`);
    if (letraSpan) {
        letraSpan.textContent = data.letra;
        letraSpan.classList.remove("oculto");
        letraSpan.classList.add("comodin-letra");
    }
}

// === COMODÍN: MOSTRAR MODAL CON JUGADORES PARA BLOQUEAR ===
function mostrarListaJugadoresModal(mensaje) {
    const data = JSON.parse(mensaje.body);
    const lista = document.getElementById("listaJugadores");
    lista.innerHTML = "";

    data.usuarios.forEach(nombre => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.textContent = nombre;

        const boton = document.createElement("button");
        boton.className = "btn btn-sm btn-outline-danger";
        boton.textContent = "Bloquear";
        boton.onclick = () => {
            bloquearUsuarioDesdeModal(nombre);
            boton.disabled = true;
        };

        li.appendChild(boton);
        lista.appendChild(li);
    });

    const modal = new bootstrap.Modal(document.getElementById("modalBloqueo"));
    modal.show();
}

// === ENVIAR BLOQUEO AL SERVIDOR ===
function bloquearUsuarioDesdeModal(usuarioABloquear) {
    stompClient.publish({
        destination: "/app/juego/bloquearUsuario",
        body: JSON.stringify({
            idPartida,
            idUsuario: usuarioId,
            usuarioABloquear
        })
    });
    // Deshabilitar el botón de bloquear para evitar múltiples usos
    document.getElementById("btn-bloquear-usuario").disabled = true;

    // Cerrar el modal
    bootstrap.Modal.getInstance(document.getElementById('modalJugadores')).hide();

}

function manejarBloqueo(mensaje) {
    const data = JSON.parse(mensaje.body);
    console.log("🚫 Bloqueado por:", data.mensaje);

    const input = document.getElementById("input-intento");
    input.disabled = true;
    input.placeholder = "Estás bloqueado por 10 segundos...";

    const btnComodin = document.getElementById("btn-comodin");
    btnComodin.disabled = true;

    mostrarMensajeChat("🔒 " + data.mensaje, false);
}

function manejarDesbloqueo(mensaje) {
    const data = JSON.parse(mensaje.body);
    console.log("✅ Desbloqueado:", data.mensaje);

    const input = document.getElementById("input-intento");
    input.disabled = false;
    input.placeholder = "Escribí la palabra...";

    const btnComodin = document.getElementById("btn-comodin");
    btnComodin.disabled = false;

    mostrarMensajeChat("🔓 " + data.mensaje, true);
}


// === ACTUALIZAR RANKING ===
function actualizarRanking(jugadores) {
    const contenedor = document.querySelector(".ranking-horizontal");
    contenedor.innerHTML = "";

    const colores = ["#ec4899", "#22c55e", "#3b82f6", "#a855f7", "#6b7280"];

    jugadores.forEach((j, index) => {
        const jugadorDiv = document.createElement("div");
        jugadorDiv.classList.add("jugador", "d-flex", "align-items-center", "gap-2", "px-3", "py-2", "rounded", "shadow-sm");
        jugadorDiv.style.backgroundColor = colores[index % colores.length];
        jugadorDiv.style.color = "#fff";

        const avatar = document.createElement("div");
        avatar.className = "avatar";
        avatar.style.width = "12px";
        avatar.style.height = "12px";
        avatar.style.backgroundColor = "white";
        avatar.style.borderRadius = "2px";

        const nombreSpan = document.createElement("span");
        nombreSpan.textContent = j.nombre;

        const puntajeSpan = document.createElement("span");
        puntajeSpan.innerHTML = `(<span class="badge bg-secondary">${j.puntaje}</span> pts)`;

        jugadorDiv.appendChild(avatar);
        jugadorDiv.appendChild(nombreSpan);
        jugadorDiv.appendChild(puntajeSpan);
        contenedor.appendChild(jugadorDiv);
    });
}

// === TEMPORIZADOR ===
function iniciarTemporizador() {
    const text = document.getElementById("temporizadorText");
    const progress = document.getElementById("progressCircle");
    const circ = 2 * Math.PI * 45;

    intervaloTemporizador = setInterval(() => {
        if (tiempoRestante <= 0) {
            detenerTimers();
            stompClient.publish({
                destination: "/app/juego/fin-ronda",
                body: JSON.stringify({ idPartida })
            });
        } else {
            text.textContent = tiempoRestante;
            progress.style.strokeDashoffset = circ - (tiempoRestante / 60) * circ;
            tiempoRestante--;
        }
    }, 1000);
}

// === MOSTRAR LETRAS AUTOMÁTICO ===
function mostrarLetras() {
    intervaloLetras = setInterval(() => {
        if (indexLetra < letras.length) {
            const letraSpan = document.getElementById(`letra-${indexLetra}`);
            if (letraSpan) {
                letraSpan.textContent = letras[indexLetra];
                letraSpan.classList.remove("oculto");
            }
            indexLetra++;
        } else {
            clearInterval(intervaloLetras);
        }
    }, 15000);
}

// === DETENER TIMERS ===
function detenerTimers() {
    if (finRondaEjecutada) return;
    finRondaEjecutada = true;
    clearInterval(intervaloTemporizador);
    clearInterval(intervaloLetras);
}

// === CHAT (palabras mencionadas) ===
function mostrarMensajeChat(texto, esCorrecto) {
    const div = document.createElement("div");
    div.className = "message-bubble " + (esCorrecto ? "mensaje-correcto" : "mensaje-incorrecto");
    div.innerHTML = `<p class="message-text">${texto}</p>`;
    const contenedorChat = document.getElementById("palabras-mencionadas");
    contenedorChat.appendChild(div);
    contenedorChat.scrollTop = contenedorChat.scrollHeight;
}

// === ABANDONAR ===
function abandonarPartida() {
    const params = new URLSearchParams({ usuarioId, idPartida });
    navigator.sendBeacon("/spring/abandonarPartida?" + params.toString());
}

// === INICIALIZACIÓN ===
document.addEventListener("DOMContentLoaded", () => {
    conectarWebSocket();
    iniciarTemporizador();
    mostrarLetras();

    document.getElementById("input-intento").addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            const palabra = this.value.trim();
            if (palabra !== "") {
                enviarIntento(palabra);
                this.value = "";
            }
        }
    });

    document.getElementById("btn-comodin").addEventListener("click", () => {
        stompClient.publish({
            destination: "/app/juego/activarComodin",
            body: JSON.stringify({ idPartida, idUsuario: usuarioId })
        });
        document.getElementById("btn-comodin").disabled = true;
    });

    document.getElementById("btn-bloquear-usuario").addEventListener("click", () => {
        stompClient.publish({
            destination: "/app/juego/obtenerUsuarios",
            body: JSON.stringify({ idPartida })
        });
    });

    window.addEventListener("beforeunload", abandonarPartida);
});


