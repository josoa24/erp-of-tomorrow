const TARGET_RATE = 16000;
const CHUNK_SAMPLES = 4000;

class PcmCapture extends AudioWorkletProcessor {
  constructor() {
    super();
    this.step = sampleRate / TARGET_RATE;
    this.position = 0;
    this.previous = 0;
    this.chunk = new Int16Array(CHUNK_SAMPLES);
    this.length = 0;
  }

  process(inputs) {
    const channel = inputs[0][0];
    if (channel) {
      this.resample(channel);
    }
    return true;
  }

  resample(input) {
    while (this.position < input.length) {
      const index = Math.floor(this.position);
      const fraction = this.position - index;
      const before = index === 0 ? this.previous : input[index - 1];
      const sample = before + (input[index] - before) * fraction;
      this.push(sample);
      this.position += this.step;
    }
    this.position -= input.length;
    this.previous = input[input.length - 1];
  }

  push(sample) {
    this.chunk[this.length++] = Math.max(-1, Math.min(1, sample)) * 0x7fff;
    if (this.length === CHUNK_SAMPLES) {
      this.port.postMessage(this.chunk.buffer.slice(0));
      this.length = 0;
    }
  }
}

registerProcessor("pcm-capture", PcmCapture);
