// const [nav] = performance.getEntriesByType("navigation");
// if (nav.type === "reload") {
//     // Si el tipo es 'reload', redirigimos inmediatamente al lobby.
//     // Esto evita que el usuario entre a una partida con el estado del script roto.
//     window.location.href = "/spring/lobby";
// }
const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
const host = window.location.host; // ej: localhost:8080 o scary-propecia-reno-chest.trycloudflare.com
let stompClient = null;
let tiempoRestante = 60;
let intervaloTemporizador;
let intervaloLetras;
let finRondaEjecutada = false;
let monedas = document.getElementById("monedas-usuario").textContent;
const monedasSpan = document.getElementById("monedas-usuario");

const idUsuario = sessionStorage.getItem('idUsuario');
const idPartida = sessionStorage.getItem('idPartida');
// === COLORES POR JUGADOR ===
const coloresJugadores = new Map();
// Lista sin ningún verde
const listaColores = [
    "#ec4899", // rosa
    "#3b82f6", // azul claro
    "#a855f7", // violeta
    "#f97316", // naranja
    "#14b8a6", // cian fuerte (si lo querés evitar, cámbialo)
    "#eab308", // amarillo
    "#8b5cf6", // violeta claro
    "#ff6f61", // coral
    "#ff8c00", // naranja oscuro
    "#ff1493", // rosa fuerte
];
let colorIndex = 0;

function obtenerColorJugador(nombreJugador) {
    if (!coloresJugadores.has(nombreJugador)) {
        const colorPropuesto = nombreJugador === document.getElementById("usuarioNombre").value
            ? "#0084ff" // Azul fijo solo para el jugador actual
            : listaColores[colorIndex % listaColores.length];

        // Evita verde y azul del sistema
        if (colorPropuesto === "#4caf50" || colorPropuesto === "#29b6f6") {
            colorIndex++;
        }

        const colorFinal = listaColores[colorIndex % listaColores.length];
        coloresJugadores.set(nombreJugador, colorFinal);
        colorIndex++;
    }
    return coloresJugadores.get(nombreJugador);
}


const palabra = document.getElementById("palabraOculta").value;
const letras = palabra.split("");
let indexLetra = 0;

// === WEBSOCKET ===
function conectarWebSocket() {
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
                stompClient.subscribe(`/user/queue/comodin`, manejarLetraComodin);
                stompClient.subscribe(`/user/queue/listaUsuarios`, mostrarListaJugadoresModal); // 👈 nuevo
                stompClient.subscribe("/user/queue/bloqueo", manejarBloqueo);
                stompClient.subscribe("/user/queue/desbloqueo", manejarDesbloqueo);
                stompClient.subscribe(`/topic/usuarioAbandonoPartida/${idPartida}`,eliminarDivDeUsuario)
                stompClient.subscribe("/user/queue/abandonarPartida",redireccionarALobby)

                stompClient.subscribe("/topic/redirigir", function(message) {
                    const url = message.body;
                    window.location.href = url;

                });

                stompClient.subscribe(`/topic/timerInicioRonda/${idPartida}`, mensajeDelServidorAlChat);

            stompClient.publish({
                destination: "/app/pedirRanking",
                body: JSON.stringify({
                        message: "",
                        number : idPartida,
                })

            });

            //iniciarRonda();
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
}

function manejarMensajeServidor(mensaje) {
    const data = JSON.parse(mensaje.body);
    reconstruirPalabra(data.palabra);
        document.getElementById("definicionActual").textContent = data.definicionTexto;
        document.getElementById("rondaActual").textContent = data.numeroDeRonda;
        document.getElementById("rondasTotales").textContent = data.rondasTotales;
        tiempoRestante      = 60;
        finRondaEjecutada   = false;
        indexLetra          = 0;

        letras.length = 0;
        data.palabra.split("").forEach(l => letras.push(l));
        const input = document.getElementById("input-intento");
        input.disabled    = false;
        input.placeholder = "";
        input.classList.remove("input-desactivado");


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

// === RESULTADO DE INTENTO CORRECTO (privado) ===
function mostrarResultadoIntento(mensaje) {
    const data = JSON.parse(mensaje.body);

    humano=true;
    mostrarMensajeChat(data.palabraCorrecta, data.esCorrecto, humano); // palabra en verde

}

// === RESULTADO DE INTENTO INCORRECTO (público) ===
function mostrarResultadoIntentoIncorrecto(mensaje) {
    const data = JSON.parse(mensaje.body);
    humano = true;

    const colorJugador = obtenerColorJugador(data.jugador);


    let texto = data.esCorrecto
        ? `<strong>${data.jugador}</strong> ha acertado la palabra`
        : `<strong>${data.jugador}</strong>: ${data.palabraIncorrecta}`;


    mostrarMensajeChat(texto, data.esCorrecto, humano, colorJugador);
}

// === COMODÍN: REVELAR LETRA ===
function manejarLetraComodin(mensaje) {
    const data = JSON.parse(mensaje.body);
    const letraSpan = document.getElementById(`letra-${data.indice}`);

    if (letraSpan && letraSpan.classList.contains("oculto")) {
        letraSpan.textContent = data.letra;
        letraSpan.classList.remove("oculto");
        letraSpan.classList.add("comodin-letra");
    }else{


        if(!consultarEstadoPalabra()) {
            let letraRepetida = true;
            activarComodinLetra(letraRepetida);
        }
    }


}
function consultarEstadoPalabra(){
    const spans = document.querySelectorAll('[id^="letra-"]'); // todos los que empiezan con "letra-"

    return Array.from(spans).every(span => !span.classList.contains("oculto"));
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
            idUsuario: idUsuario,
            usuarioABloquear
        })
    });
    // Deshabilitar el botón de bloquear para evitar múltiples usos
  //  document.getElementById("btn-bloquear-usuario").disabled = true;

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




