package com.typeomega.core.integration.ae.upload;

import org.jetbrains.annotations.Nullable;

/** Implemented by AE2's pattern encoding menu through a mixin: the key the client sent with the next Encode. */
public interface UploadRequestHolder {

    void typeomega$setUploadKey(@Nullable RecipeKey key);

    /** The key of the Encode in progress, removed; null when the client sent none. */
    @Nullable
    RecipeKey typeomega$takeUploadKey();
}
