package org.ruoyi.aihuman.controller;

import java.util.List;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.ruoyi.common.idempotent.annotation.RepeatSubmit;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.aihuman.domain.vo.AihumanConfigVo;
import org.ruoyi.aihuman.domain.bo.AihumanConfigBo;
import org.ruoyi.aihuman.service.AihumanConfigService;
import org.ruoyi.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.ruoyi.aihuman.aliyun.AsrConfig;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import io.reactivex.Flowable;
import io.reactivex.BackpressureStrategy;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.ruoyi.aihuman.service.AihumanKeywordService;
import org.ruoyi.aihuman.service.AihumanActionPresetService;
import org.ruoyi.aihuman.domain.bo.AihumanKeywordBo;
import org.ruoyi.aihuman.domain.vo.AihumanKeywordVo;
import org.ruoyi.aihuman.domain.bo.AihumanActionPresetBo;
import org.ruoyi.aihuman.domain.vo.AihumanActionPresetVo;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.ruoyi.aihuman.controller.AihumanVolcengineController;
import org.ruoyi.aihuman.domain.VoiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.ruoyi.aihuman.controller.AihumanVolcengineController;
import org.ruoyi.aihuman.domain.VoiceRequest;

/**
 * 交互数字人配置
 *
 * @author ageerle
 * @date Fri Sep 26 22:27:00 GMT+08:00 2025
 */

//临时免登录
@SaIgnore

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/aihuman/aihumanConfig")
public class AihumanConfigController extends BaseController {

    private final AihumanConfigService aihumanConfigService;
    private final AihumanKeywordService aihumanKeywordService;
    private final AihumanActionPresetService aihumanActionPresetService;
    @Autowired
    private AihumanVolcengineController volcengineController;

/**
 * 查询交互数字人配置列表
 */
@SaCheckPermission("aihuman:aihumanConfig:list")
@GetMapping("/list")
    public TableDataInfo<AihumanConfigVo> list(AihumanConfigBo bo, PageQuery pageQuery) {
        return aihumanConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出交互数字人配置列表
     */
    @SaCheckPermission("aihuman:aihumanConfig:export")
    @Log(title = "交互数字人配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AihumanConfigBo bo, HttpServletResponse response) {
        List<AihumanConfigVo> list = aihumanConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "交互数字人配置", AihumanConfigVo.class, response);
    }

    /**
     * 获取交互数字人配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("aihuman:aihumanConfig:query")
    @GetMapping("/{id}")
    public R<AihumanConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer id) {
        return R.ok(aihumanConfigService.queryById(id));
    }

    /**
     * 新增交互数字人配置
     */
    @SaCheckPermission("aihuman:aihumanConfig:add")
    @Log(title = "交互数字人配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AihumanConfigBo bo) {
        return toAjax(aihumanConfigService.insertByBo(bo));
    }

    /**
     * 修改交互数字人配置
     */
    @SaCheckPermission("aihuman:aihumanConfig:edit")
    @Log(title = "交互数字人配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AihumanConfigBo bo) {
        return toAjax(aihumanConfigService.updateByBo(bo));
    }

