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
import org.ruoyi.aihuman.domain.vo.AihumanActionPresetVo;
import org.ruoyi.aihuman.domain.bo.AihumanActionPresetBo;
import org.ruoyi.aihuman.service.AihumanActionPresetService;
import org.ruoyi.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ruoyi.aihuman.aliyun.AsrConfig;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import io.reactivex.Flowable;
import io.reactivex.BackpressureStrategy;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.ruoyi.aihuman.service.AihumanKeywordService;
import org.ruoyi.aihuman.domain.bo.AihumanKeywordBo;
import org.ruoyi.aihuman.domain.vo.AihumanKeywordVo;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;

/**
 * 关键词管理
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */

//临时免登录
@SaIgnore

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/aihuman/aihumanActionPreset")
public class AihumanActionPresetController extends BaseController {

    private final AihumanActionPresetService aihumanActionPresetService;
    private final AihumanKeywordService aihumanKeywordService;

/**
 * 查询关键词管理列表
 */
@SaCheckPermission("aihuman:aihumanActionPreset:list")
@GetMapping("/list")
    public TableDataInfo<AihumanActionPresetVo> list(AihumanActionPresetBo bo, PageQuery pageQuery) {
        return aihumanActionPresetService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出关键词管理列表
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:export")
    @Log(title = "关键词管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AihumanActionPresetBo bo, HttpServletResponse response) {
        List<AihumanActionPresetVo> list = aihumanActionPresetService.queryList(bo);
        ExcelUtil.exportExcel(list, "关键词管理", AihumanActionPresetVo.class, response);
    }

    @SaCheckPermission("aihuman:aihumanActionPreset:asr")
    @PostMapping("/asr/{id}")
    public R<Map<String, Object>> asr(@PathVariable String id, @RequestParam(required = false) MultipartFile audio, @RequestParam(required = false) String audioBase64) {
        AihumanActionPresetVo preset = aihumanActionPresetService.queryById(id);
        if (preset == null) {
            return R.fail("动作不存在");
        }
        String schema = preset.getParamsSchema();
        if (schema == null || schema.isEmpty()) {
            return R.fail("未配置ASR参数");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            AsrConfig config = mapper.readValue(schema, AsrConfig.class);
            if (config.getPlatform() == null || !"aliyun-asr".equalsIgnoreCase(config.getPlatform())) {
                return R.fail("不支持的 ASR 平台");
            }
            byte[] audioBytes;
            if (audio != null && !audio.isEmpty()) {
                audioBytes = audio.getBytes();
            } else if (audioBase64 != null && !audioBase64.isEmpty()) {
                audioBytes = java.util.Base64.getDecoder().decode(audioBase64);
            } else {
                return R.fail("缺少音频数据");
            }
            // Ensure PCM 16k/16bit/mono for aliyun-asr
            boolean looksLikeWav = audioBytes.length > 12 && new String(audioBytes, 0, 4, StandardCharsets.US_ASCII).equals("RIFF");
            if (config.getFormat() == null || config.getFormat().isBlank() || "pcm".equalsIgnoreCase(config.getFormat())) {
                config.setFormat("pcm");
                if (looksLikeWav) {
                    AsrConfig.Audio a = config.getAudio();
                    if (a == null) {
                        a = new AsrConfig.Audio();
                        a.setSampleRate(16000);
                        a.setSampleSizeInBits(16);
                        a.setChannels(1);
                        a.setSigned(true);
                        a.setBigEndian(false);
                        config.setAudio(a);
                    }
                    try {
                        AudioInputStream src = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));
                        AudioFormat target = new AudioFormat(a.getSampleRate(), a.getSampleSizeInBits(), a.getChannels(), true, false);
                        AudioInputStream pcm = AudioSystem.getAudioInputStream(target, src);
                        audioBytes = pcm.readAllBytes();
                    } catch (Exception e) {
                        return R.fail("音频格式转换失败，请上传 PCM 或 WAV(16k/16bit/mono)");
                    }
                }
            } else if (!"wav".equalsIgnoreCase(config.getFormat())) {
                return R.fail("不支持的音频格式，请上传 PCM 或 WAV");
            }
            final int chunkFinal = (config.getAudio() != null && config.getAudio().getBufferSize() != null) ? config.getAudio().getBufferSize() : 1024;
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
                return R.fail("ASR 接口调用失败");
            }
            Recognition recognizer = new Recognition();
            RecognitionParam param = RecognitionParam.builder()
                    .model(config.getModel())
                    .format(config.getFormat())
                    .sampleRate(config.getSampleRate())
                    .apiKey(apiKey)
                    .build();
            AtomicReference<String> finalText = new AtomicReference<>("");
            recognizer.streamCall(param, audioSource).blockingForEach(r -> {
                if (r.isSentenceEnd()) {
                    finalText.set(r.getSentence().getText());
                }
            });
            String text = finalText.get();
            AihumanKeywordBo kbo = new AihumanKeywordBo();
            kbo.setStatus("0");
            List<AihumanKeywordVo> kws = aihumanKeywordService.queryList(kbo);
            String matchKeyword = null;
            String matchActionCode = null;
            String matchActionName = null;
            boolean matched = false;
            for (AihumanKeywordVo kv : kws) {
                String kw = kv.getKeyword();
                String mode = kv.getMatchMode();
                boolean ok = false;
                if ("0".equals(mode)) ok = text.equals(kw);
                else if ("1".equals(mode)) ok = text.contains(kw);
                else if ("2".equals(mode)) {
                    try { ok = java.util.regex.Pattern.compile(kw).matcher(text).find(); } catch (Exception ignored) {}
                }
                else if ("3".equals(mode)) ok = text.startsWith(kw);
                else if ("4".equals(mode)) ok = text.endsWith(kw);
                if (ok) {
                    matchKeyword = kw;
                    matchActionCode = kv.getActionCode();
                    if (matchActionCode != null && !matchActionCode.isEmpty()) {
                        AihumanActionPresetBo abo = new AihumanActionPresetBo();
                        abo.setActionCode(matchActionCode);
                        List<AihumanActionPresetVo> list = aihumanActionPresetService.queryList(abo);
                        if (!list.isEmpty()) {
                            matchActionName = list.get(0).getName();
                        }
                    }
                    matched = true;
                    break;
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("result", text);
            data.put("match_keyword", matchKeyword);
            data.put("match_action_code", matchActionCode);
            data.put("match_action_name", matchActionName);
            data.put("is_match", matched);
            return R.ok(data);
        } catch (Exception e) {
            return R.fail("ASR 接口调用失败");
        }
    }

    /**
     * 获取关键词管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:query")
    @GetMapping("/{id}")
    public R<AihumanActionPresetVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aihumanActionPresetService.queryById(id));
    }

    /**
     * 新增关键词管理
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:add")
    @Log(title = "关键词管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AihumanActionPresetBo bo) {
        return toAjax(aihumanActionPresetService.insertByBo(bo));
    }

    /**
     * 修改关键词管理
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:edit")
    @Log(title = "关键词管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AihumanActionPresetBo bo) {
        return toAjax(aihumanActionPresetService.updateByBo(bo));
    }

    /**
     * 删除关键词管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:remove")
    @Log(title = "关键词管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aihumanActionPresetService.deleteWithValidByIds(List.of(ids), true));
    }
}
