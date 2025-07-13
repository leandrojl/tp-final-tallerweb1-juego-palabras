
let stompClient = null;
let tiempoRestante = 60;
let intervaloTemporizador;
let intervaloLetras;
let finRondaEjecutada = false;

const idUsuario = sessionStorage.getItem('idUsuario');
const idPartida = sessionStorage.getItem('idPartida');


//const idUsuario = Number(document.getElementById("usuarioId").value);
//const idPartida = Number(document.getElementById("idPartida").value);

const palabra = document.getElementById("palabraOculta").value;
const letras = palabra.split("");
let indexLetra = 0;

// === WEBSOCKET ===
function conectarWebSocket() {
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const host = window.location.host; // ej: localhost:8080 o scary-propecia-reno-chest.trycloudflare.com
    const brokerURL = `${protocol}://${host}/spring/wschat`; // <-- corregido

    stompClient = new StompJs.Client({
        brokerURL: brokerURL,
        reconnectDelay: 5000,
        onConnect: () => {
            console.log(`✅ Conectado al WebSocket en: ${brokerURL}`);


                stompClient.subscribe(`/topic/juego/${idPartida}`, manejarMensajeServidor);
                stompClient.subscribe(`/user/queue/resultado`, mostrarResultadoIntento);
                stompClient.subscribe(`/topic/verRanking/${idPartida}`, actualizarRanking);
                stompClient.subscribe(`/topic/mostrarIntento/${idPartida}`, mostrarResultadoIntentoIncorrecto);
                stompClient.subscribe("/topic/redirigir", function(message) {
                    const url = message.body;
                    window.location.href = url;

                });

                stompClient.subscribe(`/topic/timerInicioRonda/${idPartida}`, mensajeDelServidorAlChat);


            iniciarRonda();
        },
        onStompError: (frame) => {
            console.error('❌ Error STOMP:', frame);
        },
        onWebSocketError: (event) => {
            console.error('❌ Error WebSocket:', event);
        }
    });

    stompClient.activate();
}


function mensajeDelServidorAlChat(mensaje) {
    const valor = mensaje.body.trim();

    // Si es un número del 1 al 5 → parte de cuenta regresiva
    if (!isNaN(valor) && Number(valor) >= 1 && Number(valor) <= 5) {
        iniciarCuentaRegresivaDesde(Number(valor));
    }
    // Si no es un número → lo mostramos como mensaje final centrado
    else {
        mostrarMensajeCentrado(valor);
    }
}


// === ENVÍA INTENTO ===
function enviarIntento(palabra) {
    stompClient.publish({
      destination: "/app/juego/intento",
      body: JSON.stringify({
        intentoPalabra: palabra,
        idUsuario,
        idPartida,
        tiempoRestante
      })
    });

//    stompClient.send("/app/juego/verificarAvanceDeRonda", {}, JSON.stringify({
//        idUsuario,
//        partidaId,
//        tiempoRestante
//    }));
}

// === RECIBE MENSAJE DEL SERVIDOR ===/*
/*function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);
        if (data.tipo === "inicio-ronda") {
            document.getElementById("palabraOculta").value = data.palabra;
            document.getElementById("definicionActual").textContent = data.definicion;
        } else if (data.tipo === "fin-ronda") {
        detenerTimers();
        window.location.href = `/juego?ronda=${data.siguienteRonda}&idUsuario=${idUsuario}`;
    }
}*/

function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);
    reconstruirPalabra(data.palabra);
        document.getElementById("definicionActual").textContent = data.definicionTexto;
        document.getElementById("rondaActual").textContent = data.numeroDeRonda;
        tiempoRestante      = 60;
        finRondaEjecutada   = false;
        indexLetra          = 0;

        letras.length = 0;
        data.palabra.split("").forEach(l => letras.push(l));
        const input = document.getElementById("input-intento");
        input.disabled    = false;
        input.placeholder = "";
        input.classList.remove("input-desactivado");
        mostrarLetras();


}
function reconstruirPalabra(palabra) {
    document.getElementById("palabraOculta").value = palabra;
    const contenedor = document.getElementById("contenedor-palabra");
    contenedor.innerHTML = '';
    const inputHidden = document.createElement("input");
    inputHidden.type = "hidden";
    inputHidden.id = "palabraOculta";
    inputHidden.value = palabra;
    contenedor.appendChild(inputHidden);
    for (let i = 0; i < palabra.length; i++) {
        const span = document.createElement("span");
        span.className = "bloque-letra oculto";
        span.id = "letra-" + i;
        contenedor.appendChild(span);
    }
}

