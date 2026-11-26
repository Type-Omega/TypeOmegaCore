package com.typeomega.core.integration.ae.upload;

import com.typeomega.core.TypeOmegaMod;

/** Messages of the automatic pattern upload: English here, pt_br and zh_cn in the lang files. */
public final class UploadLang {

    public static final String DONE = "typeomega.pattern_upload.done";
    public static final String DUPLICATE = "typeomega.pattern_upload.duplicate";
    public static final String AMBIGUOUS = "typeomega.pattern_upload.ambiguous";
    public static final String FULL = "typeomega.pattern_upload.full";
    // EN: The Wireless Comprehensive Work Terminal words its upload results with these two ExtendedAE Plus keys, which
    // EN: show as raw text without that mod; the same text here fills them.
    // PT: O Wireless Comprehensive Work Terminal escreve o resultado do upload com estas duas chaves do ExtendedAE
    // PT: Plus, que aparecem como texto cru sem esse mod; o mesmo texto aqui as preenche.
    // ZH: Wireless Comprehensive Work Terminal 用 ExtendedAE Plus 的这两个键显示上传结果，没有该模组时会显示原始键名；
    // ZH: 这里用同样含义的文本补上。
    public static final String WCWT_DONE = "extendedae_plus.screen.upload.auto_upload_success";
    public static final String WCWT_FAILED = "extendedae_plus.screen.upload.auto_upload_failed";

    private UploadLang() {}

    public static void init() {
        TypeOmegaMod.REGISTRATE.addRawLang(DONE, "Pattern sent to %s");
        TypeOmegaMod.REGISTRATE.addRawLang(DUPLICATE, "%s already holds this pattern; it stays in the terminal");
        TypeOmegaMod.REGISTRATE.addRawLang(AMBIGUOUS,
                "Several providers fit \"%s\" (%s); the pattern stays in the terminal. Name them apart to send it");
        TypeOmegaMod.REGISTRATE.addRawLang(FULL,
                "Every provider for \"%s\" is full; the pattern stays in the terminal");
        TypeOmegaMod.REGISTRATE.addRawLang(WCWT_DONE, "Pattern sent to %s");
        TypeOmegaMod.REGISTRATE.addRawLang(WCWT_FAILED, "Could not send the pattern to %s; it stays in the terminal");
    }
}
