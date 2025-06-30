let stompClient = null;
let tiempoRestante = 60;
let intervaloTemporizador;
let intervaloLetras;
let finRondaEjecutada = false;

const usuarioId = Number(document.getElementById("usuarioId").value);
const partidaId = Number(document.getElementById("partidaId").value);
const palabra = document.getElementById("palabraOculta").value;
const letras = palabra.split("");
let indexLetra = 0;

// === WEBSOCKET ===
function conectarWebSocket() {
    const socket = new SockJS("/spring/wschat");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        // Suscribimos a los topics para recibir mensajes
        stompClient.subscribe(`/topic/juego/${partidaId}`, manejarMensajeServidor);
        stompClient.subscribe(`/user/queue/resultado`, mostrarResultadoIntento);
        stompClient.subscribe(`/topic/mostrarIntento/${partidaId}`, mostrarResultadoIntentoIncorrecto);

        // Ya conectado, iniciamos la ronda
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
        intentoPalabra: palabra,
        usuarioId,
        partidaId,
        tiempoRestante
    }));

//    stompClient.send("/app/juego/verificarAvanceDeRonda", {}, JSON.stringify({
//        usuarioId,
//        partidaId,
//        tiempoRestante
//    }));
}

// === RECIBE MENSAJE DEL SERVIDOR ===
function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);

    if (data.tipo === "actualizar-puntajes") {
        actualizarRanking(data.jugadores);
    } else if (data.tipo === "fin-ronda") {
        detenerTimers();
        window.location.href = `/juego?ronda=${data.siguienteRonda}&usuarioId=${usuarioId}`;
    }
}

// === RESULTADO DEL INTENTO (Privado) ===
function mostrarResultadoIntento(mensaje) {
    const data = JSON.parse(mensaje.body);
    mostrarMensajeChat(data.palabraCorrecta, data.correcto);
}

// === RESULTADO DEL INTENTO INCORRECTO (Público) ===
function mostrarResultadoIntentoIncorrecto(mensaje) {

 console.log("MENSAJE CRUDO:", mensaje); // Esto te da el objeto recibido
    console.log("BODY CRUDO:", mensaje.body)
    const data = JSON.parse(mensaje.body);
    mostrarMensajeChat(data.palabraIncorrecta, data.esCorrecto);
}

// === RANKING ACTUALIZADO ===
function actualizarRanking(jugadores) {
    const contenedor = document.querySelector(".ranking-horizontal");
    contenedor.innerHTML = ""; // Limpiar antes de agregar jugadores nuevos

    jugadores.forEach(j => {
        const div = document.createElement("div");
        div.className = "jugador" + (j.correcto ? " correcto" : "");
        div.innerHTML = `
            <div class="avatar"></div> 
            <span>${j.nombre}</span> 
            (<span class="badge bg-secondary">${j.puntaje}</span> pts)
        `;
        contenedor.appendChild(div);
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
});


