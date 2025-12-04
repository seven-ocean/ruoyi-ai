package org.ruoyi.aihuman.aliyun;

import lombok.Data;

@Data
public class AsrConfig {
    private String platform;
    private String apiKey;
    private String model;
    private String format;
    private Integer sampleRate;
    private Audio audio;

    @Data
    public static class Audio {
        private Integer sampleRate;
        private Integer sampleSizeInBits;
        private Integer channels;
        private Boolean signed;
        private Boolean bigEndian;
        private Integer bufferSize;
        private Integer durationMs;
        private Integer sleepMs;
    }
}