    /**
     * 删除交互数字人配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("aihuman:aihumanConfig:remove")
    @Log(title = "交互数字人配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] ids) {
        return toAjax(aihumanConfigService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 查询已发布的交互数字人配置列表
     * 只返回 publish = 1 的数据
     */
    @GetMapping("/publishedList")
    public TableDataInfo<AihumanConfigVo> publishedList(PageQuery pageQuery) {
        // 创建查询条件对象并设置publish=1
        AihumanConfigBo bo = new AihumanConfigBo();
        bo.setPublish(1);
        // 调用现有的查询方法，传入预设了publish=1条件的bo对象
        return aihumanConfigService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("aihuman:aihumanConfig:asr")
    @PostMapping("/asr/{id}")
    public R<Map<String, Object>> asr(@PathVariable Integer id, @RequestParam(required = false) MultipartFile audio, @RequestParam(required = false) String audioBase64) {
        AihumanConfigVo cfgVo = aihumanConfigService.queryById(id);
        if (cfgVo == null) return R.fail("配置不存在");
        try {
            ObjectMapper mapper = new ObjectMapper();
            AsrConfig asr;
            java.io.InputStream in = AihumanConfigController.class.getClassLoader().getResourceAsStream("aihuman/asr-config.json");
            if (in != null) asr = mapper.readValue(in, AsrConfig.class); else {
                asr = new AsrConfig();
                asr.setPlatform("aliyun-asr");
                asr.setModel("fun-asr-realtime");
                asr.setFormat("pcm");
                asr.setSampleRate(16000);
                AsrConfig.Audio a = new AsrConfig.Audio();
                a.setSampleRate(16000); a.setSampleSizeInBits(16); a.setChannels(1); a.setSigned(true); a.setBigEndian(false); a.setBufferSize(1024); a.setDurationMs(300000); a.setSleepMs(20);
                asr.setAudio(a);
            }
            if (asr.getPlatform() == null || !"aliyun-asr".equalsIgnoreCase(asr.getPlatform())) return R.fail("不支持的 ASR 平台");
            byte[] audioBytes;
            if (audio != null && !audio.isEmpty()) audioBytes = audio.getBytes(); else if (audioBase64 != null && !audioBase64.isEmpty()) audioBytes = java.util.Base64.getDecoder().decode(audioBase64); else return R.fail("缺少音频数据");
            boolean looksLikeWav = audioBytes.length > 12 && new String(audioBytes, 0, 4, StandardCharsets.US_ASCII).equals("RIFF");
            if (asr.getFormat() == null || asr.getFormat().isBlank() || "pcm".equalsIgnoreCase(asr.getFormat())) {
                asr.setFormat("pcm");
                if (looksLikeWav) {
                    AsrConfig.Audio a = asr.getAudio();
                    if (a == null) { a = new AsrConfig.Audio(); a.setSampleRate(16000); a.setSampleSizeInBits(16); a.setChannels(1); a.setSigned(true); a.setBigEndian(false); asr.setAudio(a); }
                    try {
                        AudioInputStream src = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));
                        AudioFormat target = new AudioFormat(a.getSampleRate(), a.getSampleSizeInBits(), a.getChannels(), true, false);
                        AudioInputStream pcm = AudioSystem.getAudioInputStream(target, src);
                        audioBytes = pcm.readAllBytes();
                    } catch (Exception e) { return R.fail("音频格式转换失败，请上传 PCM 或 WAV(16k/16bit/mono)"); }
                }
            } else if (!"wav".equalsIgnoreCase(asr.getFormat())) {
                return R.fail("不支持的音频格式，请上传 PCM 或 WAV");
            }

            final int chunkFinal = (asr.getAudio() != null && asr.getAudio().getBufferSize() != null) ? asr.getAudio().getBufferSize() : 1024;
            final byte[] audioBytesFinal = audioBytes;
            Flowable<ByteBuffer> audioSource = Flowable.create(emitter -> {
                        new Thread(() -> {
                            try {
                                int offset = 0;
                                while (offset < audioBytesFinal.length) {
                                    int len = Math.min(chunkFinal, audioBytesFinal.length - offset);
                                    ByteBuffer buffer = ByteBuffer.allocate(len);
                                    buffer.put(audioBytesFinal, offset, len);
                                    buffer.flip();
                                    emitter.onNext(buffer);
                                    offset += len;
                                }
                                emitter.onComplete();
                            } catch (Exception e) { emitter.onError(e); }
                        }).start();
                    }, BackpressureStrategy.BUFFER);

            String apiKey = asr.getApiKey();
            if (apiKey == null || apiKey.isBlank()) apiKey = System.getenv("DASHSCOPE_API_KEY");
            if (apiKey == null || apiKey.isBlank()) apiKey = System.getProperty("dashscope.api_key");
            if (apiKey == null || apiKey.isBlank()) return R.fail("ASR 接口调用失败");
            Recognition recognizer = new Recognition();
            RecognitionParam param = RecognitionParam.builder().model(asr.getModel()).format(asr.getFormat()).sampleRate(asr.getSampleRate()).apiKey(apiKey).build();
            AtomicReference<String> finalText = new AtomicReference<>("");
            StringBuilder allText = new StringBuilder();
            recognizer.streamCall(param, audioSource).blockingForEach(r -> {
                if (r.getSentence() != null && r.getSentence().getText() != null) {
                    allText.append(r.getSentence().getText());
                }
                if (r.isSentenceEnd()) {
                    finalText.set(r.getSentence().getText());
                }
            });
            String text = finalText.get();
            if (text == null || text.isBlank()) {
                text = allText.toString();
            }
            if (text == null) text = "";
            text = text.replaceAll("[\\p{Punct}，。！？、；：‘’“”()（）\\s]+", "");

            AihumanKeywordBo kbo = new AihumanKeywordBo(); kbo.setStatus("0"); kbo.setPublish("1"); kbo.setConfigId(String.valueOf(id));
            List<AihumanKeywordVo> kws = aihumanKeywordService.queryList(kbo);
            kws.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()));
            String matchKeyword = null, matchActionCode = null, matchActionName = null, expressionName = null, expressionPlatform = null, motionsName = null; String wakeUpText = null; String standByText = null;
            boolean matched = false;
            for (AihumanKeywordVo kv : kws) {
                String kw = kv.getKeyword(); String mode = kv.getMatchMode(); boolean ok = false;
                if ("0".equals(mode)) ok = text.equals(kw); else if ("1".equals(mode)) ok = text.contains(kw);
                else if ("2".equals(mode)) { try { ok = java.util.regex.Pattern.compile(kw).matcher(text).find(); } catch (Exception ignored) {} }
                else if ("3".equals(mode)) ok = text.startsWith(kw); else if ("4".equals(mode)) ok = text.endsWith(kw);
                if (ok) { matchKeyword = kw; matchActionCode = kv.getActionCode(); matched = true; break; }
            }
            if (!matched) {
                AihumanKeywordBo gb = new AihumanKeywordBo(); gb.setStatus("0"); gb.setPublish("1");
                List<AihumanKeywordVo> kwsGlobal = aihumanKeywordService.queryList(gb);
                kwsGlobal.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()));
                for (AihumanKeywordVo kv : kwsGlobal) {
                    String kw = kv.getKeyword(); String mode = kv.getMatchMode(); boolean ok = false;
                    if ("0".equals(mode)) ok = text.equals(kw); else if ("1".equals(mode)) ok = text.contains(kw);
                    else if ("2".equals(mode)) { try { ok = java.util.regex.Pattern.compile(kw).matcher(text).find(); } catch (Exception ignored) {} }
                    else if ("3".equals(mode)) ok = text.startsWith(kw); else if ("4".equals(mode)) ok = text.endsWith(kw);
                    if (ok) { matchKeyword = kw; matchActionCode = kv.getActionCode(); matched = true; break; }
                }
            }
            if (!matched) {
                Map<String, Object> data = new HashMap<>();
                data.put("result", text);
                data.put("match_keyword", "");
                data.put("match_action_code", "");
                data.put("match_action_name", "");
                data.put("expression_name", "");
                data.put("expression_platform", "");
                data.put("is_match", false);
                return R.fail("未匹配到关键词", data);
            }
            if (matchActionCode != null && !matchActionCode.isEmpty()) {
                AihumanActionPresetBo abo = new AihumanActionPresetBo(); abo.setActionCode(matchActionCode);
                List<AihumanActionPresetVo> list = aihumanActionPresetService.queryList(abo);
                if (!list.isEmpty()) matchActionName = list.get(0).getName();
            }
            if (cfgVo.getActionParams() != null && !cfgVo.getActionParams().isEmpty()) {
                try {
                    JsonNode root = new ObjectMapper().readTree(cfgVo.getActionParams());
                    if (root.has("platform")) expressionPlatform = root.get("platform").asText();
                    if (root.has("Expressions") && root.get("Expressions").isArray()) {
                        for (JsonNode n : root.get("Expressions")) {
                            String code = n.has("action_code") ? n.get("action_code").asText() : null;
                            if (code != null && code.equals(matchActionCode)) { expressionName = n.has("Name") ? n.get("Name").asText() : null; break; }
                        }
                    }
                    if (root.has("Motions") && root.get("Motions").isArray()) {
                        for (JsonNode n : root.get("Motions")) {
                            String code = n.has("action_code") ? n.get("action_code").asText() : null;
                            if (code != null && code.equals(matchActionCode)) { motionsName = n.has("Name") ? n.get("Name").asText() : null; break; }
                        }
                    }
                    if (root.has("WakeUp") && root.get("WakeUp").isObject()) {
                        JsonNode w = root.get("WakeUp");
                        String wakeCode = w.has("action_code") ? w.get("action_code").asText() : null;
                        if (wakeCode != null && wakeCode.equals(matchActionCode)) {
                            wakeUpText = w.has("responses") ? w.get("responses").asText() : null;
                        }
                    }
                    if (root.has("StandBy") && root.get("StandBy").isObject()) {
                        JsonNode s = root.get("StandBy");
                        String stopCode = s.has("action_code") ? s.get("action_code").asText() : null;
                        if (stopCode != null && stopCode.equals(matchActionCode)) {
                            standByText = s.has("responses") ? s.get("responses").asText() : null;
                        }
                    }
                } catch (Exception ignored) { }
            }
            if ((expressionName == null || expressionName.isEmpty()) && cfgVo.getAgentParams() != null && !cfgVo.getAgentParams().isEmpty()) {
                try {
                    JsonNode root = new ObjectMapper().readTree(cfgVo.getAgentParams());
                    if (root.has("platform")) expressionPlatform = root.get("platform").asText();
                    if (root.has("Expressions") && root.get("Expressions").isArray()) {
                        for (JsonNode n : root.get("Expressions")) {
                            String code = n.has("action_code") ? n.get("action_code").asText() : null;
                            if (code != null && code.equals(matchActionCode)) { expressionName = n.has("Name") ? n.get("Name").asText() : null; break; }
                        }
                    }
                    if (root.has("Motions") && root.get("Motions").isArray()) {
                        for (JsonNode n : root.get("Motions")) {
                            String code = n.has("action_code") ? n.get("action_code").asText() : null;
                            if (code != null && code.equals(matchActionCode)) { motionsName = n.has("Name") ? n.get("Name").asText() : null; break; }
                        }
                    }
                } catch (Exception ignored) { }
            }
            if (matchActionCode != null && !("start_interaction".equalsIgnoreCase(String.valueOf(matchActionCode)) || "stop_interaction".equalsIgnoreCase(String.valueOf(matchActionCode))) && ( (expressionName == null || expressionName.isEmpty()) && (motionsName == null || motionsName.isEmpty()) )) {
                log.warn("[ASR] 动作调用失败: actionCode={}, expressionName={}, motionsName={}, configId={}", matchActionCode, expressionName, motionsName, id);
                return R.fail("动作调用失败");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("result", text);
            data.put("match_keyword", matchKeyword);
            data.put("match_action_code", matchActionCode);
            data.put("match_action_name", matchActionName);
            data.put("expression_name", expressionName);
            data.put("expression_platform", expressionPlatform);
            data.put("motions_name", motionsName);
            data.put("is_match", true);
            if ("start_interaction".equalsIgnoreCase(String.valueOf(matchActionCode)) && wakeUpText != null && !wakeUpText.isEmpty()) {
                try {
                    if (cfgVo.getAgentParams() != null && !cfgVo.getAgentParams().isEmpty()) {
                        JsonNode agentRoot = new ObjectMapper().readTree(cfgVo.getAgentParams());
                        JsonNode voiceCfg = agentRoot.has("voiceConfig") ? agentRoot.get("voiceConfig") : null;
                        if (voiceCfg != null) {
                            VoiceRequest vr = new VoiceRequest();
                            vr.setEndpoint(voiceCfg.has("ENDPOINT") ? voiceCfg.get("ENDPOINT").asText() : null);
                            vr.setAppId(voiceCfg.has("appId") ? voiceCfg.get("appId").asText() : null);
                            vr.setAccessToken(voiceCfg.has("accessToken") ? voiceCfg.get("accessToken").asText() : null);
                            vr.setResourceId(voiceCfg.has("resourceId") ? voiceCfg.get("resourceId").asText() : null);
                            vr.setVoice(voiceCfg.has("voice") ? voiceCfg.get("voice").asText() : null);
                            vr.setEncoding(voiceCfg.has("encoding") ? voiceCfg.get("encoding").asText() : "wav");
                            vr.setText(wakeUpText);
                            org.springframework.http.ResponseEntity<?> resp = volcengineController.generateVoice(vr);
                            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() instanceof java.util.Map) {
                                Object url = ((java.util.Map<?,?>) resp.getBody()).get("audioUrl");
                                if (url != null) data.put("audioUrl", String.valueOf(url));
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
            if ("stop_interaction".equalsIgnoreCase(String.valueOf(matchActionCode)) && standByText != null && !standByText.isEmpty()) {
                try {
                    if (cfgVo.getAgentParams() != null && !cfgVo.getAgentParams().isEmpty()) {
                        JsonNode agentRoot = new ObjectMapper().readTree(cfgVo.getAgentParams());
                        JsonNode voiceCfg = agentRoot.has("voiceConfig") ? agentRoot.get("voiceConfig") : null;
                        if (voiceCfg != null) {
                            VoiceRequest vr = new VoiceRequest();
                            vr.setEndpoint(voiceCfg.has("ENDPOINT") ? voiceCfg.get("ENDPOINT").asText() : null);
                            vr.setAppId(voiceCfg.has("appId") ? voiceCfg.get("appId").asText() : null);
                            vr.setAccessToken(voiceCfg.has("accessToken") ? agentRoot.get("voiceConfig").get("accessToken").asText() : null);
                            vr.setResourceId(voiceCfg.has("resourceId") ? voiceCfg.get("resourceId").asText() : null);
                            vr.setVoice(voiceCfg.has("voice") ? voiceCfg.get("voice").asText() : null);
                            vr.setEncoding(voiceCfg.has("encoding") ? voiceCfg.get("encoding").asText() : "wav");
                            vr.setText(standByText);
                            org.springframework.http.ResponseEntity<?> resp = volcengineController.generateVoice(vr);
                            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() instanceof java.util.Map) {
                                Object url = ((java.util.Map<?,?>) resp.getBody()).get("audioUrl");
                                if (url != null) data.put("audioUrl", String.valueOf(url));
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
            return R.ok(data);
        } catch (Exception e) { return R.fail("ASR 接口调用失败"); }
    }

    @SaCheckPermission("aihuman:aihumanConfig:asr")
    @PostMapping("/asr/voice/{id}")
    public org.springframework.http.ResponseEntity<byte[]> asrVoice(@PathVariable Integer id, @RequestParam(required = false) MultipartFile audio, @RequestParam(required = false) String audioBase64) {
        AihumanConfigVo cfgVo = aihumanConfigService.queryById(id);
        if (cfgVo == null) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
        try {
            ObjectMapper mapper = new ObjectMapper();
            AsrConfig asr;
            java.io.InputStream in = AihumanConfigController.class.getClassLoader().getResourceAsStream("aihuman/asr-config.json");
            if (in != null) asr = mapper.readValue(in, AsrConfig.class); else {
                asr = new AsrConfig();
                asr.setPlatform("aliyun-asr");
                asr.setModel("fun-asr-realtime");
                asr.setFormat("pcm");
                asr.setSampleRate(16000);
                AsrConfig.Audio a = new AsrConfig.Audio();
                a.setSampleRate(16000); a.setSampleSizeInBits(16); a.setChannels(1); a.setSigned(true); a.setBigEndian(false); a.setBufferSize(1024); a.setDurationMs(300000); a.setSleepMs(20);
                asr.setAudio(a);
            }
            if (asr.getPlatform() == null || !"aliyun-asr".equalsIgnoreCase(asr.getPlatform())) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
            byte[] audioBytes;
            if (audio != null && !audio.isEmpty()) audioBytes = audio.getBytes(); else if (audioBase64 != null && !audioBase64.isEmpty()) audioBytes = java.util.Base64.getDecoder().decode(audioBase64); else return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
            boolean looksLikeWav = audioBytes.length > 12 && new String(audioBytes, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF");
            if (asr.getFormat() == null || asr.getFormat().isBlank() || "pcm".equalsIgnoreCase(asr.getFormat())) {
                asr.setFormat("pcm");
                if (looksLikeWav) {
                    AsrConfig.Audio a = asr.getAudio();
                    if (a == null) { a = new AsrConfig.Audio(); a.setSampleRate(16000); a.setSampleSizeInBits(16); a.setChannels(1); a.setSigned(true); a.setBigEndian(false); asr.setAudio(a); }
                    try {
                        javax.sound.sampled.AudioInputStream src = javax.sound.sampled.AudioSystem.getAudioInputStream(new java.io.ByteArrayInputStream(audioBytes));
                        javax.sound.sampled.AudioFormat target = new javax.sound.sampled.AudioFormat(a.getSampleRate(), a.getSampleSizeInBits(), a.getChannels(), true, false);
                        javax.sound.sampled.AudioInputStream pcm = javax.sound.sampled.AudioSystem.getAudioInputStream(target, src);
                        audioBytes = pcm.readAllBytes();
                    } catch (Exception e) { return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST); }
                }
            } else if (!"wav".equalsIgnoreCase(asr.getFormat())) {
                return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
            }
            final int chunkFinal = (asr.getAudio() != null && asr.getAudio().getBufferSize() != null) ? asr.getAudio().getBufferSize() : 1024;
            final byte[] audioBytesFinal = audioBytes;
            io.reactivex.Flowable<java.nio.ByteBuffer> audioSource = io.reactivex.Flowable.create(emitter -> {
                        new Thread(() -> {
                            try {
                                int offset = 0;
                                while (offset < audioBytesFinal.length) {
                                    int len = Math.min(chunkFinal, audioBytesFinal.length - offset);
                                    java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(len);
                                    buffer.put(audioBytesFinal, offset, len);
                                    buffer.flip();
                                    emitter.onNext(buffer);
                                    offset += len;
                                }
                                emitter.onComplete();
                            } catch (Exception e) { emitter.onError(e); }
                        }).start();
                    }, io.reactivex.BackpressureStrategy.BUFFER);
            String apiKey = asr.getApiKey();
            if (apiKey == null || apiKey.isBlank()) apiKey = System.getenv("DASHSCOPE_API_KEY");
            if (apiKey == null || apiKey.isBlank()) apiKey = System.getProperty("dashscope.api_key");
            if (apiKey == null || apiKey.isBlank()) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
            com.alibaba.dashscope.audio.asr.recognition.Recognition recognizer = new com.alibaba.dashscope.audio.asr.recognition.Recognition();
            com.alibaba.dashscope.audio.asr.recognition.RecognitionParam param = com.alibaba.dashscope.audio.asr.recognition.RecognitionParam.builder().model(asr.getModel()).format(asr.getFormat()).sampleRate(asr.getSampleRate()).apiKey(apiKey).build();
            java.util.concurrent.atomic.AtomicReference<String> finalText = new java.util.concurrent.atomic.AtomicReference<>("");
            StringBuilder allText = new StringBuilder();
            recognizer.streamCall(param, audioSource).blockingForEach(r -> {
                if (r.getSentence() != null && r.getSentence().getText() != null) {
                    allText.append(r.getSentence().getText());
                }
                if (r.isSentenceEnd()) {
                    finalText.set(r.getSentence().getText());
                }
            });
            String text = finalText.get();
            if (text == null || text.isBlank()) {
                text = allText.toString();
            }
            if (text == null) text = "";
            text = text.replaceAll("[\\p{Punct}，。！？、；：‘’“”()（）\\s]+", "");
            AihumanKeywordBo kbo = new AihumanKeywordBo(); kbo.setStatus("0"); kbo.setPublish("1"); kbo.setConfigId(String.valueOf(id));
            List<AihumanKeywordVo> kws = aihumanKeywordService.queryList(kbo);
            kws.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()));
            String matchActionCode = null; boolean matched = false; String wakeUpText = null; String standByText = null;
            for (AihumanKeywordVo kv : kws) {
                String kw = kv.getKeyword(); String mode = kv.getMatchMode(); boolean ok = false;
                if ("0".equals(mode)) ok = text.equals(kw); else if ("1".equals(mode)) ok = text.contains(kw);
                else if ("2".equals(mode)) { try { ok = java.util.regex.Pattern.compile(kw).matcher(text).find(); } catch (Exception ignored) {} }
                else if ("3".equals(mode)) ok = text.startsWith(kw); else if ("4".equals(mode)) ok = text.endsWith(kw);
                if (ok) { matchActionCode = kv.getActionCode(); matched = true; break; }
            }
            if (!matched) {
                AihumanKeywordBo gb = new AihumanKeywordBo(); gb.setStatus("0"); gb.setPublish("1");
                List<AihumanKeywordVo> kwsGlobal = aihumanKeywordService.queryList(gb);
                kwsGlobal.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()));
                for (AihumanKeywordVo kv : kwsGlobal) {
                    String kw = kv.getKeyword(); String mode = kv.getMatchMode(); boolean ok = false;
                    if ("0".equals(mode)) ok = text.equals(kw); else if ("1".equals(mode)) ok = text.contains(kw);
                    else if ("2".equals(mode)) { try { ok = java.util.regex.Pattern.compile(kw).matcher(text).find(); } catch (Exception ignored) {} }
                    else if ("3".equals(mode)) ok = text.startsWith(kw); else if ("4".equals(mode)) ok = text.endsWith(kw);
                    if (ok) { matchActionCode = kv.getActionCode(); matched = true; break; }
                }
            }
            if (!matched) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
            if ("start_interaction".equalsIgnoreCase(String.valueOf(matchActionCode))) {
                if (cfgVo.getActionParams() != null && !cfgVo.getActionParams().isEmpty()) {
                    try {
                        JsonNode root = new ObjectMapper().readTree(cfgVo.getActionParams());
                        if (root.has("WakeUp") && root.get("WakeUp").isObject()) {
                            JsonNode w = root.get("WakeUp");
                            String wakeCode = w.has("action_code") ? w.get("action_code").asText() : null;
                            if (wakeCode != null && wakeCode.equals(matchActionCode)) {
                                wakeUpText = w.has("responses") ? w.get("responses").asText() : null;
                            }
                        }
                    } catch (Exception ignored) { }
                }
                if (wakeUpText == null || wakeUpText.isEmpty()) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
                if (cfgVo.getAgentParams() != null && !cfgVo.getAgentParams().isEmpty()) {
                    JsonNode agentRoot = new ObjectMapper().readTree(cfgVo.getAgentParams());
                    JsonNode voiceCfg = agentRoot.has("voiceConfig") ? agentRoot.get("voiceConfig") : null;
                    if (voiceCfg != null) {
                        VoiceRequest vr = new VoiceRequest();
                        vr.setEndpoint(voiceCfg.has("ENDPOINT") ? voiceCfg.get("ENDPOINT").asText() : null);
                        vr.setAppId(voiceCfg.has("appId") ? voiceCfg.get("appId").asText() : null);
                        vr.setAccessToken(voiceCfg.has("accessToken") ? voiceCfg.get("accessToken").asText() : null);
                        vr.setResourceId(voiceCfg.has("resourceId") ? voiceCfg.get("resourceId").asText() : null);
                        vr.setVoice(voiceCfg.has("voice") ? voiceCfg.get("voice").asText() : null);
                        vr.setEncoding(voiceCfg.has("encoding") ? voiceCfg.get("encoding").asText() : "wav");
                        vr.setText(wakeUpText);
                        org.springframework.http.ResponseEntity<byte[]> resp = volcengineController.generateVoiceDirect(vr);
                        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                            String enc = vr.getEncoding() != null ? vr.getEncoding() : "wav";
                            if ("wav".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/wav"));
                            else if ("mp3".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/mpeg"));
                            else if ("ogg".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/ogg"));
                            else headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                            headers.setContentLength(resp.getBody().length);
                            headers.add("Accept-Ranges", "bytes");
                            headers.add("Access-Control-Allow-Origin", "*");
                            return new org.springframework.http.ResponseEntity<>(resp.getBody(), headers, org.springframework.http.HttpStatus.OK);
                        }
                    }
                }
                return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if ("stop_interaction".equalsIgnoreCase(String.valueOf(matchActionCode))) {
                if (cfgVo.getActionParams() != null && !cfgVo.getActionParams().isEmpty()) {
                    try {
                        JsonNode root = new ObjectMapper().readTree(cfgVo.getActionParams());
                        if (root.has("StandBy") && root.get("StandBy").isObject()) {
                            JsonNode s = root.get("StandBy");
                            String stopCode = s.has("action_code") ? s.get("action_code").asText() : null;
                            if (stopCode != null && stopCode.equals(matchActionCode)) {
                                standByText = s.has("responses") ? s.get("responses").asText() : null;
                            }
                        }
                    } catch (Exception ignored) { }
                }
                if (standByText == null || standByText.isEmpty()) return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
                if (cfgVo.getAgentParams() != null && !cfgVo.getAgentParams().isEmpty()) {
                    JsonNode agentRoot = new ObjectMapper().readTree(cfgVo.getAgentParams());
                    JsonNode voiceCfg = agentRoot.has("voiceConfig") ? agentRoot.get("voiceConfig") : null;
                    if (voiceCfg != null) {
                        VoiceRequest vr = new VoiceRequest();
                        vr.setEndpoint(voiceCfg.has("ENDPOINT") ? voiceCfg.get("ENDPOINT").asText() : null);
                        vr.setAppId(voiceCfg.has("appId") ? voiceCfg.get("appId").asText() : null);
                        vr.setAccessToken(voiceCfg.has("accessToken") ? voiceCfg.get("accessToken").asText() : null);
                        vr.setResourceId(voiceCfg.has("resourceId") ? voiceCfg.get("resourceId").asText() : null);
                        vr.setVoice(voiceCfg.has("voice") ? voiceCfg.get("voice").asText() : null);
                        vr.setEncoding(voiceCfg.has("encoding") ? voiceCfg.get("encoding").asText() : "wav");
                        vr.setText(standByText);
                        org.springframework.http.ResponseEntity<byte[]> resp = volcengineController.generateVoiceDirect(vr);
                        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                            String enc = vr.getEncoding() != null ? vr.getEncoding() : "wav";
                            if ("wav".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/wav"));
                            else if ("mp3".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/mpeg"));
                            else if ("ogg".equalsIgnoreCase(enc)) headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/ogg"));
                            else headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                            headers.setContentLength(resp.getBody().length);
                            headers.add("Accept-Ranges", "bytes");
                            headers.add("Access-Control-Allow-Origin", "*");
                            return new org.springframework.http.ResponseEntity<>(resp.getBody(), headers, org.springframework.http.HttpStatus.OK);
                        }
                    }
                }
                return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