// === RESULTADO DEL INTENTO (Privado) ===
function mostrarResultadoIntento(mensaje) {
    const data = JSON.parse(mensaje.body);
    humano=true;
    mostrarMensajeChat(data.palabraCorrecta, data.esCorrecto, humano); // palabra en verde
}

// === RESULTADO DEL INTENTO INCORRECTO (Público) ===
function mostrarResultadoIntentoIncorrecto(mensaje) {
    const data = JSON.parse(mensaje.body);
    humano=true;
    let textoIncorrecto = `<strong>${data.jugador}</strong>: ${data.palabraIncorrecta}`;
    let textoCorrecto = `<strong>${data.jugador}</strong> ha acertado la palabra`
    if (data.esCorrecto) {
        mostrarMensajeChat(textoCorrecto, data.esCorrecto, humano); // Ej: "Pepito acerto"
    } else {
        mostrarMensajeChat(textoIncorrecto, data.esCorrecto, humano); // palabra en rojo
    }
}

// === RANKING ACTUALIZADO ===
function actualizarRanking(mensaje) {
    const data = JSON.parse(mensaje.body);
    console.log("Ranking recibido:", data);
    const jugadores = data.jugadores;
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
     // Desactivar el input
        const input = document.getElementById("input-intento");
        input.disabled = true;
        input.placeholder = "Tiempo agotado";
        input.classList.add("input-desactivado");
}

// === CHAT LOCAL (Palabras Mencionadas) ===
function mostrarMensajeChat(texto, esCorrecto, humano) {
    const div = document.createElement("div");
    console.log("Intento:", texto, "¿Es correcto?", esCorrecto);
    if(humano) {div.className = "message-bubble " + (esCorrecto ? "mensaje-correcto" : "mensaje-incorrecto");}
    else {div.className = "message-bubble " + "mensaje-servidor";}
    div.innerHTML = `<p class="message-text">${texto}</p>`;
    const contenedorChat =  document.getElementById("palabras-mencionadas");
    contenedorChat.appendChild(div);
    contenedorChat.scrollTop = contenedorChat.scrollHeight; // scroll automático
}

// === ABANDONAR PARTIDA ===
function abandonarPartida() {
    const params = new URLSearchParams({
        idUsuario: jugadorId,
        idPartida: idPartida
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

function iniciarCuentaRegresivaDesde(valorInicial) {
    let contenedor = document.getElementById("cuenta-regresiva");
    if (!contenedor) {
        contenedor = document.createElement("div");
        contenedor.id = "cuenta-regresiva";
        document.body.appendChild(contenedor);
    }

    Object.assign(contenedor.style, {
        position: "fixed",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        zIndex: "9999",
        fontSize: "8rem",
        fontWeight: "bold",
        color: "#ffffff",
        textShadow: "0 0 25px rgba(255,255,255,0.7)",
        pointerEvents: "none",
        opacity: "0",
        transition: "opacity 0.4s ease, transform 0.4s ease"
    });

    let valor = valorInicial;

    const intervalo = setInterval(() => {
        if (valor <= 0) {
            contenedor.remove();
            clearInterval(intervalo);
            return;
        }

        contenedor.textContent = valor;

        contenedor.style.opacity = "0";
        contenedor.style.transform = "translate(-50%, -50%) scale(0.6)";
        void contenedor.offsetWidth;

        contenedor.style.opacity = "1";
        contenedor.style.transform = "translate(-50%, -50%) scale(1.2)";

        setTimeout(() => {
            contenedor.style.opacity = "0";
            contenedor.style.transform = "translate(-50%, -50%) scale(0.9)";
        }, 600);

        valor--;
    }, 1000);
}


function mostrarMensajeCentrado(texto) {
    const div = document.createElement("div");
    div.textContent = texto;

    Object.assign(div.style, {
        position: "fixed",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%) scale(0.8)",
        zIndex: "9999",
        background: "linear-gradient(135deg, #00c853, #38a6c7)",
        color: "#fff",
        padding: "20px 50px",
        fontSize: "2.5rem",
        fontWeight: "bold",
        borderRadius: "25px",
        textAlign: "center",
        boxShadow: "0 0 40px rgba(0,0,0,0.8)",
        opacity: "0",
        transition: "opacity 0.5s ease, transform 0.5s ease",
        pointerEvents: "none"
    });

    document.body.appendChild(div);

    // Fade in + zoom
    setTimeout(() => {
        div.style.opacity = "1";
        div.style.transform = "translate(-50%, -50%) scale(1.1)";
    }, 10);

    // Fade out + shrink
    setTimeout(() => {
        div.style.opacity = "0";
        div.style.transform = "translate(-50%, -50%) scale(0.7)";
    }, 2500);

    // Cleanup
    setTimeout(() => {
        div.remove();
    }, 3200);
}

