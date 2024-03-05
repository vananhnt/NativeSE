package android.media.effect.effects;

import android.filterpacks.imageproc.VignetteFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

/* loaded from: VignetteEffect.class */
public class VignetteEffect extends SingleFilterEffect {
    public VignetteEffect(EffectContext context, String name) {
        super(context, name, VignetteFilter.class, "image", "image", new Object[0]);
    }
}