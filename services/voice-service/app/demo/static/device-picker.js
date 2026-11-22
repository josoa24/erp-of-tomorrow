const DEVICE_STORAGE_KEY = "voice-demo-microphone";

function createDevicePicker(select) {
  async function refresh() {
    const devices = await navigator.mediaDevices.enumerateDevices();
    const inputs = devices.filter((device) => device.kind === "audioinput" && device.deviceId !== "communications");
    const saved = readSaved();
    select.innerHTML = "";
    select.append(new Option("Micro par défaut du système", ""));
    inputs
      .filter((device) => device.deviceId !== "default")
      .forEach((device, index) => select.append(new Option(device.label || `Micro ${index + 1}`, device.deviceId)));
    select.value = inputs.some((device) => device.deviceId === saved) ? saved : "";
  }

  function selected() {
    return select.value;
  }

  function remember(deviceId) {
    try {
      localStorage.setItem(DEVICE_STORAGE_KEY, deviceId);
    } catch (error) {
      return;
    }
  }

  function readSaved() {
    try {
      return localStorage.getItem(DEVICE_STORAGE_KEY) || "";
    } catch (error) {
      return "";
    }
  }

  select.addEventListener("change", () => remember(select.value));
  navigator.mediaDevices.addEventListener("devicechange", refresh);
  refresh();
  return { refresh, selected };
}
