const MAX_DEBUG_LINES = 200;

function createDebugLog(element) {
  const lines = [];
  let pending = [];
  let socket = null;

  function log(message) {
    const time = new Date().toLocaleTimeString("fr-FR", { hour12: false });
    lines.push(`${time}  ${message}`);
    if (lines.length > MAX_DEBUG_LINES) lines.shift();
    element.textContent = lines.join("\n");
    element.scrollTop = element.scrollHeight;
    pending.push(message);
    flush();
  }

  function attach(openSocket) {
    socket = openSocket;
    flush();
  }

  function flush() {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;
    pending.forEach((message) => socket.send(JSON.stringify({ type: "log", message })));
    pending = [];
  }

  return { log, attach };
}

function createAudioStats(log) {
  let samples = 0;
  let peak = 0;
  let timer = null;

  function add(buffer) {
    const chunk = new Int16Array(buffer);
    samples += chunk.length;
    for (const value of chunk) peak = Math.max(peak, Math.abs(value) / 32768);
  }

  function start() {
    samples = 0;
    timer = setInterval(() => {
      log(`audio envoyé : ${(samples / 16000).toFixed(1)} s, niveau max sur 2 s : ${peak.toFixed(3)}`);
      peak = 0;
    }, 2000);
  }

  function stop() {
    clearInterval(timer);
  }

  return { add, start, stop };
}
