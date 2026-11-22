const SPEECH_LEVEL = 0.02;
const HISTORY_POINTS = 240;

function createMicMeter(canvas, label) {
  const context2d = canvas.getContext("2d");
  const levels = new Array(HISTORY_POINTS).fill(0);
  let analyser = null;
  let frame = null;

  function start(audioContext, source) {
    analyser = audioContext.createAnalyser();
    analyser.fftSize = 1024;
    source.connect(analyser);
    loop();
  }

  function stop() {
    cancelAnimationFrame(frame);
    analyser = null;
    levels.fill(0);
    draw();
    label.textContent = "Micro coupé";
    label.className = "meter-label";
  }

  function loop() {
    if (!analyser) return;
    const samples = new Float32Array(analyser.fftSize);
    analyser.getFloatTimeDomainData(samples);
    const level = Math.sqrt(samples.reduce((sum, value) => sum + value * value, 0) / samples.length);
    levels.push(level);
    levels.shift();
    const hearing = level >= SPEECH_LEVEL;
    label.textContent = hearing ? "Je vous entends" : "Silence";
    label.className = hearing ? "meter-label hearing" : "meter-label";
    draw();
    frame = requestAnimationFrame(loop);
  }

  function draw() {
    const { width, height } = canvas;
    const style = getComputedStyle(document.documentElement);
    context2d.clearRect(0, 0, width, height);
    const thresholdY = height - Math.min(1, SPEECH_LEVEL * 8) * height;
    context2d.fillStyle = style.getPropertyValue("--muted");
    context2d.fillRect(0, thresholdY, width, 1);
    const barWidth = width / HISTORY_POINTS;
    levels.forEach((level, index) => {
      const barHeight = Math.min(1, level * 8) * height;
      context2d.fillStyle = style.getPropertyValue(level >= SPEECH_LEVEL ? "--accent" : "--muted");
      context2d.fillRect(index * barWidth, height - barHeight, Math.max(1, barWidth - 1), barHeight);
    });
  }

  draw();
  return { start, stop };
}