// === TEMPORIZADOR ===
function iniciarTemporizador() {
    const text = document.getElementById("temporizadorText");
    const progress = document.getElementById("progressCircle");
    const circ = 2 * Math.PI * 45;

    intervaloTemporizador = setInterval(() => {
        if (tiempoRestante <= 0) {

            //detenerTimers();

           // detenerTimers();

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

// === DETENER TIMERS ===>

// === CHAT LOCAL (Palabras Mencionadas) ===
function mostrarMensajeChat(texto, esCorrecto, humano, colorPersonalizado = null) {
    const div = document.createElement("div");
    div.className = "message-bubble";

    if (!humano) {
        div.classList.add("mensaje-servidor");
    } else if (esCorrecto) {
        div.classList.add("mensaje-correcto");
    } else if (!colorPersonalizado) {
        div.classList.add("mensaje-incorrecto"); // solo si no hay color personalizado
    }

    // Si tiene color personalizado (jugador), aplicalo
    if (humano && colorPersonalizado) {
        div.style.backgroundColor = colorPersonalizado;
        div.style.color = "white";
    }

    div.innerHTML = `<p class="message-text mb-0">${texto}</p>`;

    const contenedorChat = document.getElementById("palabras-mencionadas");
    contenedorChat.appendChild(div);
    contenedorChat.scrollTop = contenedorChat.scrollHeight;
}


// === ABANDONAR PARTIDA ===
// function abandonarPartida() {
//     const params = new URLSearchParams({
//         idUsuario: idUsuario,
//         idPartida: idPartida
//     });
//
//     navigator.sendBeacon("/spring/abandonarPartida?" + params.toString());
// }

function actualizarMonedasDisplay() {
    monedasSpan.textContent = monedas;
}

function activarComodinLetra(letraRepetida) {
    stompClient.publish({
        destination: "/app/juego/activarComodin",
        body: JSON.stringify({ idPartida, idUsuario: idUsuario, letraRepetida })
    });
}

// === INICIALIZACIÓN ===
document.addEventListener("DOMContentLoaded", () => {
    conectarWebSocket();
    iniciarTemporizador();
    //mostrarLetras();

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

    document.getElementById("btn-comodin").addEventListener("click", function () {

        let letraRepetida;
        if(!consultarEstadoPalabra()){
        if (monedas >= 75) {
            monedas -= 75;
            actualizarMonedasDisplay();
            letraRepetida = false;
            activarComodinLetra(letraRepetida);


            //   document.getElementById("btn-comodin").disabled = true;
        } else {
            alert("No tenés suficientes monedas para usar un comodín.");
        }
        } else   alert("Ya no hay letras para revelar");
    });

    document.getElementById("btn-bloquear-usuario").addEventListener("click", function () {
        if (monedas >= 100) {
            monedas -= 100;
            actualizarMonedasDisplay();

            stompClient.publish({
                destination: "/app/juego/obtenerUsuarios",
                body: JSON.stringify({ idPartida })
            });

            // desactivás el botón temporalmente si querés:
            // document.getElementById("btn-bloquear-usuario").disabled = true;
        } else {
            alert("No tenés suficientes monedas para bloquear.");
        }
    });actualizarMonedasDisplay(); // Al cargar

    //window.addEventListener("beforeunload", abandonarPartida);
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


// === ACTUALIZAR RANKING ===
// === RANKING ACTUALIZADO ===
function actualizarRanking(mensaje) {
    const data = JSON.parse(mensaje.body);
    console.log("Ranking recibido:", data);
    const jugadores = data.jugadores;
    const contenedor = document.querySelector(".ranking-horizontal");
    contenedor.innerHTML = "";

    jugadores.forEach((j) => {
        const colorJugador = obtenerColorJugador(j.nombre);

        const jugadorDiv = document.createElement("div");
        jugadorDiv.id = j.nombre; // Asignamos el nombre como ID
        jugadorDiv.classList.add("jugador", "d-flex", "align-items-center", "gap-2", "px-3", "py-2", "rounded", "shadow-sm");
        jugadorDiv.style.backgroundColor = colorJugador;
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


function eliminarDivDeUsuario(mensaje) {
    const nombre = mensaje.body; // Llega como string simple
    const jugadorDiv = document.getElementById(nombre);
    if (jugadorDiv) {
        jugadorDiv.remove();
        console.log(`Jugador eliminado: ${nombre}`);
    } else {
        console.warn(`No se encontró el div del jugador: ${nombre}`);
    }
}

function redireccionarALobby(mensaje) {
    const data = JSON.parse(mensaje.body);
    const destino = window.location.origin + data.message; // origen + /spring/lobby
    console.log("🔁 Redirigiendo a:", destino);
    window.location.href = destino;
}


