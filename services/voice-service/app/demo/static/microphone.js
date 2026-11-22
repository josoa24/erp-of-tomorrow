async function openMicrophone(context, log, deviceId) {
  log(`traitement audio créé : ${context.state}, ${context.sampleRate} Hz`);
  const constraints = { channelCount: 1, echoCancellation: true, noiseSuppression: true, autoGainControl: true };
  if (deviceId) constraints.deviceId = { exact: deviceId };
  const stream = await navigator.mediaDevices.getUserMedia({ audio: constraints });
  const track = stream.getAudioTracks()[0];
  const settings = track.getSettings();
  log(`micro : « ${track.label || "inconnu"} », ${track.readyState}, actif=${track.enabled}, coupé=${track.muted}`);
  log(`réglages micro : ${JSON.stringify(settings)}`);
  await logDevices(log);
  track.onmute = () => log("le micro a été coupé par le système");
  track.onunmute = () => log("le micro est de nouveau actif");
  track.onended = () => log("le micro s'est arrêté");
  await context.resume();
  log(`traitement audio : ${context.state}, ${context.sampleRate} Hz`);
  await context.audioWorklet.addModule("/demo/static/pcm-worklet.js");
  const node = new AudioWorkletNode(context, "pcm-capture");
  const source = context.createMediaStreamSource(stream);
  source.connect(node);
  node.connect(context.destination);
  return { stream, context, source, node };
}

async function logDevices(log) {
  const devices = await navigator.mediaDevices.enumerateDevices();
  const inputs = devices.filter((device) => device.kind === "audioinput");
  log(`micros disponibles : ${inputs.map((device) => device.label || device.deviceId).join(" | ")}`);
}

function closeMicrophoneResources(audio) {
  audio.stream.getTracks().forEach((track) => track.stop());
  audio.context.close();
}
