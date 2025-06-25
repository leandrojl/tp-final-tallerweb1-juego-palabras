let stompClient = null;
let tiempoRestante = 60;
let intervaloTemporizador;
let intervaloLetras;
let finRondaEjecutada = false;

const jugadorId = document.getElementById("jugadorId").value;
const partidaId = document.getElementById("partidaId").value;
const palabra = document.getElementById("palabraOculta").value;
const letras = palabra.split("");
let indexLetra = 0;

// === WEBSOCKET ===
function conectarWebSocket() {
    const socket = new SockJS("/spring/wschat");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/juego/${partidaId}`, manejarMensajeServidor);
        stompClient.subscribe(`/user/queue/resultado`, mostrarResultadoIntento);
        stompClient.subscribe(`/topic/mostrarIntento`, mostrarResultadoIntentoIncorrecto);

        iniciarRonda();
    });
}

// === INICIA LA RONDA EN EL SERVIDOR ===
function iniciarRonda() {
    stompClient.send("/app/juego/iniciar", {}, JSON.stringify({ partidaId }));
}

// === ENVÍA INTENTO ===
function enviarIntento(palabra) {
    stompClient.send("/app/juego/intento", {}, JSON.stringify({
        intento: palabra,
        jugadorId,
        partidaId,
        tiempoRestante
    }));

    stompClient.send("/app/juego/verificarAvanceDeRonda", {}, JSON.stringify({
        jugadorId,
        partidaId,
        tiempoRestante
    }));
}

// === RECIBE MENSAJE DEL SERVIDOR ===
function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);

    if (data.tipo === "actualizar-puntajes" || data.tipo === "inicio-ronda") {
        actualizarRanking(data.jugadores);
        if (data.tipo === "inicio-ronda") {
            document.getElementById("palabraOculta").value = data.palabra;
            document.getElementById("definicionTexto").textContent = data.definicionTexto;
        }
    } else if (data.tipo === "fin-ronda") {
        detenerTimers();
        window.location.href = `/juego?ronda=${data.siguienteRonda}&jugadorId=${jugadorId}`;
    }
}

// === RESULTADO DEL INTENTO (Privado) ===
function mostrarResultadoIntento(mensaje) {
    const data = JSON.parse(mensaje.body);
    mostrarMensajeChat(data.intento, data.correcto);
}

// === RESULTADO DEL INTENTO INCORRECTO (Público) ===
function mostrarResultadoIntentoIncorrecto(mensaje) {
    const data = JSON.parse(mensaje.body);
    mostrarMensajeChat(data.intento, data.correcto);
}

// === RANKING ACTUALIZADO ===
function actualizarRanking(jugadores) {
    const contenedor = document.querySelector(".ranking-horizontal");
    contenedor.innerHTML = "";

    const nombreActual = document.getElementById("usuarioNombre").value;
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
            stompClient.send("/app/juego/fin-ronda", {}, JSON.stringify({ partidaId }));
        } else {
            text.textContent = tiempoRestante;
            progress.style.strokeDashoffset = circ - (tiempoRestante / 60) * circ;
            tiempoRestante--;
        }
    }, 1000);
}

// === MOSTRAR LETRAS ===
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

// === CHAT LOCAL (Palabras Mencionadas) ===
function mostrarMensajeChat(texto, esCorrecto) {
    const div = document.createElement("div");
    div.className = "message-bubble " + (esCorrecto ? "sent" : "received");
    div.innerHTML = `<p class="message-text">${texto}</p>`;
    document.getElementById("palabras-mencionadas").appendChild(div);
}

// === ABANDONAR PARTIDA ===
function abandonarPartida() {
    const params = new URLSearchParams({
        usuarioId: jugadorId,
        partidaId: partidaId
    });
    navigator.sendBeacon("/spring/abandonarPartida?" + params.toString());
}

// === INICIALIZACIÓN ===
document.addEventListener("DOMContentLoaded", () => {
    conectarWebSocket();
    iniciarTemporizador();
    mostrarLetras();

    const input = document.getElementById("input-intento");
    input.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            const palabra = input.value.trim();
            if (palabra !== "") {
                enviarIntento(palabra);
                input.value = "";
            }
        }
    });

    // Detectar cuando el usuario cierra la pestaña o se va
    window.addEventListener("beforeunload", function () {
        abandonarPartida();
    });
});


