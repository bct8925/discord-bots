package com.bri64.discord.audio.receive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.commons.io.FileUtils;

@SuppressWarnings("WeakerAccess")
public class AudioSample {

  public static AudioFormat DiscordInput = new AudioFormat(
      48000, // samplerate
      16, // quantization
      1, // channels
      true, // signed
      true); // bigendian
  public static AudioFormat SphinxOutput = new AudioFormat(
      16000, // samplerate
      16, // quantization
      1, // channels
      true, // signed
      false);

  private static int time = 20;
  private AudioFormat format;
  private List<byte[]> data;

  public AudioSample(final AudioFormat inputFormat) {
    this.format = inputFormat;
    this.data = new ArrayList<>();
  }

  public int getTime() {
    return data.size() * time;
  }

  public void addData(byte[] e) {
    data.add(e);
  }

  public AudioInputStream getStream(AudioFormat outputFormat) {
    byte[] b = makeMono(concatenateArrays());
    AudioInputStream input = new AudioInputStream(new ByteArrayInputStream(b), format, b.length);
    if (outputFormat == null) {
      return input;
    }
    return AudioSystem.getAudioInputStream(outputFormat, input);
  }

  private byte[] concatenateArrays() {
    List<byte[]> currentData = new ArrayList<>(data);
    ByteBuffer buffer = ByteBuffer.allocate(currentData.size() * currentData.get(0).length * 2);
    for (byte[] b : currentData) {
      try {
        buffer.put(b);
      } catch (BufferOverflowException ex) {
        ex.printStackTrace();
        break;
      }
    }
    return buffer.array();
  }

  private byte[] makeMono(byte[] b) {
    byte[] mono = new byte[b.length / 2];
    for (int i = 0; i < b.length; i += 4) {
      mono[(i / 2)] = b[i];
      mono[(i / 2) + 1] = b[i + 1];
    }
    return mono;
  }

  public void write(String file) {
    try {
      FileUtils.writeByteArrayToFile(new File(file), concatenateArrays());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
