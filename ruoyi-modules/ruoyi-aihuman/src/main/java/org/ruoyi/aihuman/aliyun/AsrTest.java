package org.ruoyi.aihuman.aliyun;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AsrTest {

    public static void main(String[] args) throws NoApiKeyException {
        ObjectMapper mapper = new ObjectMapper();
        AsrConfig config;
        try {
            java.io.InputStream in = AsrTest.class.getClassLoader().getResourceAsStream("aihuman/asr-config.json");
            if (in == null) {
                config = new AsrConfig();
                config.setModel("fun-asr-realtime");
                config.setFormat("pcm");
                config.setSampleRate(16000);
                AsrConfig.Audio audio = new AsrConfig.Audio();
                audio.setSampleRate(16000);
                audio.setSampleSizeInBits(16);
                audio.setChannels(1);
                audio.setSigned(true);
                audio.setBigEndian(false);
                audio.setBufferSize(1024);
                audio.setDurationMs(300000);
                audio.setSleepMs(20);
                config.setAudio(audio);
            } else {
                config = mapper.readValue(in, AsrConfig.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (config.getPlatform() != null && !"aliyun-asr".equalsIgnoreCase(config.getPlatform())) {
            System.out.println("ASR platform is " + config.getPlatform() + ", skip aliyun-asr flow");
            System.exit(0);
        }
        Flowable<ByteBuffer> audioSource = Flowable.create(emitter -> {
                    new Thread(() -> {
                        try {
                            AudioFormat audioFormat = new AudioFormat(
                                    config.getAudio().getSampleRate(),
                                    config.getAudio().getSampleSizeInBits(),
                                    config.getAudio().getChannels(),
                                    Boolean.TRUE.equals(config.getAudio().getSigned()),
                                    Boolean.TRUE.equals(config.getAudio().getBigEndian()));
                            // 根据格式匹配默认录音设备
                            TargetDataLine targetDataLine =
                                    AudioSystem.getTargetDataLine(audioFormat);
                            targetDataLine.open(audioFormat);
                            // 开始录音
                            targetDataLine.start();
                            ByteBuffer buffer = ByteBuffer.allocate(config.getAudio().getBufferSize());
                            long start = System.currentTimeMillis();
                            while (System.currentTimeMillis() - start < config.getAudio().getDurationMs()) {
                                int read = targetDataLine.read(buffer.array(), 0, buffer.capacity());
                                if (read > 0) {
                                    buffer.limit(read);
                                    // 将录音音频数据发送给流式识别服务
                                    emitter.onNext(buffer);
                                    buffer = ByteBuffer.allocate(config.getAudio().getBufferSize());
                                    Thread.sleep(config.getAudio().getSleepMs());
                                }
                            }
                            // 通知结束转写
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }).start();
                },
                BackpressureStrategy.BUFFER);

        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("DASHSCOPE_API_KEY");
        }
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getProperty("dashscope.api_key");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("DashScope API key not set. Set env DASHSCOPE_API_KEY or -Ddashscope.api_key.");
        }
        Recognition recognizer = new Recognition();
        RecognitionParam param = RecognitionParam.builder()
                .model(config.getModel())
                .format(config.getFormat())
                .sampleRate(config.getSampleRate())
                .apiKey(apiKey)
                .build();

        // 流式调用接口
        recognizer.streamCall(param, audioSource)
                // 调用Flowable的subscribe方法订阅结果
                .blockingForEach(
                        result -> {
                            // 打印最终结果
                            if (result.isSentenceEnd()) {
                                System.out.println("Fix:" + result.getSentence().getText());
                            } else {
                                System.out.println("Result:" + result.getSentence().getText());
                            }
                        });
        System.exit(0);
    }
}
