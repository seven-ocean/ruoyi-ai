package org.ruoyi.aihuman.aliyun;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.Constants;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class TimeUtils {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getTimestamp() {
        return LocalDateTime.now().format(formatter);
    }
}

public class AsrFileTest {
    public static void main(String[] args) throws InterruptedException {
        // 以下为北京地域url，若使用新加坡地域的模型，需将url替换为：wss://dashscope-intl.aliyuncs.com/api-ws/v1/inference
        Constants.baseWebsocketApiUrl = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Path filepath = null;
        try {
            if (args != null && args.length > 0) {
                Path p = Paths.get(args[0]);
                if (java.nio.file.Files.exists(p)) {
                    filepath = p;
                }
            }
            if (filepath == null) {
                java.net.URL url = AsrFileTest.class.getClassLoader().getResource("aihuman/asr_example.wav");
                if (url != null) {
                    filepath = Paths.get(url.toURI());
                }
            }
            if (filepath == null) {
                Path p = Paths.get(System.getProperty("user.dir"), "ruoyi-modules", "ruoyi-aihuman", "src", "main", "resources", "aihuman", "asr_example.wav");
                if (java.nio.file.Files.exists(p)) {
                    filepath = p;
                }
            }
            if (filepath == null) {
                throw new java.io.FileNotFoundException("未找到 asr_example.wav，请通过参数传入或放置于模块 resources/aihuman 下");
            }
        } catch (Exception e) {
            System.err.println("[AsrFileTest] 解析音频文件失败: " + e.getMessage());
            System.exit(1);
        }
        executorService.submit(new RealtimeRecognitionTask(filepath));
        executorService.shutdown();

        // wait for all tasks to complete
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.exit(0);
    }
}

class RealtimeRecognitionTask implements Runnable {
    private Path filepath;

    public RealtimeRecognitionTask(Path filepath) {
        this.filepath = filepath;
    }

    @Override
    public void run() {
        RecognitionParam param = RecognitionParam.builder()
                .model("fun-asr-realtime")
                // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("sk-111d18189c244c22866b76f6c6e9189d")
                .format("wav")
                .sampleRate(16000)
                .build();
        Recognition recognizer = new Recognition();

        String threadName = Thread.currentThread().getName();

        ResultCallback<RecognitionResult> callback = new ResultCallback<RecognitionResult>() {
            @Override
            public void onEvent(RecognitionResult message) {
                if (message.isSentenceEnd()) {

                    System.out.println(TimeUtils.getTimestamp()+" "+
                            "[process " + threadName + "] Final Result:" + message.getSentence().getText());
                } else {
                    System.out.println(TimeUtils.getTimestamp()+" "+
                            "[process " + threadName + "] Intermediate Result: " + message.getSentence().getText());
                }
            }

            @Override
            public void onComplete() {
                System.out.println(TimeUtils.getTimestamp()+" "+"[" + threadName + "] Recognition complete");
            }

            @Override
            public void onError(Exception e) {
                System.out.println(TimeUtils.getTimestamp()+" "+
                        "[" + threadName + "] RecognitionCallback error: " + e.getMessage());
            }
        };

        try {
            recognizer.call(param, callback);
            // Please replace the path with your audio file path
            System.out.println(TimeUtils.getTimestamp()+" "+"[" + threadName + "] Input file_path is: " + this.filepath);
            // Read file and send audio by chunks
            FileInputStream fis = new FileInputStream(this.filepath.toFile());
            // chunk size set to 1 seconds for 16KHz sample rate
            byte[] buffer = new byte[3200];
            int bytesRead;
            // Loop to read chunks of the file
            while ((bytesRead = fis.read(buffer)) != -1) {
                ByteBuffer byteBuffer;
                // Handle the last chunk which might be smaller than the buffer size
                System.out.println(TimeUtils.getTimestamp()+" "+"[" + threadName + "] bytesRead: " + bytesRead);
                if (bytesRead < buffer.length) {
                    byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                } else {
                    byteBuffer = ByteBuffer.wrap(buffer);
                }

                recognizer.sendAudioFrame(byteBuffer);
                buffer = new byte[3200];
                Thread.sleep(100);
            }
            System.out.println(TimeUtils.getTimestamp()+" "+LocalDateTime.now());
            recognizer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 任务结束后关闭 Websocket 连接
            recognizer.getDuplexApi().close(1000, "bye");
        }

        System.out.println(
                "["
                        + threadName
                        + "][Metric] requestId: "
                        + recognizer.getLastRequestId()
                        + ", first package delay ms: "
                        + recognizer.getFirstPackageDelay()
                        + ", last package delay ms: "
                        + recognizer.getLastPackageDelay());
    }
}
