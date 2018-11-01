package com.bri64.discord.audio.receive;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class SphinxTranslator {

  private StreamSpeechRecognizer recognizer;
  private String result;
  private boolean done;

  public SphinxTranslator() {
    this.result = null;
    this.done = true;
    try {
      Configuration configuration = new Configuration();
      configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
      configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
      configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
      configuration.setSampleRate(16000);
      this.recognizer = new StreamSpeechRecognizer(configuration);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getResult() {
    return result;
  }

  public boolean isDone() {
    return done;
  }

  public void start(final InputStream stream, TimeFrame time) {
    if (done) {
      done = false;
      new Thread(() -> {
        SpeechResult res = null;
        recognizer.startRecognition(stream, time);
        //System.out.println("[Started rcognziing...]");
        /*for (int i = 0; i < 5; i++) {
          result = recognizer.getResult();
          if (result != null) break;
        }
        if (result != null) {
          if (result.getHypothesis().equalsIgnoreCase(phrase)) {
            found = true;
          }
          System.out.println(result.getHypothesis());
        } else {
          System.out.println("[no match]");
        }*/

        long start = System.currentTimeMillis();
        while (res == null) {
          res = recognizer.getResult();
          if (System.currentTimeMillis() - start >= 10000) {
            break;
          }
        }
        if (res != null) {
          result = res.getHypothesis();
        }
        recognizer.stopRecognition();
        done = true;
      }).start();
    }
  }
}
