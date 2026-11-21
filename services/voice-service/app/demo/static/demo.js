const button = document.getElementById("toggle");
const statusLabel = document.getElementById("status");
const live = document.getElementById("live");
const history = document.getElementById("history");
const languageSelect = document.getElementById("language");
const meter = createMicMeter(document.getElementById("meter"), document.getElementById("meter-label"));
const debug = createDebugLog(document.getElementById("debug"));
const stats = createAudioStats(debug.log);
const devices = createDevicePicker(document.getElementById("device"));

let socket = null;
let audio = null;
let starting = false;

button.addEventListener("click", () => (starting ? null : socket ? stop() : start()));

async function start() {
  starting = button.disabled = true;
  const context = new AudioContext();
  try {
    setStatus("Autorisez le micro…");
    audio = await openMicrophone(context, debug.log, devices.selected());
    devices.refresh();
  } catch (error) {
    context.close();
    debug.log("erreur micro : " + error.name + " " + error.message);
    finishStarting("Micro refusé ou indisponible : " + error.message);
    return;
  }
  meter.start(audio.context, audio.source);
  connect();
  finishStarting("Connexion…");
  button.textContent = "Arrêter";
}

function connect() {
  const protocol = location.protocol === "https:" ? "wss" : "ws";
  socket = new WebSocket(`${protocol}://${location.host}/stream?language=${languageSelect.value}`);
  socket.binaryType = "arraybuffer";
  socket.onopen = () => {
    debug.attach(socket);
    debug.log("connecté au voice-service");
    stats.start();
    setStatus("J'écoute… parlez");
  };
  socket.onmessage = (event) => show(JSON.parse(event.data));
  socket.onclose = (event) => reset(`Arrêté (code ${event.code})`);
  socket.onerror = () => debug.log("erreur de connexion au voice-service");
  audio.node.port.onmessage = (event) => {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;
    stats.add(event.data);
    socket.send(event.data);
  };
}

function finishStarting(message) {
  starting = button.disabled = false;
  setStatus(message);
}

function stop() {
  closeMicrophone();
  starting = button.disabled = true;
  setStatus("Fin de la phrase…");
  if (socket.readyState === WebSocket.OPEN) socket.send("stop");
  setTimeout(() => socket && socket.close(), 30000);
}

function show(event) {
  debug.log(`serveur : ${event.type}${event.text !== undefined ? " « " + event.text + " »" : ""}`);
  if (event.type === "done") return socket.close();
  if (event.type === "processing") return (live.textContent = "Transcription en cours…");
  if (event.type === "partial") return (live.textContent = event.text);
  live.textContent = "";
  if (!event.text) return;
  const item = document.createElement("li");
  item.textContent = event.text;
  history.prepend(item);
}

function closeMicrophone() {
  if (!audio) return;
  stats.stop();
  meter.stop();
  closeMicrophoneResources(audio);
  audio = null;
}

function reset(message) {
  closeMicrophone();
  socket = null;
  starting = button.disabled = false;
  button.textContent = "Parler";
  setStatus(message);
  debug.log(message);
}

function setStatus(message) {
  statusLabel.textContent = message;
}
